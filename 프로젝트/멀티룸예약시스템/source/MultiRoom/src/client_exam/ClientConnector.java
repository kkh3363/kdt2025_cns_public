package client_exam;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

//jsonSimple Parser
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import crypto.*;


public class ClientConnector {
	private Socket socket; 
	private DataInputStream dis; //서버로부터 받은 데이터 읽기
	private DataOutputStream dos; //서버로 데이터 전송
	
	private String sessionKey = null;
	private CryptoManager cryptoManager = null;
	private ExceptionHandler exceptionHandler = null; //예외처리 핸들러
	
	private HashMap<String, Object> store = null;
	//객체 사용 도중에 사용할 임시 저장소
	
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
		socket = new Socket("localhost",50001);
		dis = new DataInputStream(socket.getInputStream());
		dos = new DataOutputStream(socket.getOutputStream());
		cryptoManager = new CryptoManager();
		setupCryptoSession();
	}
	
	//소켓 연결 끊기
	public void disconnect(){
		try {
			store.clear();
			sessionKey = null;
			exceptionHandler = null;
			if(!socket.isClosed())
				socket.close();
		}catch(Exception e) { }
	}
	
	//암호화 통신 설정 과정
	private void setupCryptoSession() throws Exception{
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
				JSONObject json = new JSONObject();
				json.put("command", command);
				json.put("data", requestData);
				String sendData=encryptSendData(json.toJSONString());
				send(sendData);
				
				//서버로부터 응답 수신
				String response = dis.readUTF();
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
					JSONObject json = new JSONObject();
					json.put("command", command);
					json.put("data", requestData);
					String sendData=encryptSendData(json.toJSONString());
					send(sendData);
					
					//서버로부터 응답 수신
					String response = dis.readUTF();
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
