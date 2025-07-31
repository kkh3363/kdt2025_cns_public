package MultiRoom;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Callback;

public class reservationController implements Initializable{
	String string="";
	private String id = "";
	@FXML Button reservation_btn;
	@FXML Button check_btn;
	@FXML ImageView room_image;
	@FXML DatePicker date;
	@FXML ComboBox time;
	@FXML Text room_name;
	@FXML Text service;
	@FXML Text location;
	@FXML Text hourly_price;
	@FXML Text max_capacity;
	@FXML Text details;
	@FXML TextField people;
	String data_img="";
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		reservation_btn.setOnAction(e->{
			try {
				handleReservationBtn(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		check_btn.setOnAction(e->handleCheckBtn(e));
		date.setDayCellFactory(new Callback<DatePicker, DateCell>() {
	            public DateCell call(DatePicker datePicker) {
	                return new DateCell() {
	                    public void updateItem(LocalDate item, boolean empty) {
	                        super.updateItem(item, empty);
	                        // 오늘 날짜 이전의 날짜 비활성화
	                        if (item.isBefore(LocalDate.now())) {
	                            setDisable(true);
	                            setStyle("-fx-background-color: #ffc0cb;"); // 비활성화된 날짜 색상
	                        }
	                    }
	                };
	            }
	        });
		
	}
	
	private void handleCheckBtn(ActionEvent event) {
		
		String dateString = null;
		try {
			dateString=date.getValue().toString();
		}catch(Exception e) {
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("조회 실패");
				alert.setHeaderText("조회 실패!");
				alert.setContentText("입력하신 정보가 유효하지 않습니다.");
				alert.showAndWait();
	        });
			return;
		}
		// TODO Auto-generated method stub
		ClientConnector client = new ClientConnector();
    	try {
			client.connect();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("room_id",Integer.parseInt(string));
    	jsonData.put("reservation_date", date.getValue().toString());
    	String data=jsonData.toJSONString();
    	
		client.requestAndReponseSync("available_time", data, response ->{
			JSONObject json = client.parse(response);
			String status = json.get("status").toString(); 
			//상태에 따른 이벤트 처리 방법
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString();
					String origin=client.decryptReceivedData(encryptedData);
					
					JSONParser parser = new JSONParser();
				
					JSONObject info  = (JSONObject)parser.parse(origin);
					
					String jsonString =info.get("result").toString();
					String[] timeArray = jsonString.split(",");
					if(!timeArray[0].equals(""))
					{
						ObservableList<String> timeList = FXCollections.observableArrayList(timeArray);
						time.setItems(timeList);
					}
					client.disconnect();
				break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.disconnect();
					break;
			}
		});
	}

	private void handleReservationBtn(ActionEvent event) throws Exception {
		String dateString = null;
		String timeString = null;
		int peopleCount = 1;
		try {
			dateString=date.getValue().toString();
			timeString=time.getValue().toString();
	    	peopleCount=Integer.parseInt(people.getText());
		}catch(Exception e) {
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("결제 사이트 연결예정");
				alert.setHeaderText("결제 실패!");
				alert.setContentText("입력하신 정보가 유효하지 않습니다.");
				alert.showAndWait();
	        });
			return;
		}
		
		//예약
		ClientConnector client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("room_id",Integer.parseInt(string));
    	jsonData.put("user_id",id);
    	jsonData.put("reservation_date", dateString);
    	
    	jsonData.put("reservation_time", timeString); 
    	jsonData.put("people", peopleCount);

		String data=jsonData.toJSONString();
		System.out.println(data);
    	client.requestAndReponseSync("reservation", data, response ->{
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
					
			        String jsonString =info.get("reservation").toString();
			        Platform.runLater(()->{
						Alert alert = new Alert(AlertType.INFORMATION);
						alert.setTitle("결제 사이트 연결예정");
						alert.setHeaderText("결제 완료!");
						alert.setContentText("결제가 완료되었습니다.");
						alert.showAndWait();
	
	                });
			        client.disconnect();
			        break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.disconnect();
					break;
			}
		});
	}

	public void setData(String string,String id) throws Exception {
		// TODO Auto-generated method stub
		this.string=string; // 방id
		this.id=id;
		ClientConnector client = new ClientConnector();
    	client.connect();

    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("room_id", this.string);

		String data=jsonData.toJSONString();
		
    	client.requestAndReponseSync("findRoomByid", data, response ->{
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
		            room_name.setText(fields[0]);
		            max_capacity.setText(fields[3]);
		            hourly_price.setText(fields[5]);
		            location.setText(fields[3]);
		            service.setText(fields[4]);
		            details.setText(fields[6]);
		            data_img=fields[1].toString();
		        }
				//reservations.setText(info.get("my_reservation_print").toString());
		        client.disconnect();
				break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.disconnect();
					break;
			}
		});
    	
    	ClientConnector client_img = new ClientConnector();
    	client_img.connect();
		
    	client_img.setExceptionHandler(e->{
    		client_img.disconnect();
		});

		client_img.requestAndReponseSync("getRoomImg", data_img, response -> {
			
    		JSONObject json = client_img.parse(response);
			String status = json.get("status").toString(); 
			
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString(); //암호화된 데이터를 얻습니다. 
					String originData=client_img.decryptReceivedData(encryptedData); //복호화합니다.
					
					//메타 데이터 추출
					JSONObject jsonObj = (JSONObject)client_img.parse(originData);
					String metaDataString = (String)jsonObj.get("s2cFTP");
					JSONObject metaData = (JSONObject)client_img.parse(metaDataString);
					
					//FTP 수신 호출 -> 파일을 성공적으로 받았는지 여부를 반환
					boolean success=client_img.s2cFTP(metaData); 
					client_img.saveData("success", success);
					break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client_img.saveData("success", false);
					break;
			}
		});
    	
    	//파일 전송 결과 확인
    	boolean success=(boolean)client_img.popData("success");
    	if(!success) {
    		client_img.disconnect();
    		return;
    	}
    	//전송에 성공한 경우 fileNames라는 이름으로 파일 이름 목록이 만들어짐
    	
    	//받은 파일 사용하기
    	String[] fileNames=(String[])client_img.popData("fileNames");
    	ByteArrayInputStream bis=null;
    	for(String fileName : fileNames) {
    		byte[] file=(byte[])client_img.popData(fileName);
    		//파일 쓰기
    		bis = new ByteArrayInputStream(file);
    	}
    	Image image = new Image(bis); // 이후 추가
    	room_image.setImage(image);
    	client_img.disconnect();
	}
}