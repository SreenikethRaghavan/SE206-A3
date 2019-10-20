package controllers;

import java.io.File;
import java.io.IOException;

import FXML.AppWindow;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import wikispeak.BashProcess;

/**
 * Controller for the Audio Name scene,
 * where the user is allowed to enter a 
 * name for the audio file generated from 
 * the selected text.
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class AudioNameController {

	@FXML
	private TextField textBar;

	@FXML
	private Button saveButton;

	@FXML
	private Button backButton;

	private String userInput;

	private String fileName;

	private boolean audioCreationFailed = false; 

	@FXML
	private void initialize() {

		String searchTerm = AssociationClass.getInstance().getSearchTerm();

		String defaultName = searchTerm + "_audio";

		defaultName = defaultName.replace(' ', '_');

		String fileName = "./creation_files/temporary_files/audio_files/"+ defaultName +".wav";

		File file = new File(fileName);

		// if a file with the suggested default name already exists then keep generating 
		// new names with numbers at the end of them till a unique file name is generated 

		int fileCount = 1;

		while(file.exists() && file.isFile()) {

			defaultName = searchTerm + "_audio_" + fileCount;

			defaultName = defaultName.replace(' ', '_');

			fileName = "./creation_files/temporary_files/audio_files/"+ defaultName +".wav";

			file = new File(fileName);

			fileCount++;
		}

		// auto-fill the text field with the default name so that the user 		
		// doesn't have to type a new name every time
		textBar.setText(defaultName);

		// bind the save button to the text field so that it is disabled 
		// when the text field is empty
		saveButton.disableProperty().bind(
				Bindings.isEmpty(textBar.textProperty()));
	}


	@FXML
	private void createAudioFile(ActionEvent e) throws IOException {

		userInput = textBar.getText();

		char[] chars = userInput.toCharArray();

		// Check for inavlid chars in the name
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

		// If the file exists, give the user the option to override it
		if(file.exists() && file.isFile()) {

			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Audio File Already Exists");
			fileExists.setHeaderText("An audio file with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing file?");
			fileExists.showAndWait().ifPresent(selection -> {

				if(selection == ButtonType.OK) {					

					generateWaveFile(e);
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

			generateWaveFile(e);

		}
	}

	/**
	 * The method used to create the audio file using 
	 * text2wave. Its run on a different thread to prevent 
	 * the GUI from freezing. 
	 */
	@FXML
	private void generateWaveFile(ActionEvent e) {

		saveButton.disableProperty().unbind();

		backButton.setDisable(true);
		saveButton.setDisable(true);

		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess createAudio = new BashProcess();

				String selectedText = AssociationClass.getInstance().getSelectedText();
				String selectedVoice = AssociationClass.getInstance().getSelectedVoice();

				// generate audio file using text2wave
				String command = "echo -e \"" + selectedText + "\" | text2wave -eval \"(voice_" + selectedVoice + ")\" > " + fileName;

				createAudio.runCommand(command);

				File testFile = new File(fileName);

				// If the voice cannot pronounce a word in the selected text
				if(testFile.length() == 0) {
					testFile.delete();
					audioCreationFailed = true;
				}

				return null;
			}

			@Override protected void done() {

				Platform.runLater(() -> {

					if (audioCreationFailed) {

						Alert creationFailed = new Alert(Alert.AlertType.ERROR);

						creationFailed.setTitle("Audio File Creation Failed");
						creationFailed.setHeaderText("Creation of the Audio File with the name '" + userInput + "' has unfortunately "
								+ "failed due the text-to-speech synthesizer not being able to pronounce a word in the text you selected!");
						creationFailed.setContentText("Kindly select a different part/chunk of text and test the audio output before attempting to create "
								+ "an audio file.");
						creationFailed.showAndWait();

						try {
							AppWindow.valueOf("SelectSentences").setScene(e);
						} catch (IOException e1) {
							e1.printStackTrace();
						}

						return;			
					}

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
