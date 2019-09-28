package controllers;

import java.io.File;
import java.io.IOException;
import java.util.List;

import FXML.AppWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import wikispeak.BashProcess;

public class MergeNameController {

	@FXML
	private TextField textBar;

	private String userInput;

	@FXML
	private void mergeAudioFiles(ActionEvent e) throws IOException {

		userInput = textBar.getText();

		if(userInput == null || userInput.equals("")){
			AppWindow.valueOf("MergeName").setScene(e);
			return;
		}


		char[] chars = userInput.toCharArray();

		for(char Char : chars) {
			if (!Character.isDigit(Char) && !Character.isLetter(Char) && Char != '-' && Char != '_') {
				Alert invalidName = new Alert(Alert.AlertType.ERROR);
				invalidName.setTitle("Invalid Audio File Name");
				invalidName.setHeaderText("You cannot save an audio file with the character '" + Char + "' in its name!");
				invalidName.setContentText("Kindly enter a different name.");
				invalidName.showAndWait();

				AppWindow.valueOf("MergeName").setScene(e);
				return;
			}
		}


		String fileName = "./creation_files/temporary_files/audio_files/"+ userInput +".wav";

		File file = new File(fileName);

		if(file.exists() && file.isFile()) {

			// give the user the option to override the existing audio file
			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Audio File Already Exists");
			fileExists.setHeaderText("An audio file with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing file?");
			fileExists.showAndWait().ifPresent(selection -> {

				if(selection == ButtonType.OK) {

					merging();

					try { 
						AppWindow.valueOf("SelectImages").setScene(e); 
					} 

					catch (IOException e1) { 
						e1.printStackTrace(); 
					}

				}

				else {


					try {
						AppWindow.valueOf("MergeName").setScene(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			});
		}


		else {

			merging();

			AppWindow.valueOf("SelectImages").setScene(e);
		}

	}

	@FXML
	private void returnToAudioFiles(ActionEvent e) throws IOException {

		AppWindow.valueOf("AudioFiles").setScene(e);
		return;
	}

	private void merging() {

		AssociationClass.getInstance().storeAudioFile(userInput);

		BashProcess playAudio = new BashProcess();

		String path = "\"./creation_files/temporary_files/audio_files/";

		String command = "sox ";

		List<String> mergeList = AssociationClass.getInstance().getFilesToMerge();		


		for (String audioFile : mergeList) {

			command += path + audioFile + ".wav\" ";
		}

		command += path + userInput + ".wav\"";

		playAudio.runCommand(command);

		Alert created = new Alert(Alert.AlertType.INFORMATION);

		created.setTitle("Audio File Created");
		created.setHeaderText("Combined Audio File with the name '" + userInput + "' has been successfully created!");
		created.showAndWait();

	}

}
