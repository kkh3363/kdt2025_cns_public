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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class FindPWDController implements Initializable{
	@FXML private Button back;
	@FXML private Button btnMain;
	@FXML private Button findBtn;
	@FXML private Button UpdatePWD;
	
	@FXML	private TextField join_id;
	@FXML	private TextField join_name;
	@FXML	private TextField join_email;
	@FXML	private TextField pw1;
	@FXML	private TextField pw2;
	
	@FXML	private Text TextID;
	@FXML	private Text TextName;
	@FXML	private Text TextEmail;
	@FXML	private Text Textpw;
	@FXML	private Text Textpw2;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btnMain.setOnAction(e -> handleBtnMain(e));
		back.setOnAction(e -> handleBtnMain(e));
		findBtn.setOnAction(e -> {
			try {
				handlefindBtn(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		UpdatePWD.setOnAction(e->handleUpdateBtn(e));
	}

	ClientConnector client;
	private void handlefindBtn(ActionEvent event) throws Exception {
		// TODO Auto-generated method stub
		client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("id", join_id.getText());
    	jsonData.put("name", join_name.getText());
    	jsonData.put("email", join_email.getText());
		
		String data=jsonData.toJSONString();
		
    	client.requestAndReponseSync("findPWD", data, response ->{
			JSONObject json = client.parse(response);
			
			String status = json.get("status").toString(); 
			//상태에 따른 이벤트 처리 방법
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString();
					String originData=client.decryptReceivedData(encryptedData);
					
					JSONParser parser = new JSONParser();
					JSONObject info  = (JSONObject)parser.parse(originData);
					
					if(info.get("findPWD").toString().contains("변경가능")) {
						System.out.println(info.get("findPWD").toString().substring(0, info.get("findPWD").toString().lastIndexOf(" ")));
						join_id.setVisible(false);
						join_name.setVisible(false);
						join_email.setVisible(false);
						pw1.setVisible(true);
						pw2.setVisible(true);
						TextID.setVisible(false);
						TextName.setVisible(false);
						TextEmail.setVisible(false);
						Textpw.setVisible(true);
						Textpw2.setVisible(true);
						findBtn.setVisible(false);
						UpdatePWD.setVisible(true);
						client.saveData("id", info.get("findPWD").toString().substring(0, info.get("findPWD").toString().lastIndexOf(" ")));
					}
					else {
						Platform.runLater(()->{
    						Alert alert = new Alert(AlertType.INFORMATION);
    						alert.setTitle("비밀번호 찾기");
    						alert.setHeaderText("없는 정보!");
    						alert.setContentText("해당 정보의 아이디를 찾지 못했습니다.");
    						alert.showAndWait();

    	                });
					}
					break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					break;
			}
		});
		
	}
	private void handleUpdateBtn(ActionEvent event) {
		// TODO Auto-generated method stub
		String id = (String)client.popData("id");
		JSONObject jsonData = new JSONObject();
    	jsonData.put("id", id);
    	jsonData.put("pwd", pw2.getText());
    	if(pw2.getText().equals(pw1.getText())) {
    		String data=jsonData.toJSONString();
    		
        	client.requestAndReponseSync("UpdatePWD", data, response ->{
    		JSONObject json = client.parse(response);
    			
    		String status = json.get("status").toString(); 
    			//상태에 따른 이벤트 처리 방법
    			switch(status) {
    				case("success"):
    					String encryptedData = json.get("data").toString();
    					String originData=client.decryptReceivedData(encryptedData);
    					Platform.runLater(()->{
    						Alert alert = new Alert(AlertType.INFORMATION);
    						alert.setTitle("비밀번호 찾기");
    						alert.setHeaderText("비밀번호 변경완료!");
    						alert.setContentText("비밀번호가 변경되었습니다. 다시 로그인해주세요.");
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
    	}
    	else {
    		Platform.runLater(()->{
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("비밀번호 확인");
				alert.setHeaderText("비밀번호 서로 다름!");
				alert.setContentText("비밀번호가 서로 다릅니다. 다시 확인해주세요");
				alert.showAndWait();

            });
    	}
		
		//(명령어, 데이터) -> 서버로 보낼 데이터 ||response -> 서버에서 넘어온 데이터 처리 	
    	client.disconnect();
	}
	public void handleBtnMain(ActionEvent event) {
		if(client!=null)
		    client.disconnect();
        try {
            // 로그인 화면을 로드합니다.
            Parent login = FXMLLoader.load(getClass().getResource("root.fxml"));

            // StackPane의 루트 노드 가져오기
            StackPane root = (StackPane) btnMain.getScene().getRoot();

            // 로그인 화면을 StackPane에 추가합니다.
            root.getChildren().clear();
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
