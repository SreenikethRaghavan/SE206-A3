package controllers;

import java.io.File;
import java.io.IOException;

import FXML.AppWindow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import wikispeak.BashProcess;

public class AudioNameController {

	@FXML
	private TextField textBar;

	@FXML
	private Button enterButton;

	@FXML
	private Button backButton;

	private String userInput;

	private String fileName;

	@FXML
	private void createAudioFile(ActionEvent e) throws IOException {

		userInput = textBar.getText();

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


		fileName = "./creation_files/temporary_files/audio_files/"+ userInput +".wav";

		File file = new File(fileName);

		if(file.exists() && file.isFile()) {

			// give the user the option to override the existing audio file
			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Audio File Already Exists");
			fileExists.setHeaderText("An audio file with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing file?");
			fileExists.showAndWait().ifPresent(selection -> {

				if(selection == ButtonType.OK) {					

					generateWavFile(e);
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

			generateWavFile(e);

		}
	}

	@FXML
	private void generateWavFile(ActionEvent e) {

		backButton.setDisable(true);
		enterButton.setDisable(true);

		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess createAudio = new BashProcess();

				String selectedText = AssociationClass.getInstance().getSelectedText();
				String selectedVoice = AssociationClass.getInstance().getSelectedVoice();

				String command = "echo -e \"" + selectedText + "\" | text2wave -eval \"(voice_" + selectedVoice + ")\" > " + fileName;

				createAudio.runCommand(command);

				return null;
			}

			@Override protected void done() {

				Platform.runLater(() -> {
					Alert created = new Alert(Alert.AlertType.INFORMATION);

					created.setTitle("Audio File Created");
					created.setHeaderText("Audio File with the name '" + userInput + "' has been successfully created!");
					created.setContentText("You can now listen to the audio file you have created and can merge multiple audio files together.");
					created.showAndWait();

					try {
						AppWindow.valueOf("AudioFiles").setScene(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				});
			}
		};

		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();
	}

	@FXML
	private void returnToSelectSentences(ActionEvent e) throws IOException {
		AppWindow.valueOf("SelectSentences").setScene(e);
	}
}
