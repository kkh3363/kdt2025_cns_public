package MultiRoom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;


public class MyPageController implements Initializable {
	private String id;
	@FXML private Label ID;
	@FXML private Label reservations;
	@FXML private VBox FINDMYRESERVATIONROOMVBOX;
	@FXML private Button pastReservationsBtn;
	@FXML private Button changeUserInfo;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		changeUserInfo.setOnAction(e->{
			try {
				handleBtnChangeUser(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		pastReservationsBtn.setOnAction(e->{
			try {
				handleBtnpastReservations(e);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	private void handleBtnpastReservations(ActionEvent event) throws Exception {
		// TODO Auto-generated method stub
		 	FXMLLoader loader = new FXMLLoader(getClass().getResource("pastReservation.fxml"));
	        Parent login = loader.load();

	        // 컨트롤러를 가져옵니다.
	        pastReservationController controller = loader.getController();

	        // 데이터를 컨트롤러에 전달합니다.
	        controller.setData(id);

	        Stage stage = new Stage();
	        stage.setTitle("과거 예약 이력");
	        stage.setScene(new Scene(login));
	        stage.show();
		
	}
	private void handleBtnChangeUser(ActionEvent event) throws IOException {
		// TODO Auto-generated method stub
		  try {
	            // 로그인 화면을 로드합니다.
			    FXMLLoader loader = new FXMLLoader(getClass().getResource("changeUserInfo.fxml"));
		        Parent login = loader.load();

		        // 컨트롤러를 가져옵니다.
		        changeUserInfoController controller = loader.getController();

		        // 데이터를 컨트롤러에 전달합니다.
		        controller.setData(id);

		        // StackPane의 루트 노드 가져오기
		        StackPane root = (StackPane) changeUserInfo.getScene().getRoot();

		        // 로그인 화면을 StackPane에 추가합니다.
		        root.getChildren().clear();
		        root.getChildren().add(login);

		        // 로그인 화면의 초기 위치를 오른쪽 밖으로 설정합니다.
		        login.setTranslateY(350);

		        // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
		        Timeline timeline = new Timeline();
		        KeyValue keyValue = new KeyValue(login.translateYProperty(), 0);
		        KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);
		        timeline.getKeyFrames().add(keyFrame);
		        timeline.play();
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	    
	}
	public void setOriginData(String originData) throws Exception {
        this.id = originData;
        ID.setText(id);

        ClientConnector client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("id", id);
		
		String data=jsonData.toJSONString();
		
    	client.requestAndReponseSync("my_reservation_print", data, response ->{
			JSONObject json = client.parse(response);
			
			String status = json.get("status").toString(); 
			//상태에 따른 이벤트 처리 방법
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString();
					String origin=client.decryptReceivedData(encryptedData);
					System.out.println(origin);
					JSONParser parser = new JSONParser();
					
					JSONObject info  = (JSONObject)parser.parse(origin);
					
			        String jsonString =info.get("my_reservation_print").toString();
			        // Parse the JSON array
			        JSONArray jsonArray = new JSONArray(jsonString);

			        // Convert the JSON array to ObservableList
			        ObservableList<String[]> records = FXCollections.observableArrayList();
			        for (int i = 0; i < jsonArray.length(); i++) {
			            String record = jsonArray.getString(i);
			            String[] fields = record.split(",");
			            if (fields.length == 9) {
			                AddToVbox(fields);
			            }
			        }
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					//label에 message출력
					reservations.setText(message);
					break;
			}
		});
    }
	private void AddToVbox(String[] fields) throws Exception {
		for(String field : fields) {
			System.out.println(field);
		}
		HBox hbox = new HBox();

		
		// 검색한 정보를 담을 컨테이너
		hbox.setPrefSize(600, 200);
		hbox.setStyle("-fx-border-color: black; -fx-background-color: lightblue;");

		Pane pane = new Pane();
	    pane.setPrefSize(600, 200);

	        // Create Text nodes
	    Text text1 = new Text("신청일");
	    text1.setLayoutX(35);
	    text1.setLayoutY(40);

	    Text text2 = new Text("예약 공간");
	    text2.setLayoutX(35);
	    text2.setLayoutY(81);

	        Text text3 = new Text("예약 시간");
	        text3.setLayoutX(35);
	        text3.setLayoutY(118);

	        Text text4 = new Text("예약 인원");
	        text4.setLayoutX(35);
	        text4.setLayoutY(157);

	        // Create TextFields
	        TextField textField1 = new TextField();
	        textField1.setLayoutX(107);
	        textField1.setLayoutY(26);
	        textField1.setText(fields[7]);  //신청일
	        textField1.setEditable(false); 
	        textField1.setAlignment(javafx.geometry.Pos.CENTER);

	        TextField textField2 = new TextField();
	        textField2.setLayoutX(107);
	        textField2.setLayoutY(66);
	        textField2.setText(fields[6]);  //예약공간
	        textField2.setEditable(false); 
	        textField2.setAlignment(javafx.geometry.Pos.CENTER);
	        
	        TextField textField3 = new TextField();
	        textField3.setLayoutX(107);
	        textField3.setLayoutY(110);
	        textField3.setText(fields[2]+","+fields[3]); 
	        textField3.setEditable(false); 
	        textField3.setAlignment(javafx.geometry.Pos.CENTER);

	        TextField textField4 = new TextField();
	        textField4.setLayoutX(107);
	        textField4.setLayoutY(149);
	        textField4.setText(fields[8]+"명");  
	        textField4.setEditable(false); 
	        textField4.setAlignment(javafx.geometry.Pos.CENTER);

	        // Create additional Text and TextField
	        Text additionalText = new Text("비밀번호");
	        additionalText.setLayoutX(515);
	        additionalText.setLayoutY(34);

	        TextField additionalTextField = new TextField();
	        additionalTextField.setLayoutX(469);
	        additionalTextField.setLayoutY(37);
	        additionalTextField.setPrefSize(125, 22);
	        additionalTextField.setText(fields[5]);  //추후 예약db수정예정
	        additionalTextField.setEditable(false); 
	        additionalTextField.setAlignment(javafx.geometry.Pos.CENTER);

	        // Create Button
	        Button button = new Button("결제취소");
	        button.setLayoutX(512);
	        button.setLayoutY(160);
	        button.setOnAction(e->handleButton(e,fields[0]));

	        // Add all controls to the Pane
	        pane.getChildren().addAll(
	            text1, text2, text3, text4,
	            textField1, textField2, textField3, textField4,
	            additionalText, additionalTextField, button
	        );
	        hbox.getChildren().addAll(pane);

		hbox.setPadding(new Insets(0, 0, 10, 0));
		FINDMYRESERVATIONROOMVBOX.getChildren().add(hbox);
	}
	private void handleButton(ActionEvent event,String reservation_id) {
		Button cancelReservBtn = (Button)event.getSource();
		// TODO Auto-generated method stub
		Platform.runLater(()->{
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("예약 취소!");
			alert.setHeaderText("예약 취소합니다.");
			alert.setContentText("정말 예약을 취소하시겠습니까?");
			Optional<ButtonType> result = alert.showAndWait();
			
			if(result.isPresent()) {
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
		    	jsonData.put("reservation_id", reservation_id);
				
				String data=jsonData.toJSONString();
				
		    	client.requestAndReponseSync("deleteReservation", data, response ->{
					JSONObject json = client.parse(response);
					
					String status = json.get("status").toString();
					//상태에 따른 이벤트 처리 방법
					switch(status) {
						case("success"):
							String encryptedData = json.get("data").toString();
							String origin=client.decryptReceivedData(encryptedData);
							//삭제
					    	Pane parent = (Pane)cancelReservBtn.getParent();
					    	HBox hbox = (HBox) parent.getParent();
					    	if(FINDMYRESERVATIONROOMVBOX.getChildren().contains(hbox))
					    		FINDMYRESERVATIONROOMVBOX.getChildren().remove(hbox);
					    	System.out.println("결제 취소");
						case("invalid"):
						case("fail"):
							String message = json.get("message").toString();
							//label에 message출력
							System.out.println(message);
							break;
					}
				});
		    	client.disconnect();
			}
        });
	}
}
