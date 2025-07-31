package MultiRoom;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Login_Controller_2 implements Initializable{
	@FXML private Button back;
	@FXML private Button signUp;
	@FXML private Button btnMain;
	@FXML private Button checkID;
	
	@FXML private TextField id_text;
	@FXML private PasswordField pwd_text;
	
	@FXML private TextField join_name;
	@FXML private TextField join_id;
	@FXML private PasswordField join_pw1;
	@FXML private PasswordField join_pw2;
	@FXML private TextField join_call;
	@FXML private TextField join_email;

	@FXML private Text TextName;
	@FXML private Text TextID;
	@FXML private Text TextPW;
	@FXML private Text TextCall;
	@FXML private Text TextEmail;
	
	//유효성 검사
	private boolean ID_check = false;
	private boolean PW_check = false;
	private boolean Name_check = false;
	private boolean Call_check = false;
	private boolean Email_check = false;
	
	//요청 확인자
	private boolean isRequestSent = false;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		signUp.setOnAction(e -> {
			try {
				handleBtnsignUp(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		checkID.setOnAction(e -> {
			try {
				handleBtnCheckID(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
    	btnMain.setOnAction(e -> handleBtnMain(e));
    	back.setOnAction(e -> handleBtnMain(e));
    	
    	//이름 체크
    	join_name.textProperty().addListener((observable, oldValue, newValue)->{
    		Name_check = false;
    		if(newValue.isEmpty()) {
    			TextName.setStyle("-fx-fill : black;");
    			TextName.setText("");
    		}
    		else if(RegExp.checkFormName(newValue)) {
    			TextName.setStyle("-fx-fill : green;");
    			TextName.setText("✓");
    			Name_check = true;
    		}else {
    			TextName.setStyle("-fx-fill: red;");
    			TextName.setText("한글 또는 영문명을 입력해주세요.");
    		}
    	});
    	
    	//아이디 값의 변경이 있는 경우 check 해제 -> 중복검사에서 수행됨
    	join_id.textProperty().addListener((observable, oldValue, newValue)->{
    		ID_check = false;
    	});
    	
    	
    	join_pw1.textProperty().addListener((observable, oldValue, newValue)->{
    		PW_check = false;
    		if(newValue.isEmpty()) {
    			TextPW.setStyle("-fx-fill : black;");
    			TextPW.setText("");
    		}
    		else if(join_pw1.getText().equals(join_pw2.getText())) {
    			TextPW.setStyle("-fx-fill : blue;");
    			TextPW.setText("비밀번호가 일치합니다.");
    			//1개 이상인지 체크
    			if(!RegExp.checkFormPWD(join_pw1.getText())) {
    				TextPW.setStyle("-fx-fill : red;");
    				TextPW.setText("비밀번호를 입력해주세요");
    				PW_check = false;
    			}
    			else
    				PW_check = true;
    		}else {
    			TextPW.setStyle("-fx-fill: red;");
    			TextPW.setText("비밀번호가 일치하지 않습니다.");
    		}
    	});
    	
    	//비밀번호 일치 여부 체크
    	join_pw2.textProperty().addListener((observable, oldValue, newValue)->{
    		PW_check = false;
    		if(newValue.isEmpty()) {
    			TextPW.setStyle("-fx-fill : black;");
    			TextPW.setText("");
    		}
    		else if(join_pw1.getText().equals(join_pw2.getText())) {
    			TextPW.setStyle("-fx-fill : blue;");
    			TextPW.setText("비밀번호가 일치합니다.");
    			//1개 이상인지 체크
    			
    			if(!RegExp.checkFormPWD(join_pw2.getText())) {
    				TextPW.setStyle("-fx-fill : red;");
    				TextPW.setText("비밀번호를 입력해주세요");
    				PW_check = false;
    			}
    			else
    				PW_check = true;
    		}else {
    			TextPW.setStyle("-fx-fill: red;");
    			TextPW.setText("비밀번호가 일치하지 않습니다.");
    		}
    	});
    	
    	//전화번호 유효성 검사
    	join_call.textProperty().addListener((observable, oldValue, newValue)->{
    		Call_check=false;
			if(newValue.isEmpty()) {
				TextCall.setStyle("-fx-fill: black;");
				TextCall.setText("");
			}
			else if(RegExp.checkFormPhone(newValue)) {
				TextCall.setStyle("-fx-fill: green;");
				TextCall.setText("✓");
				Call_check=true;
			}else {
				TextCall.setStyle("-fx-fill: red;");
				TextCall.setText("전화번호 형식으로 입력해주세요... ex)010-xxxx-xxxx");
			}
    	});
    	
    	//이메일 유효성 검사
    	join_email.textProperty().addListener((observable, oldValue, newValue)->{
			Email_check=false;
    		if(newValue.isEmpty()) {
				TextEmail.setStyle("-fx-fill: black;");
				TextEmail.setText("");
			}
			else if(RegExp.checkFormEmail(newValue)) {
				TextEmail.setStyle("-fx-fill: green;");
				TextEmail.setText("✓");
				Email_check=true;
			}else {
				TextEmail.setStyle("-fx-fill: red;");
				TextEmail.setText("이메일 형식으로 입력해주세요... ex)user@company.com");
			}
    	});
    	
	}
	
    private void handleBtnCheckID(ActionEvent event) {
    	if(isRequestSent) {
    		//이미 요청이 보내진 상태면 수행X
    		return;
    	}
    	
    	new Thread(()->{
    		isRequestSent = true;
    		ClientConnector client = new ClientConnector();
    		try {
    			client.connect();
    		}catch(Exception e) {
    			System.out.println("서버에 연결할 수 없음");
    			client.disconnect();
    			isRequestSent = false;
    			return;
    		}
    		
        	client.setExceptionHandler(e->{
    			client.disconnect();
    			isRequestSent = false;
    		});
        	
        	JSONObject jsonData = new JSONObject();
        	jsonData.put("id", join_id.getText());
    		
    		String data=jsonData.toJSONString();
    		
        	client.requestAndReponseSync("check", data, response ->{
    			JSONObject json = client.parse(response);
    			
    			String status = json.get("status").toString(); 
    			//상태에 따른 이벤트 처리 방법
    			switch(status) {
    				case("success"):
    					String encryptedData = json.get("data").toString();
    					String originData=client.decryptReceivedData(encryptedData);
    					
    					JSONParser parser = new JSONParser();
    					JSONObject info  = (JSONObject)parser.parse(originData);
    					String chk = "check";
    					Platform.runLater(()->{
    						if(chk.equals(info.get("check").toString())) {
        						TextID.setStyle("-fx-fill : blue;");
        						TextID.setText("사용가능한 아이디입니다");
        						ID_check=true;
        					}
        					if(!RegExp.checkFormID(join_id.getText())) {
        						TextID.setStyle("-fx-fill : red;");
        						TextID.setText("아이디를 1글자 이상 입력해주세요");
        						ID_check=false;
        					}
    					});
    					break;
    				case("invalid"):
    				case("fail"):
    					String message = json.get("message").toString();
    					System.out.println("넘어온 데이터 : "+message);
    					Platform.runLater(()->{
    						TextID.setStyle("-fx-fill : red;");
        					TextID.setText("중복된 아이디입니다");
    					});
    					break;
    			}
    		});
        	client.disconnect();
        	isRequestSent = false;
    	}).start();
	}
	private void handleBtnsignUp(ActionEvent event) throws Exception {
		// TODO Auto-generated method stub
		boolean count = false;
		System.out.println("가입");
		
		String name = join_name.getText();
		String id = join_id.getText();
		String pw1 = join_pw1.getText();
		String pw2 = join_pw2.getText();
		String call = join_call.getText();
		String email = join_email.getText();
		
		//이름
		if(!Name_check) {
			TextName.setStyle("-fx-fill : red;");
			TextName.setText("이름을 입력해주세요.");
		}
		
		//중복검사에 없는 것도 포함됨 
		if(!ID_check) {
			TextID.setStyle("-fx-fill : red;");
			TextID.setText("중복검사를 수행해주세요.");
		}
		
		//비밀번호 일치여부 검사
		if(!PW_check) {
			TextPW.setStyle("-fx-fill : red;");
			TextPW.setText("비밀번호를 입력하고 확인해주세요.");
		}
		
		// 전화번호 빈칸 검사
		if(!Call_check) {
			TextCall.setStyle("-fx-fill : red;");
			TextCall.setText("올바른 형식의 전화번호를 입력해주세요.");
		}
		
		// 이메일 빈칸 검사
		if(!Email_check) {
			TextEmail.setStyle("-fx-fill : red;");
			TextEmail.setText("올바른 형식의 이메일을 입력해주세요.");
		}
		//하나라도 잘못되면 해당 메소드 빠져나가기
		if(!(Name_check&&ID_check&&PW_check&&Call_check&&Email_check)) {
			return;
		}
		
		if(ID_check&&PW_check)
		{
			// TODO 서버에 보내는 코드.
			ClientConnector client = new ClientConnector();
	    	client.connect();

	    	client.setExceptionHandler(e->{
				client.disconnect();
			});
	    	JSONObject jsonData = new JSONObject();
	    	jsonData.put("id", id);
	    	jsonData.put("password",pw1 );
	    	jsonData.put("email", email);
	    	jsonData.put("name", name);
	    	jsonData.put("user_type","유저");
			
			String data=jsonData.toJSONString();
			
	    	client.requestAndReponseSync("signUp", data, response ->{
				JSONObject json = client.parse(response);
				
				String status = json.get("status").toString(); 
				//상태에 따른 이벤트 처리 방법
				switch(status) {
					case("success"):
						String encryptedData = json.get("data").toString();
						String originData=client.decryptReceivedData(encryptedData);
						
						JSONParser parser = new JSONParser();
						JSONObject info  = (JSONObject)parser.parse(originData);
						//가입시 메시지창 띄우면 좋을듯
						Platform.runLater(()->{
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("가입 완료");
							alert.setHeaderText("가입을 환영합니다");
							alert.setContentText("다시 로그인해주세요.");
							Optional<ButtonType> result =alert.showAndWait();
							if(result.get()==ButtonType.OK) {
								handleBtnMain(event);
							}
		                });
						break;
					case("invalid"):
					case("fail"):
						String message = json.get("message").toString();
						System.out.println("넘어온 데이터 : "+message);
						break;
				}
			});
	    	client.disconnect();
		}
	}
	
	public void handleBtnMain(ActionEvent event) {
        try {
            // 로그인 화면을 로드합니다.
            Parent login = FXMLLoader.load(getClass().getResource("root.fxml"));

            // StackPane의 루트 노드 가져오기
            StackPane root = (StackPane) btnMain.getScene().getRoot();

            // 로그인 화면을 StackPane에 추가합니다.
            root.getChildren().add(login);

            // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
            login.setTranslateY(-350);

            // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(login.translateYProperty(), 0);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue); // 지속 시간을 300ms로 변경
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}