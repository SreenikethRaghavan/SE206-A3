package controllers;

import java.io.File;
import java.io.IOException;

import FXML.AppWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import wikispeak.BashProcess;

public class AudioNameController {

	@FXML
	private TextField textBar;

	@FXML
	private void createAudioFile(ActionEvent e) throws IOException {

		String userInput = textBar.getText();

		if(userInput == null || userInput.equals("")){
			AppWindow.valueOf("AudioName").setScene(e);
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

				AppWindow.valueOf("AudioName").setScene(e);
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


					BashProcess selectSentences = new BashProcess();

					String command = "echo -e " + AssociationClass.getInstance().getSelectedText() + " | text2wave -o \"./creation_files/temporary_files/audio_files/"
							+ userInput + ".wav\"";

					selectSentences.runCommand(command);

					Alert created = new Alert(Alert.AlertType.INFORMATION);

					created.setTitle("Audio File Created");
					created.setHeaderText("Audio File with the name '" + userInput + "' has been successfully created!");
					created.setContentText("You can now listen to the audio file you have created and can merge multiple audio file together.");
					created.showAndWait();

					try {
						AppWindow.valueOf("AudioFiles").setScene(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

				else {


					try {
						AppWindow.valueOf("AudioName").setScene(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			});
		}


		else {
			BashProcess selectSentences = new BashProcess();

			String command = "echo -e " + AssociationClass.getInstance().getSelectedText() + " | text2wave -o \"./creation_files/temporary_files/audio_files/"
					+ userInput + ".wav\"";

			selectSentences.runCommand(command);

			Alert created = new Alert(Alert.AlertType.INFORMATION);

			created.setTitle("Audio File Created");
			created.setHeaderText("Audio File with the name '" + userInput + "' has been successfully created!");
			created.setContentText("You can now listen to the audio file you have created and can merge multiple audio file together.");
			created.showAndWait();

			AppWindow.valueOf("AudioFiles").setScene(e);
		}

	}
}
