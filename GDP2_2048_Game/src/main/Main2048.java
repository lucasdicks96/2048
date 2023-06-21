package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main2048 extends Application {

	  public void start(Stage primaryStage) throws Exception {
	        FXMLLoader loader = new FXMLLoader(getClass().getResource("game2048.fxml"));
	        Parent root = loader.load();
	        Scene scene = new Scene(root);

	        Game2048Controller controller = loader.getController();
	        controller.setScene(scene);

	        primaryStage.setScene(scene);
	        primaryStage.setTitle("2048 Game");
	        primaryStage.show();
	    }
	  

	public static void main(String[] args) {
		launch(args);
	}
}
