package server_manager;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.sql.Connection;
import java.sql.SQLException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import server_util.*;
import crypto.AES;
import crypto.RSA;

public class SocketBinder {
	private MultiroomServer server; //연결 서버
	private Socket telSocket; //통신용 소켓
	
	private final String sessionKey = AES.generateIV(); //암호화 통신용
	private boolean enableSKey = false; //세션키 활성화 정보 
	
	private DataInputStream dis; //receive
	private DataOutputStream dos; //send
	
	//연결 후 서버가 다음 요청까지 대기하는 시간(sec)
	private int waitTime = 60;
	
	private String clientIp; //클라이언트 ip 정보
	
	public SocketBinder() {} //테스트용
	public SocketBinder(MultiroomServer server, Socket telSocket) {
		try {
			this.server = server;
			this.telSocket = telSocket;	
			this.dis = new DataInputStream(telSocket.getInputStream());
			this.dos = new DataOutputStream(telSocket.getOutputStream());
			
			//클라이언트 데이터
			InetSocketAddress isa = (InetSocketAddress)telSocket.getRemoteSocketAddress();
			this.clientIp = isa.getAddress().toString();
			
			//데이터 읽기 준비
			receive(); 
		}catch(IOException e) {
			System.out.println();
		}catch(Exception e) {
			System.out.println("[예외] : "+e.getMessage());
		}
	}
	
	//소켓 연결 닫기
	public void close() {
		try {
			enableSKey = false;
			if(telSocket!=null || !telSocket.isClosed())
				telSocket.close();
		}catch(Exception e) {}
	}
	
	/************************* 송수신 처리 ******************************/
	
	//암호화 통신 설정
	private void setupCryptoSession() throws Exception {
		STimer timer = new STimer(()->{
			close(); //연결 끊기
		});
		timer.start(3); //연결 설정이 3초 이상 지연 시 연결을 끊음
		
		System.out.printf("[server] %s와의 암호화 통신 설정중...\n",this.clientIp);
		//서버 -> 클라이언트 공개키 전송
		String serverPublicKey = server.cryptoManager.getPublicKey();
		JSONObject temp = new JSONObject();
		temp.put("status","connecting");
		temp.put("server_info","Multiroom Server");
		temp.put("message","Send a publicKey HERE(Name is publicKey)");
		JSONObject json = new JSONObject();
		json.put("header", temp);
		json.put("publicKey", serverPublicKey);
		sendToClient(json.toJSONString());
		
		//클라이언트 -> 서버 클라이언트 공개키로 암호화
		String data = dis.readUTF();
		JSONParser parser = new JSONParser();
		json=(JSONObject)parser.parse(data);
		String publicKey = json.get("publicKey").toString();
		
		//서버 -> 클라이언트 암호화된 세션키 전송
		String encryptSKey=RSA.encrypt(sessionKey, publicKey);
		temp = new JSONObject();
		temp.put("status","connecting");
		temp.put("server_info","Multiroom Server");
		temp.put("message","Send a response HERE(status, session_key_received)");
		json = new JSONObject();
		json.put("header", temp);
		json.put("sessionKey", encryptSKey);
		sendToClient(json.toJSONString());
		
		//클라이언트 -> 서버 응답 확인 및 세션키 활성화
		data = dis.readUTF();
		json=(JSONObject)parser.parse(data);
		String status = json.get("status").toString();
		if(status.equals("session_key_received")) {
			enableSKey = true;
			System.out.println("[server] 암호화 통신 설정됨");
		}
		//시간 안에 수행 시 타이머 삭제
		timer.delete();
	}
	
	//데이터 받기
	private void receive(){
		server.addTaskInThreadPool(()->{
			try {
				setupCryptoSession();
				if(!enableSKey)
					throw new IOException("[예외] 암호화 연결 실패");
				while(true) {
					STimer timer = new STimer(()->{
						try { telSocket.close(); }
						catch(Exception e){} 
					});
					timer.start(waitTime); //다음 요청까지 60초 간 대기합니다.
					String receiveData = dis.readUTF();
					timer.delete();
					CmdProcessor cp = new CmdProcessor(receiveData,this);
					String response = cp.executor(); //실행
					sendToClient(response);
				}
			}catch(IOException e) {
				System.out.println("[예외] 소켓 연결 끊김 : "+clientIp);
			}catch(Exception e) {
				System.out.print("[예외] 명령어 처리 중 오류 : ");
				System.out.println(e.getMessage());
			}finally {
				server.removeSocket(this);
				close();
			}
		});
	}
	
	//데이터 보내기
	public void sendToClient(String json) throws Exception {
		try {
			dos.writeUTF(json); 
			dos.flush();
		}catch(IOException e) {
			System.out.println();
		}
	}
	
	/************************* 파일 송/수신 ******************************/
	//1. 파일 전송
	//파일에 대한 메타 데이터를 생성합니다.
	public String createMetaFromFile(File[] files, int bufferSize) {
		if(files==null)
			return null;
		int fileCnt=files.length;
		JSONObject json = new JSONObject();
		JSONArray fileNames = new JSONArray();
		JSONArray fileSizes = new JSONArray();
		for(File file : files) {
			String fileName=file.getName();
			long fileSize = file.length();
			fileNames.add(fileName);
			fileSizes.add(fileSize);
		}
		json.put("fileCount", fileCnt); //파일 개수(int)
		json.put("fileNames", fileNames); //파일 이름 JSONArray-(String)
		json.put("fileSizes", fileSizes); //파일 길이 JSONArray-(long)
		json.put("bufferSize", bufferSize); //버퍼 크기(int)
		return json.toJSONString();
	}
	
	//클라이언트로 파일들을 전송
	public void sendFiles(File[] files, int bufferSize) throws Exception {
		for(File file : files) {
			sendFileToClient(file, bufferSize);
		}
	}
	
	//클라이언트 파일 전송
	private void sendFileToClient(File file, int bufferSize) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[bufferSize];
		int bytesRead;
		while((bytesRead = fis.read(buffer))!= -1) {
			dos.write(buffer, 0,bytesRead);
		}
		dos.flush();
		fis.close();
	}
	
	//2. 파일 수신
	
	//메타 데이터 수신
	public String receiveMetaData() throws Exception{
		//메타 데이터 수신
		String receivedData = dis.readUTF();
		String originData=decryptReceivedData(receivedData);
		JSONParser parser = new JSONParser();
		String metaData 
			= (String)((JSONObject)parser.parse(originData)).get("metaData");
		return metaData;	
	}
	
	//파일 수신
	public byte[] receiveFilesFromClient(long size, int bufferSize) throws Exception{
		byte[] file = new byte[(int)size];
		byte[] buffer = new byte[bufferSize];
		int bytesRead; //읽은 바이트 수
		int totalBytesRead = 0; //전체 읽은 바이트 수
		//파일 읽기
		while(totalBytesRead < size && 
			(bytesRead = dis.read(buffer,0,
				(int)Math.min(bufferSize, size-totalBytesRead)))!=-1) {
			//버퍼에서 읽은 바이트 크기만큼 file에 바이트를 복사합니다.
			System.arraycopy(buffer, 0, file, totalBytesRead, bytesRead);
			totalBytesRead += bytesRead;
		}
		return file;
	}
	
	/************************* 세션키 AES 암/복호화 ******************************/
	public String encryptSendData(String originData) throws Exception {
		String vi = AES.generateIV();
		String encryptData = AES.encrypt(sessionKey,vi, originData);
		return vi+encryptData;
	}
	
	String decryptReceivedData(String receivedData) throws Exception{
		String iv = receivedData.substring(0,16);
		String encryptedData = receivedData.substring(16);
		String result=AES.decrypt(sessionKey,iv, encryptedData);
		return result;
	}
	
	/************************* DB 커넥션 ******************************/
	public Connection getConnection() {
		int delaySec = 2000; //2초 뒤 재시도
		int maxTryCnt = 3; //최대 3번까지 시도
		
		for(int i=0;i<maxTryCnt;++i) {
			try {
				Connection conn=server.dbManager.getConnection();
				return conn;
			}catch(SQLException e) {
				if (e.getMessage().contains("timeout")) {
	                System.out.println("[DB] 커넥션 타임아웃, 재연결 시도..."+"("+(i+1)+")");
	                try {
	                	Thread.sleep(delaySec);
	                }catch(InterruptedException el) { }
	                if(i+1<maxTryCnt)
	                	continue;
	                else
	                	System.out.println("[예외] 커넥션 연결 실패");
				}
	            else {
	            	System.out.println("[예외]" + e.getMessage());
	            	break;
	            }
			}catch(Exception e) {
				System.out.println("[예외] : "+e.getMessage());
				break;
			}
		}
		return null;
	}
	
	/************************* 세션 관리 ******************************/
	/*명령 처리기에서는 SessionManager에 직접 접근하지 않고 메서드로 세션을 생성하거나 가져와서 처리*/
	
	//세션 새로 생성하기 -> 세션ID 반환
	public String createSession(long expiry) { 
		Session session = new Session(1000*expiry); 
		String sessionId=server.sessionManager.generateSId();
		server.sessionManager.addSession(sessionId, session);
		return sessionId;
	}
	
	//기존에 존재하는 세션 가져오기 -> Session | null 반환
	public Session getSession(String sessionId) {
		return server.sessionManager.getSession(sessionId);
	}
	
	//세션 제거
	public void removeSession(String sessionId) {
		server.sessionManager.removeSession(sessionId);
	}
	
	/************************* 접속 소켓 정보 ******************************/
	public String getClientIp() {
		return clientIp;
	}
}