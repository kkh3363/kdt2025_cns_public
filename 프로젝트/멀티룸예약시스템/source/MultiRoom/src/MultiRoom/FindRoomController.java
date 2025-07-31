package MultiRoom;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;

public class FindRoomController implements Initializable{
	private String people;
	private String location;
	private LocalDate Date;
	private String time;
	private String id;
	@FXML private VBox FINDROOMVBOX;
	@FXML private Label idLabel;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

	public void setOriginData(String people, String location, LocalDate value, String time,String id) throws Exception {
		
		// TODO Auto-generated method stub
		this.people=people;
		this.location=location;
		this.Date = value;
		this.time=time;
		this.id=id;
		idLabel.setText(id);
    	//검색하는거 출력하게
    	ClientConnector client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("max_capacity", this.people);
    	jsonData.put("location", this.location);
    	jsonData.put("date", this.Date.toString());
    	jsonData.put("time", this.time.toString());

		String data=jsonData.toJSONString();
		
    	client.requestAndReponseSync("findRoom", data, response ->{
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
				
		        String jsonString =info.get("findRoom").toString();
		        // Parse the JSON array
		        JSONArray jsonArray = new JSONArray(jsonString);
		        // Convert the JSON array to ObservableList
		        
		        for (int i = 0; i < jsonArray.length(); i++) {
		      
		            String record = jsonArray.getString(i);
		            String[] fields = record.split(",");
		            
		            if (fields.length == 8) {
		                AddToVbox(fields);
		            }  
		        }
				//reservations.setText(info.get("my_reservation_print").toString());
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
	Button actionButton =null;
	private void AddToVbox(String[] fields) throws Exception {

		HBox hbox = new HBox();
		ImageView imageView = new ImageView();
		VBox InformationVBox = new VBox();
		HBox innerHBox1 = new HBox();
		HBox innerHBox2 = new HBox();
		VBox IHB1_InnerVBox = new VBox();
		VBox IHB2_InnerVBox1 = new VBox();
		VBox IHB2_InnerVBox2 = new VBox();
		Pane pricePane = new Pane();
		Pane buttonPane = new Pane();
		Pane pane1 = new Pane();
		Pane pane2 = new Pane();
		Pane pane3 = new Pane();
		Pane pane4 = new Pane();
		Pane pane5 = new Pane();
		Pane pane6 = new Pane();
		Pane pane7 = new Pane();
		Pane pane8 = new Pane();
		
		ClientConnector client = new ClientConnector();
		client.connect();
		
    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	
    	//방id와 같은 데이터
		String data=fields[1].toString();
		//파일 전송 요청
    	client.requestAndReponseSync("getRoomImg", data, response -> {
			
    		JSONObject json = client.parse(response);
			String status = json.get("status").toString(); 
			
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString(); //암호화된 데이터를 얻습니다. 
					String originData=client.decryptReceivedData(encryptedData); //복호화합니다.
					
					//메타 데이터 추출
					JSONObject jsonObj = (JSONObject)client.parse(originData);
					String metaDataString = (String)jsonObj.get("s2cFTP");
					JSONObject metaData = (JSONObject)client.parse(metaDataString);
					
					//FTP 수신 호출 -> 파일을 성공적으로 받았는지 여부를 반환
					boolean success=client.s2cFTP(metaData); 
					client.saveData("success", success);
					break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.saveData("success", false);
					break;
			}
		});
    	
    	//파일 전송 결과 확인
    	boolean success=(boolean)client.popData("success");
    	if(!success) {
    		client.disconnect();
    		return;
    	}
    	//전송에 성공한 경우 fileNames라는 이름으로 파일 이름 목록이 만들어짐
    	
    	//받은 파일 사용하기
    	String[] fileNames=(String[])client.popData("fileNames");
    	ByteArrayInputStream bis=null;
    	for(String fileName : fileNames) {
    		byte[] file=(byte[])client.popData(fileName);
    		//파일 쓰기
    		bis = new ByteArrayInputStream(file);
    	}
    	//연결 종료
    	client.disconnect();
	
		Image image = new Image(bis); // 이후 추가
		actionButton = new Button("예약");
		actionButton.setOnAction(e->showReservation(e,fields[7]));
		Text RoomNameText = new Text(fields[0]);
		Text IntroduceRoomText = new Text(fields[6]);
		Text priceText = new Text(fields[5]+"원 /시간");
		Text pane3Text = new Text("위치");
		Text pane4Text = new Text("최대 수용 인원");
		Text pane5Text = new Text("부가 서비스");
		Text pane6Text = new Text(fields[3]); 
		Text pane7Text = new Text(fields[2]+"명"); 
		Text pane8Text = new Text(fields[4]); 

		// 검색한 정보를 담을 컨테이너
		hbox.setPrefSize(600, 200);
		hbox.setStyle("-fx-border-color: black; -fx-background-color: lightblue;");

		// 이미지
		imageView.setFitHeight(180);
		imageView.setFitWidth(180);
		imageView.setPickOnBounds(true);
		imageView.setPreserveRatio(true);
		imageView.setImage(image);
		HBox.setMargin(imageView, new Insets(10, 10, 10, 10));

		// PrefSize 조정
		InformationVBox.setPrefSize(400, 250);
		innerHBox1.setPrefSize(400, 80);
		innerHBox2.setPrefSize(400, 120);
		IHB1_InnerVBox.setPrefSize(300, 80);
		IHB2_InnerVBox1.setPrefSize(120, 120);
		IHB2_InnerVBox2.setPrefSize(200, 120);
		pricePane.setPrefSize(100, 80);
		pane1.setPrefSize(300, 50);
		pane2.setPrefSize(300, 30);
		pane3.setPrefSize(120, 40);
		pane4.setPrefSize(120, 40);
		pane5.setPrefSize(120, 40);
		pane6.setPrefSize(200, 40);
		pane7.setPrefSize(200, 40);
		pane8.setPrefSize(200, 40);
		buttonPane.setPrefSize(80, 120);
		actionButton.setPrefSize(63, 29);

		// etLayoutX,Y 조정
		RoomNameText.setLayoutY(29);
		IntroduceRoomText.setLayoutY(19);
		priceText.setLayoutX(18);
		priceText.setLayoutY(44);
		pane3Text.setLayoutY(23);
		pane4Text.setLayoutY(23);
		pane5Text.setLayoutY(23);
		pane6Text.setLayoutY(23);
		pane7Text.setLayoutY(23);
		pane8Text.setLayoutY(23);
		actionButton.setLayoutX(3);
		actionButton.setLayoutY(77);

		// 화면에 추가
		pane1.getChildren().add(RoomNameText);
		pane2.getChildren().add(IntroduceRoomText);
		IHB1_InnerVBox.getChildren().addAll(pane1, pane2);
		pricePane.getChildren().add(priceText);
		innerHBox1.getChildren().addAll(IHB1_InnerVBox, pricePane);
		pane3.getChildren().add(pane3Text);
		pane4.getChildren().add(pane4Text);
		pane5.getChildren().add(pane5Text);
		IHB2_InnerVBox1.getChildren().addAll(pane3, pane4, pane5);
		pane6.getChildren().add(pane6Text);
		pane7.getChildren().add(pane7Text);
		pane8.getChildren().add(pane8Text);
		IHB2_InnerVBox2.getChildren().addAll(pane6, pane7, pane8);
		buttonPane.getChildren().add(actionButton);
		innerHBox2.getChildren().addAll(IHB2_InnerVBox1, IHB2_InnerVBox2, buttonPane);
		InformationVBox.getChildren().addAll(innerHBox1, innerHBox2);
		hbox.getChildren().addAll(imageView, InformationVBox);

		hbox.setPadding(new Insets(0, 0, 10, 0));
		FINDROOMVBOX.getChildren().add(hbox);
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

	        Stage stage = new Stage();
	        stage.setScene(new Scene(login));
	        stage.show();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
}