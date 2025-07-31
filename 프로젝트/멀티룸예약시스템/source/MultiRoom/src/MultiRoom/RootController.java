package MultiRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

//import org.json.JSONObject;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import crypto.AES;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;


public class RootController implements Initializable{
	@FXML private Button btnsignUp;
	@FXML private Button btnLogin;
	@FXML private TextField id_text;
	@FXML private PasswordField pwd_text;
	@FXML private Text FindID;
	@FXML private Text FindPWD;
	
	
	private boolean isRequestSentLogin = false;
	private String sessionId = null;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
    	btnsignUp.setOnAction(e -> handleBtnsingUp(e));
    	btnLogin.setOnAction(e->handleBtnLoginUp(e));
    }

    public void handleBtnsingUp(ActionEvent event) {
        try {
            // 로그인 화면을 로드합니다.
            Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));

            // StackPane의 루트 노드 가져오기
            StackPane root = (StackPane) btnsignUp.getScene().getRoot();

            // 로그인 화면을 StackPane에 추가합니다.
            root.getChildren().clear();
            root.getChildren().add(login);

            // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
            login.setTranslateY(350);

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
    
    public void handleBtnLoginUp(ActionEvent event) {
    	//이미 처리 중이라면 동작 취소
    	if(isRequestSentLogin) {
    		return;
    	}
    	
    	new Thread(()->{
    		isRequestSentLogin = true;
    		ClientConnector client = new ClientConnector();
    		try {
    			client.connect();
    		}catch(Exception e) {
    			System.out.println("[클라이언트] 연결 실패");
    			client.disconnect();
    			isRequestSentLogin = false;
    			return;
    		}
    		
    		client.setExceptionHandler(e->{
    			client.disconnect();
    			isRequestSentLogin = false;
    		});
    		
    		JSONObject jsonData = new JSONObject();
        	jsonData.put("id", id_text.getText());
        	jsonData.put("pw", pwd_text.getText());
        	String data=jsonData.toJSONString();
        	
        	client.requestAndReponseSync("login", data, response ->{
        		JSONObject json = client.parse(response);
    			String status = json.get("status").toString(); 
    			//상태에 따른 이벤트 처리 방법
    			switch(status) {
    				case("success"):
    					String encryptedData = json.get("data").toString();
    					String originData=client.decryptReceivedData(encryptedData);
    					JSONObject info = client.parse(originData);
    					
    					//id -> sessionId 로 변경했습니다.
    					String sessionId  = info.get("sessionId").toString();
    					String user_type = info.get("login_type").toString();
    					this.sessionId = sessionId;
    					if(user_type.equals("유저")) {
    						Platform.runLater(()->{
    							try {
	       							 // FXMLLoader를 사용하여 Main.fxml 로드
	       		                    FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
	       		                    Parent main = loader.load();
	
	       		                    // MainController의 인스턴스를 가져오기
	       		                    MainController controller = loader.getController();
	       		                    // originData를 MainController에 전달
	       		                    controller.setOriginData(info.get("id").toString());
	       		                    // 세션ID 전달
	       		                    controller.setSessionId(sessionId);
	       		                    
	       				            // StackPane의 루트 노드 가져오기
	       				            StackPane root = (StackPane) btnsignUp.getScene().getRoot();
	
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
    						});
    					}else {
    						Platform.runLater(()->{
    							FXMLLoader loader = new FXMLLoader(getClass().getResource("managerMain.fxml"));
    							Parent main = null;
    							try {
    								main = loader.load(); 
    							}catch(Exception e) {
    								System.out.println(e.getMessage());
    								return;
    							}
        			            // StackPane의 루트 노드 가져오기
        			            StackPane root = (StackPane) btnsignUp.getScene().getRoot();

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
    						});
    					}
    					//로그인후 들어가는 메인창 띄우기
    					break;
    				case("invalid"):
    				case("fail"):
    					String message = json.get("message").toString();
    					Platform.runLater(()->{
    						Alert alert = new Alert(AlertType.WARNING);
    						alert.setTitle("로그인 불가!");
    						alert.setHeaderText(message);
    						alert.setContentText("ID, 패스워드 확인해주세요.");
    						alert.showAndWait();
    					});
    					break;
    			}
    		});
    		client.disconnect();
    		isRequestSentLogin = false;
    	}).start();
    }
    
    public void OnClickFindId(MouseEvent e) {
    	 try {
             // 로그인 화면을 로드합니다.
             Parent login = FXMLLoader.load(getClass().getResource("FindID.fxml"));

             // StackPane의 루트 노드 가져오기
             StackPane root = (StackPane) btnsignUp.getScene().getRoot();

             // 로그인 화면을 StackPane에 추가합니다.
             root.getChildren().clear();
             root.getChildren().add(login);

             // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
             login.setTranslateY(350);

             // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
             Timeline timeline = new Timeline();
             KeyValue keyValue = new KeyValue(login.translateYProperty(), 0);
             KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue); // 지속 시간을 300ms로 변경
             timeline.getKeyFrames().add(keyFrame);
             timeline.play();
         } catch (Exception e2) {
             e2.printStackTrace();
         }
    }
    public void OnClickFindPWD(MouseEvent e) {
   	 try {
            // 로그인 화면을 로드합니다.
            Parent login = FXMLLoader.load(getClass().getResource("FindPWD.fxml"));

            // StackPane의 루트 노드 가져오기
            StackPane root = (StackPane) btnsignUp.getScene().getRoot();

            // 로그인 화면을 StackPane에 추가합니다.
            root.getChildren().clear();
            root.getChildren().add(login);

            // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
            login.setTranslateY(350);

            // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
            Timeline timeline = new Timeline();
            KeyValue keyValue = new KeyValue(login.translateYProperty(), 0);
            KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue); // 지속 시간을 300ms로 변경
            timeline.getKeyFrames().add(keyFrame);
            timeline.play();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
   }
    public void logout() {
    	if(sessionId==null)
    		return;
    	
    	new Thread(()->{
    		ClientConnector client = new ClientConnector();
        	try {
        		client.connect();
        		
        		JSONObject param = new JSONObject();
        		param.put("sessionId", sessionId);
        		String data=param.toJSONString();
        		
        		client.requestAndReponseSync("logout", data, response->{
        			JSONObject json = client.parse(response);
        			String status = json.get("status").toString(); 
        			
        			switch(status) {
        				case("success"):
        					String encryptedData = json.get("data").toString();
        					String originData = client.decryptReceivedData(encryptedData);
        					System.out.println(originData);
        					break;
        				case("invalid"):
        				case("fail"):
        					String message = json.get("message").toString();
        					System.out.println(message);
        					break;
        			}
        		});
        		client.disconnect();
        	}catch(Exception e) {
        		client.disconnect();
        		System.out.println(e.getMessage());
        	}
    	}).start();
    }
    
}