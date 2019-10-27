package varpedia.controllers;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import varpedia.scene.AppWindow;
import varpedia.processes.BashProcess;


/**
 * Controller for the main menu scene,
 * where the user is allowed to create or
 * view creations.
 * 
 * @author Sreeniketh Raghavan
 * @author Hazel Williams
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
		try {
			AssociationClass.getInstance().loadProgess();
		} catch (IOException e) {
			e.printStackTrace();
		}

		BashProcess process = new BashProcess();

		String command = "mkdir -p ./creation_files/temporary_files/audio_files; mkdir -p ./creation_files/temporary_files/video_files; "
				+ "mkdir -p ./creation_files/temporary_files/text_files; mkdir -p ./creation_files/creations; mkdir -p ./creation_files/temporary_files/image_files; "
				+ "rm -f ./creation_files/temporary_files/image_files/*; mkdir -p ./creation_files/quiz_files/quiz_images; mkdir -p ./creation_files/memory_files;";

		process.runCommand(command);
	}

	@FXML
	private void createCreation(ActionEvent event) throws IOException {
		if(AssociationClass.getInstance().getSearchTerm().contentEquals("  ")) {
			AppWindow.valueOf("CreateMenu").setScene(event);
		} else {
			AppWindow.valueOf("CreateCreationMenu").setScene(event);
		}
		
		return;

	} 


	@FXML
	private void viewCreations(ActionEvent event) throws IOException {

		AppWindow.valueOf("ViewMenu").setScene(event);
		return;

	} 

	@FXML
	private void learnStuff(ActionEvent event) throws IOException {
		AppWindow.valueOf("LearnMenu").setScene(event);
		//AppWindow.valueOf("Quiz").setScene(event);
		return;
	}

}
