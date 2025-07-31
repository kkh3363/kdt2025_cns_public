package MultiRoom;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.json.simple.JSONObject;



public class MainController implements Initializable{
	@FXML private Button btnMyPage;
	@FXML private Button btnClose;
	@FXML private Button btnFind;
	@FXML private Button btnRegister;
	@FXML private BorderPane bp;
	@FXML private AnchorPane ap;
	
	@FXML private Spinner<Integer> people;

	@FXML private TextField location;
	@FXML private DatePicker date;
	
	@FXML private ComboBox combobox1;
	
	@FXML private ImageView exit, menu;
	@FXML private AnchorPane pane1, pane2;
	
	//서버에서 인증 시 해당 Id를 사용합니다.
	private String sessionId = null;  	
	//세션Id 설정
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		SidebarCtrl();
		
		btnClose.setOnAction(e->handleBtnClose(e));
		btnFind.setOnAction(e->{
			try {
				handleBtnFind(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		String[] strArrList = {"오전","오후","야간"};
		ObservableList<String> fxComboNameList = FXCollections.observableArrayList(strArrList);
		combobox1.setItems(fxComboNameList);
		btnRegister.setOnAction(e->{
			try {
				handleBtnRegister(e);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		try {
			loadPage("HomeCardLayout");
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		//인원수 초기값(0~1000, 초기값 1)
		SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 1);
		people.setValueFactory(valueFactory);
		people.setEditable(true);
		people.getEditor().textProperty().addListener((obs, oldValue, newValue)->{
			try {
				int value=Integer.parseInt(newValue);
				people.getValueFactory().setValue(value); //새로운 값으로 설정
			}catch(NumberFormatException e) {
				people.getEditor().setText(oldValue); //변환이 안되면 기본 값으로 설정
			}
		});
	}
	private void handleBtnRegister(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		loadPage("registerRoom");
	}
	private void handleBtnFind(ActionEvent e) throws Exception {
		// TODO Auto-generated method stub
		//검색한거 출력하게
		loadPage("FindRoom");
	}
	//종료 대신 -> 로그아웃
	//handleBtnLogout 메서드 내부 동작 추가
	private void handleBtnClose(ActionEvent e) {
		//세션 삭제 처리
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
	    	}catch(Exception ex) {
	    		client.disconnect();
	    		System.out.println(ex.getMessage());
	    	}
		}).start();
		
		//로그인 화면 로드
		Platform.runLater(()->{
			try {
	            Parent login = FXMLLoader.load(getClass().getResource("root.fxml"));
	            StackPane root = (StackPane) btnClose.getScene().getRoot();
	            
	            root.getChildren().clear();
	            root.getChildren().add(login);

	            login.setTranslateY(-350);
	            Timeline timeline = new Timeline();
	            KeyValue keyValue = new KeyValue(login.translateYProperty(), 0);
	            KeyFrame keyFrame = new KeyFrame(Duration.millis(300), keyValue); // 지속 시간을 300ms로 변경
	            timeline.getKeyFrames().add(keyFrame);
	            timeline.play();
	        } catch (Exception el) {
	            System.out.println(el.getMessage());
	        }
		});
	}
	@FXML private Label dataLabel; // 예를 들어, 데이터를 표시할 Label
    // Setter 메서드
	public void setOriginData(String originData) {
		dataLabel.setText(originData);
	}
	 
	@FXML
	private void homeClick(MouseEvent event) throws Exception{
		 //bp.setCenter(ap);
		 loadPage("HomeCardLayout");
		 closeSideBar();
	} 
	@FXML
	private void MyPageBtnClick(MouseEvent event) throws Exception {
		loadPage("MyPage");
		closeSideBar();
	}       
	public void loadMainPage() throws Exception {
		//bp.setCenter(ap);   
		 loadPage("HomeCardLayout"); 
	}
	private void loadPage(String page) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource(page + ".fxml"));
	        Parent root = loader.load();
	        if(page.equals("MyPage")) {
				MyPageController MC = loader.getController();
				MC.setOriginData(dataLabel.getText());
	        }
	        else if(page.equals("FindRoom")) {
	        	FindRoomController FC = loader.getController();
	        	String peopleText = people.getEditor().getText(); 
	        	String locationText = location.getText() != null ? location.getText() : ""; // location null 체크
	        	LocalDate dateValue = date.getValue() != null ? date.getValue() : LocalDate.now(); // date null 체크
	        	String comboValue = combobox1.getValue() != null ? combobox1.getValue().toString() : ""; // comboBox null 체크

	        	FC.setOriginData(peopleText, locationText, dateValue, comboValue, dataLabel.getText());
	        	
	        }
	        if (page.equals("registerRoom")) {
	            registerRoomController RC = loader.getController();
	            RC.setMainController(this); // 현재 컨트롤러의 참조를 전달합니다.
	        }
	        if (page.equals("HomeCardLayout")) {
	        	HomeCardLayoutCtrl HC = loader.getController();
	        	HC.startHome(); // 현재 컨트롤러의 참조를 전달합니다.
	        }

			bp.setCenter(root);
		} catch (IOException ex) {
			Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);        }     }
	
	private void SidebarCtrl() {
		pane1.setVisible(false);

		FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), pane1);
		fadeTransition.setFromValue(1);
		fadeTransition.setToValue(0);
		fadeTransition.play();

		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), pane2);
		translateTransition.setByX(-600);
		translateTransition.play();

		menu.setOnMouseClicked(event -> {

			pane1.setVisible(true);

			FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
			fadeTransition1.setFromValue(0);
			fadeTransition1.setToValue(0.15);
			fadeTransition1.play();

			TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
			translateTransition1.setByX(+600);
			translateTransition1.play();

		});
		pane1.setOnMouseClicked(event -> {
			FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
			fadeTransition1.setFromValue(0.15);
			fadeTransition1.setToValue(0);
			fadeTransition1.play();
			
			fadeTransition1.setOnFinished(event1 ->{
				pane1.setVisible(false);
			});
			
			TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
			translateTransition1.setByX(-600);
			translateTransition1.play();
		});
	}
	private void closeSideBar() {
		FadeTransition fadeTransition1 = new FadeTransition(Duration.seconds(0.5), pane1);
		fadeTransition1.setFromValue(0.15);
		fadeTransition1.setToValue(0);
		fadeTransition1.play();
		
		fadeTransition1.setOnFinished(event1 ->{
			pane1.setVisible(false);
		});
		
		TranslateTransition translateTransition1 = new TranslateTransition(Duration.seconds(0.5), pane2);
		translateTransition1.setByX(-600);
		translateTransition1.play();
	}

}
