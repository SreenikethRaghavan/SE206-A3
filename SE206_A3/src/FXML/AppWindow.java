package FXML;


/**
 * Enum used to store the paths to the 
 * various different FXML files.
 * 
 * @author Sreeniketh Raghavan
 * 
 */
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public enum AppWindow {

	MainMenu("MainMenu"), CreateMenu("CreateMenu"), CreationName("CreationName"), SearchError("SearchError"), SelectSentences("SelectSentences"), AudioFiles("AudioFiles"), 
	AudioName("AudioName"), MergeName("MergeName"), SelectImages("SelectImages"), OrderImages("OrderImages"), ViewMenu("ViewMenu"), Quiz("Quiz"), CreateCreationMenu("CreateCreationMenu"),
	LearnMenu("LearnMenu"), Memory("Memory");

	private final String fileName;

	private AppWindow(String fileName) {
		this.fileName = fileName;
	}


	/**
	 * Change the scene depending on the enum 
	 * object being referred to.
	 * 
	 */
	public void setScene(ActionEvent e) throws IOException {
		Parent sceneParent = FXMLLoader.load(getClass().getResource("/FXML/" + fileName + ".fxml"));
		Scene scene = new Scene(sceneParent);
		Stage window = (Stage) ((Node) e.getSource()).getScene().getWindow();
		window.setScene(scene);
		return;
	}
}
