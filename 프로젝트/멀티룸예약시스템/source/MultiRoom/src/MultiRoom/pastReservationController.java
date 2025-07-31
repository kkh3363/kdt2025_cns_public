package MultiRoom;

import java.net.URL;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class pastReservationController implements Initializable{
	private String id="";
	@FXML private VBox FINDMYRESERVATIONROOMVBOX;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}
	public void setData(String id) throws Exception {
		// TODO Auto-generated method stub
		this.id=id;
		
		ClientConnector client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("id", id);
		
		String data=jsonData.toJSONString();
    	client.requestAndReponseSync("my_past_reservation_print", data, response ->{
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
					
			        String jsonString =info.get("my_past_reservation_print").toString();
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
					break;
			}
		});
		
	}
	
	
	private void AddToVbox(String[] fields) throws Exception {
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
	        
	        Text additionalText = new Text("결제 가격");
	        additionalText.setLayoutX(510);
	        additionalText.setLayoutY(34);

	        TextField additionalTextField = new TextField();
	        additionalTextField.setLayoutX(469);
	        additionalTextField.setLayoutY(37);
	        additionalTextField.setPrefSize(125, 22);
	        additionalTextField.setText(fields[4]+"원");  //추후 예약db수정예정
	        additionalTextField.setEditable(false); 
	        additionalTextField.setAlignment(javafx.geometry.Pos.CENTER);

	        Button button = new Button("다시 예약하기");
	        button.setLayoutX(480);
	        button.setLayoutY(160);
	        button.setOnAction(e->showReservation(e,fields[1]));

	        // Add all controls to the Pane
	        pane.getChildren().addAll(
	            text1, text2, text3, text4,
	            textField1, textField2, textField3, textField4, button, additionalText, additionalTextField
	        );
	        hbox.getChildren().addAll(pane);

		hbox.setPadding(new Insets(0, 0, 10, 0));
		FINDMYRESERVATIONROOMVBOX.getChildren().add(hbox);
	}
	private void showReservation(ActionEvent event, String string) {
		// TODO Auto-generated method stub
		//string <= 룸 아이디
		System.out.println(string);
		 try {
	            // 로그인 화면을 로드합니다.
			    FXMLLoader loader = new FXMLLoader(getClass().getResource("reservation.fxml"));
		        Parent login = loader.load();

		        // 컨트롤러를 가져옵니다.
		        reservationController controller = loader.getController();

		        // 데이터를 컨트롤러에 전달합니다.
		        controller.setData(string,id);

		        // StackPane의 루트 노드 가져오기
		        //StackPane root = (StackPane) actionButton.getScene().getRoot();

		        // 로그인 화면을 StackPane에 추가합니다.
		        //root.getChildren().clear();
		        //root.getChildren().add(login);

		        Stage stage = new Stage();
		        stage.setScene(new Scene(login));
		        stage.show();
		        
		    } catch (Exception e) {
		        e.printStackTrace();
		    }
	    
	}


}
