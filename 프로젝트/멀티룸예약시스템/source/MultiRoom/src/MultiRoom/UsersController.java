package MultiRoom;

import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class UsersController implements Initializable {
    @FXML private Button btn1, btn2, btn3, btnMain;
    @FXML private TextField tfId, tfPw, tfEmail, tfName, tfType;
    @FXML private TableView<Users> tableView;
    @FXML private TableColumn<Users, String> tcId, tcPw, tcEmail, tcName , tcType;

    // 서버에서 가져온 사용자 데이터를 저장하는 리스트
    private ObservableList<Users> usersList = FXCollections.observableArrayList();

    private ClientConnector client = new ClientConnector();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TableColumn과 Users 객체의 필드를 바인딩
        tcId.setCellValueFactory(new PropertyValueFactory<>("id"));
        tcPw.setCellValueFactory(new PropertyValueFactory<>("password"));
        tcEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        tcName.setCellValueFactory(new PropertyValueFactory<>("name"));
        tcType.setCellValueFactory(new PropertyValueFactory<>("user_type"));;
        

        // 테이블에 ObservableList 설정
        tableView.setItems(usersList);

        // 버튼 이벤트 핸들러 설정
        btn1.setOnAction(event -> {
            try {
                total();  // 서버에서 데이터 가져오기
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btn2.setOnAction(event -> {
            try {
                add();  // 사용자 추가 요청
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        btn3.setOnAction(event -> {
			try {
				del();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});  // 사용자 삭제 요청
        btnMain.setOnAction(event->handleBtnMain(event));
        
        // 테이블의 선택된 행이 바뀔 때마다 TextField에 해당 데이터 표시
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                tfId.setText(newSelection.getId());
                tfName.setText(newSelection.getName());
                tfEmail.setText(newSelection.getEmail());
                tfPw.setText(newSelection.getPassword());
                tfType.setText(newSelection.getUser_type());
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

    // 서버에서 사용자 목록을 가져오는 메서드
    public void total() throws Exception {
        client.connect();

        client.setExceptionHandler(e -> {
            client.disconnect();
            showError("서버 연결 오류");
        });

        // 서버에 getUsers 요청
        client.requestAndReponseSync("getUsers", "params", response -> {
            try {
                JSONObject json = client.parse(response);
                String status = (String) json.get("status");
                switch (status) {
                    case "success":
                        String encryptedData = (String) json.get("data");
                        String originData = client.decryptReceivedData(encryptedData);
                        
                        // 받은 데이터를 Users 객체 리스트로 변환
                        List<Users> userList = parseUsers(originData);

                        // JavaFX UI 스레드에서 테이블 뷰 업데이트
                        Platform.runLater(() -> {
                            usersList.setAll(userList);
                        });
                        break;
                    case "fail":
                        showError("서버에서 데이터를 가져오는 데 실패했습니다.");
                        break;
                    default:
                        showError("알 수 없는 서버 응답입니다.");
                }
            } catch (Exception e) {
                System.out.println("[클라이언트] 응답 처리 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                showError("응답 처리 중 오류 발생");
            }
        });
        client.disconnect();
    }

    // 서버에 사용자 추가 요청하는 메서드
    public void add() throws Exception {
        
        String id = tfId.getText().trim();
        String password = tfPw.getText().trim();
        String email = tfEmail.getText().trim();
        String name = tfName.getText().trim();
        String user_type = tfType.getText().trim();

        if (id.isEmpty() || password.isEmpty() || email.isEmpty() || name.isEmpty() || user_type.isEmpty()) {
            showError("모든 필드를 입력해야 합니다.");
            return;
        }	

        // 서버로 전송할 JSON 데이터 생성
        JSONObject jsonData = new JSONObject();
        jsonData.put("id", id);
        jsonData.put("password", password);
        jsonData.put("email", email);
        jsonData.put("name", name);
        jsonData.put("user_type", user_type);

        client.connect();

        client.setExceptionHandler(event -> {
            client.disconnect();
            showError("서버 연결 오류");
        });

        // 서버에 사용자 추가 요청
        client.requestAndReponseSync("insertUser", jsonData.toJSONString(), response -> {
            JSONObject json = client.parse(response);
            String status = json.get("status").toString();
            switch (status) {
                case "success":
                    Platform.runLater(() -> {
                        try {
							total();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}  // 성공적으로 추가되었으면 사용자 목록을 다시 로드
                    });
                    break;
                case "fail":
                    showError("사용자 추가 중 오류가 발생했습니다.");
                    break;
            }
        });
        client.disconnect();
    }

    // 서버에 사용자 삭제 요청하는 메서드
    public void del() throws Exception {
        Users selectedUser = tableView.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showError("삭제할 사용자를 선택하세요.");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("사용자 삭제");
        confirmDialog.setHeaderText("아이디: " + selectedUser.getId() + ", 이름: " + selectedUser.getName() + "를 삭제합니다.");
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
            jsonData.put("id", selectedUser.getId());

            client.requestAndReponseSync("deleteUser", jsonData.toJSONString(), response -> {
                JSONObject json = client.parse(response);
                String status = json.get("status").toString();
                System.out.println(status);
                switch (status) {
                    case "success":
                        Platform.runLater(() -> {
                            usersList.remove(selectedUser);  // 테이블에서 사용자 제거
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

 // JSON 데이터를 Users 리스트로 변환하는 메서드
    private List<Users> parseUsers(String jsonData) {
        List<Users> userList = new ArrayList<>();
        try {
            Object parsedData = new JSONParser().parse(jsonData);

            // 응답이 JSONArray인지 JSONObject인지 확인
            if (parsedData instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) parsedData;
                for (Object obj : jsonArray) {
                    JSONObject jsonUser = (JSONObject) obj;
                    Users user = parseUserObject(jsonUser);
                    userList.add(user);
                }
            } else if (parsedData instanceof JSONObject) {
                JSONObject jsonObject = (JSONObject) parsedData;

                // JSONObject일 경우, JSONArray로 변환할 수 있는지 확인
                Object users = jsonObject.get("users"); // "users" 키로 데이터를 가져온다고 가정
                if (users instanceof JSONArray) {
                    JSONArray jsonArray = (JSONArray) users;
                    for (Object obj : jsonArray) {
                        JSONObject jsonUser = (JSONObject) obj;
                        Users user = parseUserObject(jsonUser);
                        userList.add(user);
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
        return userList;
    }

    private Users parseUserObject(JSONObject jsonUser) {
        String id = (String) jsonUser.get("id");
        String password = (String) jsonUser.get("password");
        String email = (String) jsonUser.get("email");
        String name = (String) jsonUser.get("name");
        String user_Type = (String) jsonUser.get("user_type");

        return new Users(id, password, email, name, user_Type);
    }
    // 오류 메시지를 보여주는 메서드
    private void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.CLOSE);
            alert.showAndWait();
        });
    }
    

}