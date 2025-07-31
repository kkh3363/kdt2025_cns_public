package MultiRoom;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class managerMainController implements Initializable{
	
	@FXML private Button btnUserManager;
	@FXML private Button btnRoomManager;
	@FXML private Button btnRoomADDManager;
	@FXML private Button btnSalesManager;
	
	@Override
    public void initialize(URL location, ResourceBundle resources) {
    	btnUserManager.setOnAction(e -> handleUserManager(e));
    	btnRoomManager.setOnAction(e-> handleRoomManager(e));
    	btnRoomADDManager.setOnAction(e-> handleRoomADDManager(e));
    	btnSalesManager.setOnAction(e -> handleSalesManager(e));
    }
	
	private void handleRoomADDManager(ActionEvent event) {
		// TODO Auto-generated method stub
		try {
			 // FXMLLoader를 사용하여 Main.fxml 로드
			 // FXMLLoader를 사용하여 UsersManager.fxml 로드
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("registerRoom.fxml"));
		    Parent main = loader.load();

		  
		    // StackPane의 루트 노드 가져오기
		    StackPane root = (StackPane) btnUserManager.getScene().getRoot();

		    // UsersManager 화면을 StackPane에 추가합니다.
		    root.getChildren().clear();
		    root.getChildren().add(main);

		    // UsersManager 화면의 초기 위치를 아래쪽 밖으로 설정합니다.
		    main.setTranslateY(350);

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

	public void handleUserManager(ActionEvent event) {
		try {
			 // FXMLLoader를 사용하여 Main.fxml 로드
			 // FXMLLoader를 사용하여 UsersManager.fxml 로드
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("UsersManager.fxml"));
		    Parent main = loader.load();

		    // UsersController의 인스턴스를 가져오기
		    UsersController controller = loader.getController();  // MainController 대신 UsersController 사용
		    
		    // StackPane의 루트 노드 가져오기
		    StackPane root = (StackPane) btnUserManager.getScene().getRoot();

		    // UsersManager 화면을 StackPane에 추가합니다.
		    root.getChildren().clear();
		    root.getChildren().add(main);

		    // UsersManager 화면의 초기 위치를 아래쪽 밖으로 설정합니다.
		    main.setTranslateY(350);

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
	
	public void handleRoomManager(ActionEvent event) {
		try {
			 // FXMLLoader를 사용하여 Main.fxml 로드
			 // FXMLLoader를 사용하여 UsersManager.fxml 로드
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("roomManager.fxml"));
		    Parent main = loader.load();

		    // UsersController의 인스턴스를 가져오기
		    RoomController controller = loader.getController();  // MainController 대신 UsersController 사용
		    
		    // StackPane의 루트 노드 가져오기
		    StackPane root = (StackPane) btnUserManager.getScene().getRoot();

		    // UsersManager 화면을 StackPane에 추가합니다.
		    root.getChildren().clear();
		    root.getChildren().add(main);

		    // UsersManager 화면의 초기 위치를 아래쪽 밖으로 설정합니다.
		    main.setTranslateY(350);

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
	
	public void handleSalesManager(ActionEvent event) {
		try {
			 // FXMLLoader를 사용하여 Main.fxml 로드
			 // FXMLLoader를 사용하여 UsersManager.fxml 로드
		    FXMLLoader loader = new FXMLLoader(getClass().getResource("UsersManager.fxml"));
		    Parent main = loader.load();

		    // UsersController의 인스턴스를 가져오기
		    UsersController controller = loader.getController();  // MainController 대신 UsersController 사용
		    
		    // StackPane의 루트 노드 가져오기
		    StackPane root = (StackPane) btnUserManager.getScene().getRoot();

		    // UsersManager 화면을 StackPane에 추가합니다.
		    root.getChildren().add(main);

		    // UsersManager 화면의 초기 위치를 아래쪽 밖으로 설정합니다.
		    main.setTranslateY(350);

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

	public void setOriginData(String id) {
        // 전달받은 id 데이터를 처리하는 로직 추가
        System.out.println("Received ID: " + id);
    }
}
