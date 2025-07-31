package client_exam;

import java.net.URL;
import java.util.ResourceBundle;

//import org.json.JSONObject;
import org.json.simple.JSONObject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RootController implements Initializable{
	
	@FXML private Button btnLogin;
	@FXML private TextField id_text;
	@FXML private PasswordField pwd_text;
	
	private ClientConnector client = null;
	private String sessionId = null; //인증 시 사용
	
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	btnLogin.setOnAction(e->handleBtnLoginUp(e));
    	client = new ClientConnector();
    }
    
    //로그인 버튼 이벤트 처리 ...0
    public void handleBtnLoginUp(ActionEvent event) {
    	try {
			client.connect(); //연결 설정...1
			
			//에러 처리기 등록 ...2
	    	client.setExceptionHandler(e->{
				client.disconnect();
			});
	    	
	    	//서버로 보낼 데이터 생성 ...3
	    	JSONObject jsonData = new JSONObject();
	    	jsonData.put("id", id_text.getText());
	    	jsonData.put("pw", pwd_text.getText());
			String data=jsonData.toJSONString();
			
			
			//서버로 특정 명령 요청 및 응답 처리 ...4
	    	client.requestAndReponseSync("login", data, response ->{
				
	    		//응답은 JSON 형태이므로 json으로 변환 -> status 확인
	    		JSONObject json = client.parse(response);
				String status = json.get("status").toString(); 
				
				//상태에 따른 이벤트 처리 방법
				switch(status) {
				
					case("success"):
						String encryptedData = json.get("data").toString(); //암호화된 데이터를 얻습니다. 
						String originData=client.decryptReceivedData(encryptedData); //복호화합니다.
						JSONObject obj = client.parse(originData); //JSONObject로 변환합니다.
						
						//응답받은 데이터를 사용합니다
						String sessionID = obj.get("login").toString();
						System.out.println("로그인 성공 : "+sessionID);
						
						//해당 메서드 외부에서 사용할 데이터 저장...5
						client.saveData("sessionID", sessionID);
						break;
					case("invalid"):
					case("fail"):
						String message = json.get("message").toString();
						System.out.println("넘어온 데이터 : "+message);
						break;
				}
			});
	    	
	    	//5번에서 처리한 데이터를 가져와서 다시 요청할 수 있습니다...6
	    	sessionId=(String)client.popData("sessionID");
	    	
	    	//아래는 추가 활용하는 예시입니다.
	    	jsonData = new JSONObject();
	    	jsonData.put("sessionID", sessionId);
			data=jsonData.toJSONString();
	    	
	    	client.requestAndReponseSync("예약정보", data, response -> {
	    		//do...
	    	});
	    	
	    	//연결 종료...7
	    	client.disconnect();
	    	
		} catch (Exception e) {
			e.printStackTrace();
		} 
    }

}

/*
 * 사용 흐름 1->2->3->4->7
 * 
	0. 개요 
	-> 이벤트 처리 메서드 내부에서 서버에 연결하고
    -> 요청할 명령과 파라미터를 넘긴 후 서버로부터 응답 데이터를 받아 처리합니다.
	
	1. 연결 설정
	-> 내부적으로 소켓 연결과 암호화 통신을 위한 세션키 교환이 이뤄집니다.
	
	2. 예외처리기 등록
	-> requestAndResponseSync 처리 도중 에러 발생 시
	-> 예외가 해당 핸들러로 넘어옵니다 : 이 부분에 대한 예외 처리부분를 수행합니다.
	-> 일반적으로는 소켓 연결을 끊어버립니다.
	
	3. 서버로 요청할 파라미터를 생성합니다. 
	-> JSONObject를 String으로 변환해서 전송합니다.
	
	4. requestAndResponseSync 메서드
	-> (명령+데이터(파라미터)) -> 암호화 -> 서버 전송 
	-> 서버로부터 응답이 response로 넘어옵니다.
	-> 넘어온 response에 대해서 처리하는 코드를 작성합니다.
	 
	-> 넘어온 데이터 형식은 다음과 같습니다.
	{ "status" : "상태정보", "message 또는 data" : "암호화된 데이터" }
	상태정보가 success인 경우 data에 암호화된 데이터를 넘겨줍니다.
	상태정보가 invalid 또는 fail인 경우 message를 넘겨줍니다.
	 
	-> 반환 데이터 정보 참고 : server_manager 브랜치 -> 개발 참고 문서 -> 서버 JSON 사전.xlsx
	
	5. 서버로부터 넘어온 데이터 저장 및 활용
	4번의 메서드 내부에서 처리한 데이터는 해당 메서드 내부에서만 유효합니다.
	따라서 client 객체에 넣어서 저장한 뒤 메서드 외부에서 꺼내서 활용해야 합니다.
	
	6. 5번에서 client 객체에 저장한 데이터를 꺼내서 사용합니다.
	
	7. 연결을 계속 유지해야 할 필요가 없으면 닫습니다. 
	-> 닫을 때 ClientConnector 내부 저장소는 비워집니다.
	-> 로그인 인증 정보에 대한 세션은 남아있기 때문에 다시 연결해도 세션ID로 접근할 수 있습니다.
	-> 세션ID는 루트 클라이언트에 남겨두고 필요할 때 서버로 넘겨서 활용합니다.
	
 */
