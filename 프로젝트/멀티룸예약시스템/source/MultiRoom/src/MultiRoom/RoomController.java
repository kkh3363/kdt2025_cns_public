package MultiRoom;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.simple.JSONArray;
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
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class RoomController implements Initializable{
	@FXML private Button btnShow, btnDetails, btnModify,btnDel, btnMain, btnHidePanel;
	@FXML private TextField tfRoom_id, tfRoom_pic,tfRoom_location, tfRoom_service,tfRoom_name, tfmax_capacity, tfRoom_password, tfHourly_rate;
	@FXML private TextArea ta_info;
	@FXML private TableView<Rooms> tableView;
	@FXML private TableColumn<Rooms, String> tcRoom_id, tcRoom_pic,tcRoom_location, tcRoom_service,tcRoom_name, tcmax_capacity, tcRoom_password, tcHourly_rate, tcInfo;
	@FXML private DatePicker date1;
	@FXML private DatePicker date2;
	@FXML private CheckBox time1;
	@FXML private CheckBox time2;
	@FXML private CheckBox time3;
	@FXML private Button pic_room;
	@FXML private ScrollPane scrollPanel;
	@FXML private TableView<Room_Schedules> roomSchedulesTable;
	@FXML private TableColumn<Room_Schedules, Integer> colRoomId;
    @FXML private TableColumn<Room_Schedules, Date> colDate;
    @FXML private TableColumn<Room_Schedules, String> colTime;
    @FXML private TableColumn<Room_Schedules, Boolean> colAvailable;
    @FXML private AnchorPane additionalPanel;
    
	String time = "";
	private ObservableList<Rooms> roomList = FXCollections.observableArrayList();
	
	private ObservableList<Room_Schedules> roomSchedulesList = FXCollections.observableArrayList();
	
    private ClientConnector client = new ClientConnector();
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		tcRoom_id.setCellValueFactory(new PropertyValueFactory<>("room_id"));
		tcRoom_name.setCellValueFactory(new PropertyValueFactory<>("room_name"));
		tcRoom_pic.setCellValueFactory(new PropertyValueFactory<>("room_pic"));
		tcmax_capacity.setCellValueFactory(new PropertyValueFactory<>("max_capacity"));
		tcRoom_location.setCellValueFactory(new PropertyValueFactory<>("location"));
		tcRoom_service.setCellValueFactory(new PropertyValueFactory<>("service"));
		tcRoom_password.setCellValueFactory(new PropertyValueFactory<>("room_password"));
		tcHourly_rate.setCellValueFactory(new PropertyValueFactory<>("hourly_rate"));
		tcInfo.setCellValueFactory(new PropertyValueFactory<>("details"));
		
		colRoomId.setCellValueFactory(new PropertyValueFactory<>("room_id"));
		colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
		colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
		colAvailable.setCellValueFactory(new PropertyValueFactory<>("available"));
		
		// 테이블에 ObservableList 설정
        tableView.setItems(roomList);
        
        btnShow.setOnAction(event ->{
        	try {
        		showRoom(); // 모든방 조회
        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        });
        btnDetails.setOnAction(event -> {
            try {
            	Details();  // 방 상세 예약정보 출력
            } catch (Exception e) {
                e.printStackTrace();
            } 
        });
        btnDel.setOnAction(event -> {
			try {
				del();  // 방 삭제 요청
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
        btnModify.setOnAction(event->{
        	try {
        		modify();
        	}catch (Exception e) {
				// TODO: handle exception
        		e.printStackTrace();
			}
        });
		
		btnMain.setOnAction(event->handleBtnMain(event));
		
	    
		// 패널을 숨기는 버튼 핸들러
	    btnHidePanel.setOnAction(event -> scrollPanel.setVisible(false));

	    
		
		 // 테이블의 선택된 행이 바뀔 때마다 TextField에 해당 데이터 표시
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
            	tfRoom_id.setText(Integer.toString(newSelection.getRoom_id()));
            	tfRoom_name.setText(newSelection.getRoom_name());
            	tfRoom_pic.setText(newSelection.getRoom_pic());
            	tfmax_capacity.setText(Integer.toString(newSelection.getMax_capacity()));
            	tfRoom_location.setText(newSelection.getLocation());
            	tfRoom_service.setText(newSelection.getService());
            	tfRoom_password.setText(newSelection.getRoom_password());
            	tfHourly_rate.setText(Integer.toString(newSelection.getHourly_rate()));
            	ta_info.setText(newSelection.getDetails());
            }
        });
	}

	private void handleBtnMain(ActionEvent event) {
    	try {
    		 // FXMLLoader를 사용하여 managerMain.fxml 로드
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("managerMain.fxml"));
		    Parent main = loader.load();

		    // managerMainController의 인스턴스를 가져오기
		    managerMainController controller = loader.getController();
		    
		    // StackPane의 루트 노드 가져오기
		    StackPane root = (StackPane) btnMain.getScene().getRoot();

		    // UsersManager 화면을 StackPane에 추가합니다.
		    root.getChildren().clear();
		    root.getChildren().add(main);

		    // UsersManager 화면의 초기 위치를 아래쪽 밖으로 설정합니다.
		    main.setTranslateY(-350);

		    // Timeline을 생성하고 KeyValue와 KeyFrame을 추가합니다.
		    Timeline timeline = new Timeline();
		    KeyValue keyValue = new KeyValue(main.translateYProperty(), 0);
		    KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue);  // 지속 시간을 300ms로 설정
		    timeline.getKeyFrames().add(keyFrame);
		    timeline.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
    
 // 서버에 사용자 삭제 요청하는 메서드
    public void del() throws Exception {
        Rooms selectedRoom = tableView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showError("삭제할 방을 선택하세요.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("방 삭제");
        confirmDialog.setHeaderText("방 id:"+ selectedRoom.getRoom_id() +"방 이름: " + selectedRoom.getRoom_name() + "를 삭제합니다.");
        confirmDialog.setContentText("이 작업은 되돌릴 수 없습니다.");
        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            client.connect();

            client.setExceptionHandler(e -> {
                client.disconnect();
                showError("서버 연결 오류");
            });

            // 서버에 삭제 요청
            JSONObject jsonData = new JSONObject();
            jsonData.put("room_id", selectedRoom.getRoom_id());

            client.requestAndReponseSync("deleteRoom", jsonData.toJSONString(), response -> {
                JSONObject json = client.parse(response);
                String status = json.get("status").toString();
                System.out.println(status);
                switch (status) {
                    case "success":
                        Platform.runLater(() -> {
                            roomList.remove(selectedRoom);  // 테이블에서 사용자 제거
                        });
                        break;
                    case "fail":
                        showError("사용자 삭제 중 오류가 발생했습니다.");
                        break;
                }
            });
            client.disconnect();
        }
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

                        // JSON 데이터를 Rooms 리스트로 변환
                        List<Rooms> roomList = parseRooms(originData);

                        // JavaFX UI 스레드에서 테이블 뷰 업데이트
                        Platform.runLater(() -> {
                            this.roomList.setAll(roomList); // ObservableList에 방 목록 설정
                        });
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
    
    public void modify() throws Exception {
        Rooms selectedRoom = tableView.getSelectionModel().getSelectedItem();
        if (selectedRoom == null) {
            showError("수정할 방을 선택하세요.");
            return;
        }

        String room_id = tfRoom_id.getText().trim();
        String room_pic	= tfRoom_pic.getText().trim();
        String room_name = tfRoom_name.getText().trim();
        String max_capacity = tfmax_capacity.getText().trim();
        String location = tfRoom_location.getText().trim();
        String service = tfRoom_service.getText().trim();
        String room_password = tfRoom_password.getText().trim();
        String hourly_rate = tfHourly_rate.getText().trim();
        String details = ta_info.getText().trim();

        if (room_id.isEmpty() || room_name.isEmpty() || max_capacity.isEmpty() || room_password.isEmpty() || hourly_rate.isEmpty() || details.isEmpty()) {
            showError("모든 필드를 입력해야 합니다.");
            return;
        }

        JSONObject jsonData = new JSONObject();
        jsonData.put("room_id", room_id);
        jsonData.put("room_pic", room_pic);
        jsonData.put("room_name", room_name);
        jsonData.put("max_capacity", max_capacity);
        jsonData.put("location", location);
        jsonData.put("service", service);
        jsonData.put("room_password", room_password);
        jsonData.put("hourly_rate", hourly_rate);
        jsonData.put("details", details);

        client.connect();
        client.setExceptionHandler(e -> {
            client.disconnect();
            showError("서버 연결 오류");
        });

        client.requestAndReponseSync("updateRoom", jsonData.toJSONString(), response -> {
            JSONObject json = client.parse(response);
            String status = json.get("status").toString();
            switch (status) {
                case "success":
                    Platform.runLater(() -> {
                        try {
                            showRoom(); // 성공적으로 수정되었으면 방 목록을 다시 로드
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    break;
                case "fail":
                    showError("방 수정 중 오류가 발생했습니다.");
                    break;
            }
        });

        client.disconnect();
    }
    private void Details() throws Exception {
		// TODO Auto-generated method stub
		Rooms selectedRoom = tableView.getSelectionModel().getSelectedItem();
	    if (selectedRoom == null) {
	        showError("방을 선택하세요.");
	        return;
	    }

	    int roomId = selectedRoom.getRoom_id();
	    
	    // 서버에 방 일정 요청
	    client.connect();

	    client.setExceptionHandler(e -> {
	        client.disconnect();
	        showError("서버 연결 오류");
	    });

	    // 방의 예약 정보를 요청
	    JSONObject jsonData = new JSONObject();
	    jsonData.put("room_id", roomId);

	    client.requestAndReponseSync("getRoomSchedules", jsonData.toJSONString(), response -> {
	        try {
	            JSONObject json = client.parse(response);
	            String status = (String) json.get("status");
	            switch (status) {
	                case "success":
	                    String encryptedData = (String) json.get("data");
	                    String originData = client.decryptReceivedData(encryptedData);

	                    // JSON 데이터를 Room_Schedules 리스트로 변환
	                    List<Room_Schedules> schedulesList = parseRoomSchedules(originData);

	                    // JavaFX UI 스레드에서 테이블 뷰 업데이트
	                    Platform.runLater(() -> {
	                        roomSchedulesList.setAll(schedulesList); // ObservableList에 방 일정 설정
	                        roomSchedulesTable.setItems(roomSchedulesList); // 테이블에 설정
	                    });
	                    scrollPanel.setVisible(true);//패널을 보이게함
	                    break;
	                case "fail":
	                    showError("방 일정 정보를 가져오는 데 실패했습니다.");
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
    
    private List<Room_Schedules> parseRoomSchedules(String jsonData) {
        List<Room_Schedules> schedulesList = new ArrayList<>();
        try {
            JSONObject jsonObject = (JSONObject) new JSONParser().parse(jsonData);
            JSONArray jsonArray = (JSONArray) jsonObject.get("schedules");
            for (Object obj : jsonArray) {
                JSONObject jsonSchedule = (JSONObject) obj;
                Room_Schedules schedule = parseRoomScheduleObject(jsonSchedule);
                schedulesList.add(schedule);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return schedulesList;
    }

    private Room_Schedules parseRoomScheduleObject(JSONObject jsonSchedule) {
        Long room_id = (Long) jsonSchedule.get("room_id");
        Date date = Date.valueOf((String) jsonSchedule.get("date")); // 날짜 형식 변환
        String time = (String) jsonSchedule.get("time");
        Boolean available = (Boolean) jsonSchedule.get("available");
        return new Room_Schedules(room_id.intValue(), date, time, available);
    }
	
	// JSON 데이터를 Users 리스트로 변환하는 메서드
    private List<Rooms> parseRooms(String jsonData) {
        List<Rooms> roomList = new ArrayList<>();
        try {
            Object parsedData = new JSONParser().parse(jsonData);

            // 응답이 JSONArray인지 JSONObject인지 확인
            if (parsedData instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) parsedData;
                for (Object obj : jsonArray) {
                    JSONObject jsonRoom = (JSONObject) obj;
                    Rooms room = parseRoomObject(jsonRoom);
                    roomList.add(room);
                }
            } else if (parsedData instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) parsedData;

                // JSONObject일 경우, JSONArray로 변환할 수 있는지 확인
                Object rooms = jsonObject.get("Rooms"); // "Rooms" 키로 데이터를 가져온다고 가정
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
        Long room_id = (Long) jsonRoom.get("room_id"); // 숫자는 Long으로 캐스팅
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
	
	 // 오류 메시지를 보여주는 메서드
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
            alert.showAndWait();
        });
    }

}
