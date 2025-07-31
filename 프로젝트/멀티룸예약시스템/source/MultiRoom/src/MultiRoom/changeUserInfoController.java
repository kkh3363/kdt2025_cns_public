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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class changeUserInfoController implements Initializable{
	@FXML private TextField name;
	@FXML private TextField id;
	@FXML private TextField email;
	@FXML private PasswordField pwd;
	@FXML private PasswordField pwd2;
	@FXML private Text checkMail;
	@FXML private Button btnMain;
	@FXML private Button deleteID;
	@FXML private Button updateID;
	
	private boolean Email_check = false;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		btnMain.setOnAction(e->{
			try {
				handleBtnMain(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		deleteID.setOnAction(e->handleBtndeleteID(e));
		updateID.setOnAction(e->{
			try {
				handleBtnupdateID(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		email.textProperty().addListener((observable, oldValue, newValue)->{
			Email_check=false;
    		if(newValue.isEmpty()) {
    			checkMail.setStyle("-fx-fill: black;");
    			checkMail.setText("");
			}
			else if(RegExp.checkFormEmail(newValue)) {
				checkMail.setStyle("-fx-fill: green;");
				checkMail.setText("✓");
				Email_check=true;
			}else {
				checkMail.setStyle("-fx-fill: red;");
				checkMail.setText("이메일 형식으로 입력해주세요... ex)user@company.com");
			}
    	});
	}
	private void handleBtnupdateID(ActionEvent event) throws Exception {
		// TODO Auto-generated method stub
		ClientConnector client = new ClientConnector();
		
		if(pwd.getText().equals(pwd2.getText())) {
			
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("회원 수정");
				alert.setHeaderText("정말 회원수정 하시겠습니까?");
				alert.setContentText("회원수정시 복구 안됩니다.");
				Optional<ButtonType> result =alert.showAndWait();
				if(result.get()==ButtonType.OK) {
					try {
						client.connect();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					client.setExceptionHandler(e->{
						client.disconnect();
					});
					JSONObject jsonData = new JSONObject();
			    	jsonData.put("id", id.getText());
			    	jsonData.put("pwd", pwd2.getText());
			    	jsonData.put("email", email.getText());
			    	String data=jsonData.toJSONString();
			    	client.requestAndReponseSync("changeInfo", data, response ->{
						JSONObject json = client.parse(response);
						
						String status = json.get("status").toString(); 
						System.out.println(status);
						//상태에 따른 이벤트 처리 방법
						switch(status) {
							case("success"):
								String encryptedData = json.get("data").toString();
								String originData=client.decryptReceivedData(encryptedData);
								System.out.println("넘어온 데이터 : "+originData);
								break;
							case("invalid"):
							case("fail"):
								String message = json.get("message").toString();
								System.out.println("넘어온 데이터 : "+message);
								break;
						}
					});
				}
			});
			
		}else {
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("비밀번호가 다릅니다");
				alert.setHeaderText("비밀번호가 다릅니다");
				alert.setContentText("비밀번호가 다릅니다. 다시 확인 해주세요.");
				alert.showAndWait();

			});
		}
		client.disconnect();
	}
	
	private void handleBtndeleteID(ActionEvent event) {
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("회원탈퇴");
			alert.setHeaderText("정말 회원탈퇴 하시겠습니까?");
			alert.setContentText("회원탈퇴시 복구 안됩니다.");
			Optional<ButtonType> result =alert.showAndWait();
			if(result.get()==ButtonType.OK) {
				 ClientConnector client = new ClientConnector();
			    	try {
						client.connect();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

			    	client.setExceptionHandler(e->{
						client.disconnect();
					});
			    	JSONObject jsonData = new JSONObject();
			    	jsonData.put("id", id.getText());
					
					String data=jsonData.toJSONString();
					
			    	client.requestAndReponseSync("deleteUser", data, response ->{
						JSONObject json = client.parse(response);
						
						String status = json.get("status").toString(); 
						//상태에 따른 이벤트 처리 방법
						switch(status) {
							case("success"):
								String encryptedData = json.get("data").toString();
								String originData=client.decryptReceivedData(encryptedData);
								try {
									 // FXMLLoader를 사용하여 Main.fxml 로드
						           FXMLLoader loader = new FXMLLoader(getClass().getResource("root.fxml"));
						           Parent main = loader.load();

						           // StackPane의 루트 노드 가져오기
						           StackPane root = (StackPane) deleteID.getScene().getRoot();

						           // 로그인 화면을 StackPane에 추가합니다.
						           root.getChildren().clear();
						           root.getChildren().add(main);

						           // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
						           main.setTranslateY(350);

						           // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
						           Timeline timeline = new Timeline();
						           KeyValue keyValue = new KeyValue(main.translateYProperty(), 0);
						           KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue); // 지속 시간을 300ms로 변경
						           timeline.getKeyFrames().add(keyFrame);
						           timeline.play();
						       } catch (Exception e) {
						           e.printStackTrace();
						       }
								
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
        });

	}
	private void handleBtnMain(ActionEvent event) throws Exception {
		// TODO Auto-generated method stub
		try {
			 // FXMLLoader를 사용하여 Main.fxml 로드
           FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
           Parent main = loader.load();

           // MainController의 인스턴스를 가져오기
           MainController controller = loader.getController();
           // originData를 MainController에 전달
           controller.setOriginData(id.getText());
           
           // StackPane의 루트 노드 가져오기
           StackPane root = (StackPane) btnMain.getScene().getRoot();

           // 로그인 화면을 StackPane에 추가합니다.
           root.getChildren().clear();
           root.getChildren().add(main);

           // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
           main.setTranslateY(350);

           // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
           Timeline timeline = new Timeline();
           KeyValue keyValue = new KeyValue(main.translateYProperty(), 0);
           KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue); // 지속 시간을 300ms로 변경
           timeline.getKeyFrames().add(keyFrame);
           timeline.play();
       } catch (Exception e) {
           e.printStackTrace();
       }
	}
	public void setData(String id) {
        this.id.setText(id);
        ClientConnector client = new ClientConnector();
    	try {
			client.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("id", id);
		
		String data=jsonData.toJSONString();
		
    	client.requestAndReponseSync("findUser", data, response ->{
			JSONObject json = client.parse(response);
			
			String status = json.get("status").toString(); 
			//상태에 따른 이벤트 처리 방법
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString();
					String originData=client.decryptReceivedData(encryptedData);
					
					JSONParser parser = new JSONParser();
					JSONObject info  = (JSONObject)parser.parse(originData);
					String[] parts = info.get("check").toString().split(",");
					name.setText(parts[0]);
					email.setText(parts[1]);
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
