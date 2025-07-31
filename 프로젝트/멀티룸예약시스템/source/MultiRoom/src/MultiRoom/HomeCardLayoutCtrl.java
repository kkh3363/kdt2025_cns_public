package MultiRoom;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.fxml.Initializable;

public class HomeCardLayoutCtrl implements Initializable {
	private ClientConnector client = new ClientConnector();
	
    @FXML private ScrollPane scroll;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
    	
    }

    public void startHome() throws Exception {
    	showRoom();
    }
    private void ShowCard(List<Rooms> roomList) throws Exception {
    	int i = 0;
    	GridPane gridPane = new GridPane();
        gridPane.setPadding(new Insets(10));
        gridPane.setHgap(20);
        gridPane.setVgap(20);
    	
    	for (Rooms room : roomList) {
    		VBox card = createCard(room.getRoom_pic(),room.getRoom_name(), room.getDetails(), room.getHourly_rate());
            gridPane.add(card, i % 3, i / 3);
            i++;  
        }
    	
        // 카드 개수에 따라 높이 조정
        int numRows = (int) Math.ceil(i / 3.0);

        double rowHeight = 250;
        gridPane.setPrefHeight(numRows * (rowHeight + gridPane.getVgap())); // 행 개수에 따라 높이 설정

        // ScrollPane에 GridPane 추가
        scroll.setContent(gridPane);
    }

    // 카드 생성 메서드
    private VBox createCard(String room_pic, String room_name, String room_details, int room_HRate) throws Exception {
        VBox card = new VBox();
        card.setPadding(new Insets(10));
        card.setSpacing(10);
        card.setStyle("-fx-border-color: #cccccc; -fx-border-width: 2px; -fx-border-radius: 10px;");
		ClientConnector client = new ClientConnector();
		client.connect();
		
    	client.setExceptionHandler(e->{
			client.disconnect();
		});
      	String data=room_pic;
      		
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
        // 이미지
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(250);
        imageView.setFitHeight(250);
        card.setMaxWidth(250);
        // 제목
        Label titleLabel = new Label(room_name);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        titleLabel.setWrapText(true);
        // 설명
        Label descriptionLabel = new Label(room_details);
        descriptionLabel.setStyle("-fx-font-size: 14px;");
        descriptionLabel.setWrapText(true);
        
        // 가격
        Label priceLabel = new Label(room_HRate + "원/시간");
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #888888;");
        priceLabel.setWrapText(true);
        
        // VBox에 이미지, 제목, 설명, 가격을 추가
        card.getChildren().addAll(imageView, titleLabel, descriptionLabel, priceLabel);

        // 카드 클릭 이벤트 추가
        card.setOnMouseClicked(event -> handleCardClick(room_name, room_details, room_HRate));
        return card;
    }
    
    private void handleCardClick(String title, String description, int price) {
    	
    	Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("상세 정보");

        BorderPane popupLayout = new BorderPane();
        popupLayout.setPadding(new Insets(20));

        Label titleLabel = new Label("제목: " + title);
        Label descriptionLabel = new Label("설명: " + description);
        Label priceLabel = new Label("가격: " + price + "원/시간");
        
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        descriptionLabel.setStyle("-fx-font-size: 16px;");
        priceLabel.setStyle("-fx-font-size: 16px;");
        
        titleLabel.setWrapText(true);
        descriptionLabel.setWrapText(true);
        priceLabel.setWrapText(true);
        VBox content = new VBox(10, titleLabel, descriptionLabel, priceLabel);
        popupLayout.setCenter(content);

        Scene popupScene = new Scene(popupLayout, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.show();
        
    	
    }
    public void showRoom() throws Exception {
        client.connect();

        client.setExceptionHandler(e -> {
            client.disconnect();
            showError("서버 연결 오류");
        });

        client.requestAndReponseSync("showRoom", "params", response -> {
            try {
                JSONObject json = client.parse(response);
                String status = (String) json.get("status");
                switch (status) {
                    case "success":
                        String encryptedData = (String) json.get("data");
                        String originData = client.decryptReceivedData(encryptedData);
                        List<Rooms> roomList = parseRooms(originData);
                        ShowCard(roomList);
                        break;
                    case "fail":
                        showError("서버에서 데이터를 가져오는 데 실패했습니다.");
                        break;
                    default:
                        showError("알 수 없는 서버 응답입니다.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showError("데이터 처리 중 오류가 발생했습니다.");
            }
        });
        client.disconnect();
    }
    private List<Rooms> parseRooms(String jsonData) {
        List<Rooms> roomList = new ArrayList<>();
        try {
            Object parsedData = new JSONParser().parse(jsonData);
            
            if (parsedData instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) parsedData;
                for (Object obj : jsonArray) {
                    JSONObject jsonRoom = (JSONObject) obj;
                    Rooms room = parseRoomObject(jsonRoom);
                    roomList.add(room);
                }
            } else if (parsedData instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) parsedData;

                Object rooms = jsonObject.get("Rooms");
                if (rooms instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) rooms;
                    for (Object obj : jsonArray) {
                        JSONObject jsonRoom = (JSONObject) obj;
                        Rooms room = parseRoomObject(jsonRoom);
                        roomList.add(room);
                    }
                } else {
                    System.out.println("Unexpected JSON format: " + jsonObject);
                }
            } else {
                System.out.println("Invalid JSON format: " + jsonData);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return roomList;
    }
    private Rooms parseRoomObject(JSONObject jsonRoom) {
        Long room_id = (Long) jsonRoom.get("room_id");
        String room_pic = (String) jsonRoom.get("room_pic");
        String room_name = (String) jsonRoom.get("room_name");
        Long max_capacity = (Long) jsonRoom.get("max_capacity");
        String location = (String) jsonRoom.get("location");
        String service = (String) jsonRoom.get("service");
        String room_password = (String) jsonRoom.get("room_password");
        Long hourly_rate = (Long) jsonRoom.get("hourly_rate");
        String details = (String) jsonRoom.get("details");

        return new Rooms(room_id.intValue(), room_pic, room_name, max_capacity.intValue(),location,service, room_password, hourly_rate.intValue(), details);
    }
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
            alert.showAndWait();
        });
    }
}