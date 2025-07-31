package server_manager;

import java.io.IOException;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import crypto.CryptoManager;
import server_db.DBManager;
import server_util.*;
import server_util.FileManager.PATH;

public class MultiroomServer {
	private ServerSocket socket;
	private final ExecutorService threadPool;
	private final String ipAddrString;
	private final int portNum;
	private final int backlog;
	private final Queue<SocketBinder> clientSockets;
	DBManager dbManager = null; //db 관리용
	CryptoManager cryptoManager = null; //암호화 통신용
	SessionManager sessionManager = null; //세션 관리용
	
	//포트, 백로그(최대 대기 수), 쓰레드풀 수
	public MultiroomServer(int portNum, int backlog, int threadCnt) {
		this.ipAddrString = "0.0.0.0"; //모든 주소로 개방
		this.portNum = portNum;
		this.backlog = backlog;
		this.threadPool = Executors.newFixedThreadPool(threadCnt);
		this.clientSockets = new ConcurrentLinkedQueue<>();
		//1분 주기로 세션 만료를 검사하는 세션 매니저 생성
		this.dbManager = DBManager.getInstance();
		this.sessionManager = SessionManager.getInstance(60);
		this.cryptoManager = new CryptoManager();
	}
	
	//ip주소, 포트, 백로그, 쓰레드풀 수
	public MultiroomServer(String ipAddress, int portNum, int backlog, int threadCnt) {
		this.ipAddrString = ipAddress.toString();
		this.portNum = portNum;
		this.backlog = backlog;
		this.threadPool = Executors.newFixedThreadPool(threadCnt);
		this.clientSockets = new ConcurrentLinkedQueue<>();
		
		this.dbManager = DBManager.getInstance();
		this.sessionManager = SessionManager.getInstance(60);
		this.cryptoManager = new CryptoManager();
	}
	
	//쓰레드 풀에 작업 추가
	public void addTaskInThreadPool(Runnable task) {
		threadPool.execute(task);
	}
	
	//끊어진 소켓 삭제
	public void removeSocket(SocketBinder socket) {
		clientSockets.remove(socket);
		System.out.println("[현재 연결된 소켓 수] : "+clientSockets.size());
	}
	
	//서버 시작
	public void start() throws Exception{
		try {
			InetAddress ipAddress = InetAddress.getByName(ipAddrString);
			socket = new ServerSocket(portNum, backlog, ipAddress);
		}catch(UnknownHostException e) {
			throw new Exception("잘못된 IP주소");
		}catch(IllegalArgumentException e) {
			throw new Exception("유효하지 않은 포트 번호");
		}catch(BindException e) {
			throw new Exception("사용 중인 포트 번호거나 비활성IP 주소");
		}catch(IOException e) {
			throw new Exception("well-known 포트 사용 시도(권한 필요)");
		}
		
		dbManager.openDBCP();
		
		FileManager.createDir(PATH.IMAGE,""); //영상 파일 저장용 디렉터리 
		
		System.out.println("[server] Server started");
		Thread thread=new Thread(()->{
			try {
				while(true) {
					Socket telSocket = socket.accept(); //통신 소켓 연결 대기
					SocketBinder client=new SocketBinder(this, telSocket); //통신 소켓과 서버 연결
					clientSockets.add(client);
				}
			}catch(IOException e) { 
				System.out.println("[socket] "+e.getMessage());
			}catch(Exception e) {
				System.out.println("[server] "+e.getMessage());
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	public boolean isOpened() {
		return socket.isClosed()?false:true;
	}
	
	//서버 종료
	public void stop() throws IOException{
		threadPool.shutdownNow();
		if(!socket.isClosed())
			socket.close();
		//연결된 소켓 모두 닫기
		clientSockets.stream().forEach(client->{
			client.close();
		});
		dbManager.closeDBCP();
		System.out.println("[server] Server shutdown");
	}
}
