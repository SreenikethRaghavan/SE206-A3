package controllers;

import java.io.IOException;

import FXML.AppWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import wikispeak.BashProcess;


/**
 * Controller for the main menu scene,
 * where the user is allowed to create or
 * view creations.
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class MainMenuController {

	@FXML
	private AnchorPane anchorPane;

	@FXML
	private Button createButton;

	@FXML
	private Button viewButton;

	@FXML
	private void initialize() {

		BashProcess process = new BashProcess();

		String command = "mkdir -p ./creation_files/temporary_files/audio_files; mkdir -p ./creation_files/temporary_files/video_files; "
				+ "mkdir -p ./creation_files/temporary_files/text_files; mkdir -p ./creation_files/creations; mkdir -p ./creation_files/temporary_files/image_files;";

		process.runCommand(command);
	}

	@FXML
	private void createCreation(ActionEvent event) throws IOException {

		AppWindow.valueOf("CreateMenu").setScene(event);
		return;

	} 


	@FXML
	private void viewCreations(ActionEvent event) throws IOException {

		AppWindow.valueOf("ViewMenu").setScene(event);
		return;

	} 

}
