package MultiRoom;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;

public class AppMain extends Application {

	@Override
	public void start(Stage primaryStage) throws Exception {

		FXMLLoader loader = new FXMLLoader(getClass().getResource("root.fxml"));
		Parent root=loader.load();
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setResizable(false); // 윈도우 크기를 조정할 수 없도록 함
		primaryStage.show();
		
		RootController rootController = loader.getController();
		primaryStage.setOnCloseRequest((windowEvent)->{
			rootController.logout(); //자동 로그아웃
			Platform.exit(); //모든 플랫폼 닫기
		});
	}

	public static void main(String[] args) {
		launch(args);
	}

}