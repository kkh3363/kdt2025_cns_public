package MultiRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

import org.json.simple.JSONArray;
//jsonSimple Parser
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MultiRoom.FileManager.PATH;
import crypto.*;

public class ClientConnector {
	
	private static String ipAddress;
	private static int portNumber;
	
	private Socket socket; 
	private DataInputStream dis; //서버로부터 받은 데이터 읽기
	private DataOutputStream dos; //서버로 데이터 전송
	
	private String sessionKey = null;
	private CryptoManager cryptoManager = null;
	private ExceptionHandler exceptionHandler = null; //예외처리 핸들러
	
	private HashMap<String, Object> store = null;
	//객체 사용 도중에 사용할 임시 저장소
	
	//파일에서 가져오기
	static {
		File file = FileManager.getFile(PATH.ROOT,"connection/connect.txt");
		try {
            Scanner sc = new Scanner(file);
            if(sc.hasNextLine()) {
            	String line = sc.nextLine();
            	JSONParser parser = new JSONParser();
            	JSONObject jsonObj = (JSONObject)parser.parse(line);
            	ipAddress = jsonObj.get("ip").toString();
            	portNumber = Integer.parseInt(jsonObj.get("port").toString());
            	System.out.println(ipAddress+"::"+portNumber);
            }else
            	throw new Exception("");
        } catch (Exception e) {
        	ipAddress = "localhost";
    		portNumber = 50001;
            System.out.println(e.getMessage());
        }
	}
	
	public ClientConnector() {
		super();
		store=new HashMap<>();
	}
	
	/************************* 인터페이스 ******************************/
	//응답 처리 인터페이스 
	// -> 클라이언트 개발하시는 분이 구현해서 사용하시면 됩니다.
	public interface ResponseHandler {
        void handleResponse(String response) throws Exception;
    }
	
	//송신하고 받을 때 발생하는 예외처리 인터페이스
	public interface ExceptionHandler {
        void handleException(Exception e);
    }
	
	//예외처리 등록기
	public void setExceptionHandler(ExceptionHandler handler) {
		this.exceptionHandler = handler;
	}
	
	/************************* 커넥션 설정 ******************************/
	//서버 연결
	public void connect() throws Exception{
		socket = new Socket(ipAddress, portNumber);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		cryptoManager = new CryptoManager();
		try {
			setupCryptoSession();
		}catch(Exception e) {
			System.out.println("[에러] : 암호화 통신 설정 실패");
			disconnect();
		}
	}
	
	//소켓 연결 끊기
	public void disconnect(){
		try {
			if(store!=null)
				store.clear();
			sessionKey = null;
			exceptionHandler = null;
			if(!socket.isClosed())
				socket.close();
		}catch(Exception e) { }
	}
	
	//암호화 통신 설정 과정
	private void setupCryptoSession() throws Exception{
		STimer timer = new STimer(()->{
			disconnect();
		});
		timer.start(3); //연결 설정이 3초 이상 지연 시 연결을 끊음
		
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		//첫번째, 서버 공개키 및 인증서 확인 
		String receiveData = dis.readUTF(); 
		obj=(JSONObject)parser.parse(receiveData);
		JSONObject header = (JSONObject)obj.get("header");
		System.out.println("[상태] : "+header.get("status").toString());
		System.out.println("[서버 정보] : "+header.get("server_info").toString());
		System.out.println("[메시지] : "+header.get("message").toString());
		
		//두번째, 클라이언트 공개키 전송
		obj = new JSONObject();
		obj.put("publicKey", cryptoManager.getPublicKey());
		send(obj.toJSONString());
		
		//세번째, 세션키 저장
		receiveData = dis.readUTF();
		obj=(JSONObject)parser.parse(receiveData);
		header = (JSONObject)obj.get("header");
		System.out.println("[메시지] : "+header.get("message").toString());
		String encryptSKey=obj.get("sessionKey").toString();
		sessionKey = cryptoManager.decryptText(encryptSKey);
		
		//네번째, 응답 전송 
		obj = new JSONObject();
		String response = (sessionKey!=null)?"session_key_received":"fail";
		obj.put("status", response);
		send(obj.toJSONString());
		
		timer.delete();
	}
	
	/************************* 메시지 송수신 설정 ******************************/
	
	//메시지 보내기
	public void send(String json) throws IOException {
		dos.writeUTF(json);
		dos.flush();
	}
	
	//요청 및 응답 데이터 동기화 처리 -> 핸들러 부분에서 처리할 메서드를 구현합니다.
	public void requestAndReponseSync(String command, String requestData, ResponseHandler responseHandler) {
		try {
			if(socket.isConnected()) {
				//응답 연결 지연 시 방안
				STimer timer = new STimer(()->{
					disconnect();
				});
				
				JSONObject json = new JSONObject();
				json.put("command", command);
				json.put("data", requestData);
				String sendData=encryptSendData(json.toJSONString());
				send(sendData);
				timer.start(5);
				
				//서버로부터 응답 수신
				String response = dis.readUTF();
				
				//타이머 삭제
				timer.delete();
				responseHandler.handleResponse(response);
			}
			else
				throw new Exception("소켓 연결 끊김"); 
		}catch(Exception e) {
			if (exceptionHandler != null) {
                exceptionHandler.handleException(e); //예외를 핸들러로 전달
            }else {
            	System.out.println("[예외] : "+e.getMessage());
            }
		}
	}
	
	//위의 동작을 비동기로 수행하는 코드(별도의 쓰레드로 처리)
	public CompletableFuture<Void> requestAndReponseASync(String command, String requestData, ResponseHandler responseHandler) {
		return CompletableFuture.runAsync(()->{
			try {
				if(socket.isConnected()) {
					STimer timer = new STimer(()->{
						disconnect();
					});
					
					JSONObject json = new JSONObject();
					json.put("command", command);
					json.put("data", requestData);
					String sendData=encryptSendData(json.toJSONString());
					send(sendData);
					timer.start(5);
					
					//서버로부터 응답 수신
					String response = dis.readUTF();
					
					//타이머 삭제
					timer.delete();
					responseHandler.handleResponse(response);
				}
				else
					throw new Exception("소켓 연결 끊김");
			}catch(Exception e) {
				if (exceptionHandler != null) {
	                exceptionHandler.handleException(e);
	            }else {
	            	System.out.println("[예외] : "+e.getMessage());
	            }
			}
		});
	}	
	
	/************************* 파일 송/수신 ******************************/
	//1. 파일 수신
	//서버로부터 ready_receive와 메타데이터를 받으면
	//해당 메서드를 호출 -> 클라이언트 저장소에 파일 이름, 파일이 저장됨
	
	//클라이언트에서 호출해서 사용
	public boolean s2cFTP(JSONObject metaData) {
		saveMetaData(metaData);
		//1. 메타 데이터 가져오기
		int fileCount = (Integer)popData("fileCount");
		String[] fileNames = (String[])popData("fileNames");
		long[] fileSizes = (long[])popData("fileSizes");
		int bufferSize = (Integer)popData("bufferSize");
		
		//2. 파일 받기
		String receivedResult=saveFilesToBytes
				(fileCount, fileNames, fileSizes, bufferSize);
		
		//3. 받은 결과가 있는 경우
		if(receivedResult!=null) {
			System.out.println("[전송 결과] : "+receivedResult);
			saveData("fileNames", fileNames);
			return true;
		}else
			return false;
	}
	
	//파일에 대한 메타 데이터를 가져와서 임시 저장소에 저장합니다.
	private void saveMetaData(JSONObject json) {
		//파일 개수
		Number fileCount = (Number)json.get("fileCount");
		saveData("fileCount",fileCount.intValue());
		
		//파일 이름
		JSONArray array = (JSONArray)json.get("fileNames");
		String[] fileNames = new String[array.size()];
		for(int i=0;i<fileNames.length;++i)
			fileNames[i] = (String)array.get(i);
		saveData("fileNames",fileNames);
		
		//파일 크기
		array = (JSONArray)json.get("fileSizes");
		long[] fileSizes = new long[array.size()];
		for(int i=0;i<fileSizes.length;++i) {
			Number size=(Number)array.get(i);
			fileSizes[i] = size.longValue();
		}
		saveData("fileSizes",fileSizes);
		
		//버퍼 크기
		Number bufferSize = (Number)json.get("bufferSize");
		saveData("bufferSize",bufferSize.intValue());
	}
	
	//파일을 받아서 임시 저장소에 저장합니다.
	private String saveFilesToBytes(int count,String[] names, long[] sizes, int bufferSize){
		try {
			for(int i=0;i<count;++i) {
				byte[] file = receiveFilesFromServer(sizes[i],bufferSize);
				saveData(names[i],file);
			}
			//파일이 전부 전송되었음을 확인
			String response = dis.readUTF();
			JSONObject json = (JSONObject)parse(response); 
			String encryptedData=json.get("data").toString();
			response=decryptReceivedData(encryptedData);
			return response;
		}catch(Exception e) { //파일 전송 도중에 실패한 경우
			return null;
		}
	}
	//파일을 읽어서 반환(위의 메서드에서 사용)
	private byte[] receiveFilesFromServer(long size, int bufferSize) throws Exception{
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
	
	//2. 파일 송신
	
	//클라이언트에서 호출해서 사용
	public String c2sFTP(File[] files, int bufferSize) {
		if(files==null) {
			System.out.println("[예외] : 전송할 파일이 없음");
			return null;
		}else if(files.length==0) {
			System.out.println("[예외] : 전송할 파일이 없음");
			return null;
		}
		
		//파일 메타 데이터 생성
		String metaData=createMetaFromFile(files,bufferSize);
		JSONObject sendData = new JSONObject();
		//c2sFTP
		sendData.put("metaData", metaData);
		String sendDataString=sendData.toJSONString();
		
		//메타 데이터 전송
		try {
			String encryptedData=encryptSendData(sendDataString);
			send(encryptedData);
		}catch(Exception e) {
			System.out.println("[예외] : 메타 데이터 전송 실패");
			return null;
		}
		
		//파일 전송 시작
		String receivedResult= sendFiles(files,bufferSize);
		return receivedResult;
	}
	
	//메타 데이터 생성
	private String createMetaFromFile(File[] files, int bufferSize) {
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
	
	//서버로 파일 전송
	private String sendFiles(File[] files, int bufferSize){
		try {
			for(File file : files)
				sendFileToServer(file, bufferSize);
			//서버로부터 파일을 전부 받았다는 성공 여부 수신
			String response = dis.readUTF();
			JSONObject json = (JSONObject)parse(response); 
			String encryptedData=json.get("data").toString();
			response=decryptReceivedData(encryptedData);
			return response;
		}catch(Exception e) {
			System.out.println("[예외] 파일 전송 실패 : "+e.getMessage());
			return null;
		}
	}
	
	//서버로 파일 전송
	private void sendFileToServer(File file, int bufferSize) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[bufferSize];
		int bytesRead;
		while((bytesRead = fis.read(buffer))!= -1) {
			dos.write(buffer, 0,bytesRead);
		}
		dos.flush();
		fis.close();
	}
	
	/************************* 세션키 AES 암/복호화 ******************************/
	private String encryptSendData(String originData) throws Exception {
		String vi = AES.generateIV();
		String encryptData = AES.encrypt(sessionKey,vi, originData);
		return vi+encryptData;
	}
	
	public String decryptReceivedData(String receivedData) throws Exception{
		String iv = receivedData.substring(0,16);
		String encryptedData = receivedData.substring(16);
		String result=AES.decrypt(sessionKey,iv, encryptedData);
		return result;
	}
	/************************* 저장소 접근 ******************************/
	//내부에서 response을 받고 처리할 때 사용할 저장소입니다.
	public void saveData(String key, Object value) {
		store.put(key, value);
	}
	
	//데이터를 빼냅니다.
	public Object popData(String key) {
		return store.remove(key);
	}
	
	/************************* json 파서 ******************************/
	public JSONObject parse(String json) throws Exception {
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject)parser.parse(json);
		return obj;
	}
}