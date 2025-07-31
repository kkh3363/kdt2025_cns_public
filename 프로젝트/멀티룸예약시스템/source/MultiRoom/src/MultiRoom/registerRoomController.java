package MultiRoom;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import MultiRoom.ClientConnector;
import MultiRoom.FileManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

public class registerRoomController implements Initializable{
	@FXML private TextField room_name;
	@FXML private TextField max_capacity;
	@FXML private TextField hourly_rate;
	@FXML private TextField details;
	
	@FXML private DatePicker date1;
	@FXML private DatePicker date2;
	@FXML private CheckBox time1;
	@FXML private CheckBox time2;
	@FXML private CheckBox time3;
	@FXML private TextField location;
	@FXML private TextField service;
	@FXML private Button pic_room;
	@FXML private TextField room_password;
	@FXML private Button register_room;
	String time = "";
	private MainController mainController;
	@FXML private Button btnMain;
	
	@FXML private ImageView PreviewImage;
	private Image firstImage;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		pic_room.setOnAction(e->{
			try {
				handleBtnPic(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		register_room.setOnAction(e->handleBtnRegisterRoom(e));
		btnMain.setOnAction(event->handleBtnMain(event));
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
	private void handleBtnRegisterRoom(ActionEvent event) {
		// TODO Auto-generated method stub
		ClientConnector client = new ClientConnector();
    	try {
			client.connect();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} //연결 설정
    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	
    	JSONObject jsonData = new JSONObject();
    	jsonData.put("room_name", room_name.getText().replace(",", ""));
    	jsonData.put("max_capacity", max_capacity.getText());
    	jsonData.put("hourly_rate", hourly_rate.getText());
    	jsonData.put("details", details.getText().replace(",", ""));
    	jsonData.put("date1", date1.getValue().toString());
    	jsonData.put("date2", date2.getValue().toString());
    	if(time1.isSelected())
    		time += "오전 ";
    	if(time2.isSelected())
    		time += "오후 ";
    	if(time3.isSelected())
    		time += "야간 ";
    	jsonData.put("time", time);
    	jsonData.put("location", location.getText().replace(",", ""));
    	jsonData.put("service", service.getText().replace(",", ""));
    	jsonData.put("room_password", room_password.getText());
    	String data=jsonData.toJSONString();
    	client.requestAndReponseSync("setRoom", data, response->{
    		
    		JSONObject json = client.parse(response);
			String status = json.get("status").toString(); 
			
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString(); //암호화된 데이터를 얻습니다. 
					String originData=client.decryptReceivedData(encryptedData); //복호화합니다.
					client.saveData("fileTransfer", originData);
					
					break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.saveData("fileTransfer", null);
					break;
			}
    	});
    	
    	String ftp = (String)client.popData("fileTransfer");
    	if(ftp==null) {
    		client.disconnect();
    		return;
    	}
    	
    	//전송할 파일리스트 생성
    	File[] files = null;
    	int i =0;
    	if (selectedFiles != null && !selectedFiles.isEmpty()) {
    		files = new File[selectedFiles.size()];
	        // 여러 파일이 선택된 경우 처리
	        for (File file : selectedFiles) {
	            files[i] = FileManager.getFile(file.getPath());
	            i++;
	        }
	    } 
    	
    	//전송 버퍼 크기 4KB
    	int bufferSize = 1024*4;
    	
    	//자동으로 파일의 메타 데이터를 생성한 뒤 서버로 보냅니다.
    	String success=client.c2sFTP(files,bufferSize);
    	
    	//결과값 확인
		if(success!=null) {
			System.out.println("[응답] : "+success);
			Platform.runLater(()->{
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("방 등록 ");
				alert.setHeaderText("방 등록 완료!");
				alert.setContentText("방 등록 완료했습니다.");
				alert.showAndWait();
				try {
					mainController.loadMainPage();
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

            });
		}
		else
			System.out.println("파일 전송 실패");
		
    	
		//(명령어, 데이터) -> 서버로 보낼 데이터 ||response -> 서버에서 넘어온 데이터 처리 	
		client.disconnect();
    }
	public void setMainController(MainController mainController) {
		this.mainController = mainController;
	}
	List<File> selectedFiles=null;
	private void handleBtnPic(ActionEvent event) throws Exception {
	    // TODO Auto-generated method stub
	    Node source = (Node) event.getSource();
	    Stage stage = (Stage) source.getScene().getWindow();
	    
	    FileChooser fileChooser = new FileChooser();
	    
	    //선언한 파일 추저에서 표시한 옵션을 넣어 줍니다.
	    fileChooser.getExtensionFilters().addAll(
	            new FileChooser.ExtensionFilter("그림파일 : Image Files", "*.png", "*.jpg", "*.gif"));
	    
	    //여러 파일을 선택할 수 있게 합니다.
	    selectedFiles = fileChooser.showOpenMultipleDialog(stage);

	    if (selectedFiles != null && !selectedFiles.isEmpty()) {
	        // 여러 파일이 선택된 경우 처리
	        for (File file : selectedFiles) {
	            System.out.println("open할 파일의 이름은 :" + file.getPath());
	            firstImage = new Image("file:" + file.getPath());
	        }
	        
	        PreviewImage.setImage(firstImage);
	    } else {
	        System.out.println("아무것도 지정하지 않았습니다.");
	    }
	}

}
