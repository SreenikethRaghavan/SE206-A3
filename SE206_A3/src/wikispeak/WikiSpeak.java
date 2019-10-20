package wikispeak;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


/**
 * Class containing the main method
 * to launch the stage with the main
 * menu scene. 
 * 
 * Entry point to the application.
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class WikiSpeak extends Application {

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		Parent root = FXMLLoader.load(getClass().getResource("/FXML/MainMenu.fxml"));		
		primaryStage.setTitle("VARpedia");
		primaryStage.sizeToScene();
		primaryStage.setResizable(false);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
	}
}
