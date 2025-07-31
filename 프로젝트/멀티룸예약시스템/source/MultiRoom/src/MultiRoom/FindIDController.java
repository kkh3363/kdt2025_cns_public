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

public class FindIDController implements Initializable{
	@FXML private Button back;
	@FXML private Button btnMain;
	@FXML private Button findBtn;
	
	@FXML	private TextField join_name;
	@FXML	private TextField join_email;

	@FXML	private Text TextName;
	@FXML	private Text TextEmail;
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
	}
	private void handlefindBtn(ActionEvent event) throws Exception {
		// TODO Auto-generated method stub
		ClientConnector client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("name", join_name.getText());
    	jsonData.put("email", join_email.getText());
		
		String data=jsonData.toJSONString();
		
    	client.requestAndReponseSync("findID", data, response ->{
			JSONObject json = client.parse(response);
			
			String status = json.get("status").toString(); 
			//상태에 따른 이벤트 처리 방법
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString();
					String originData=client.decryptReceivedData(encryptedData);
					
					JSONParser parser = new JSONParser();
					JSONObject info  = (JSONObject)parser.parse(originData);
					
					Platform.runLater(()->{
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("아이디 찾기");
						alert.setHeaderText("아이디 결과");
						alert.setContentText(info.get("findID").toString());
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
	public void handleBtnMain(ActionEvent event) {
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
