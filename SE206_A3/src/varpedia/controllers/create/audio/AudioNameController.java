package varpedia.controllers.create.audio;

import java.io.File;
import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import varpedia.scene.AppWindow;
import varpedia.controllers.AssociationClass;
import varpedia.tasks.GenerateWaveFileTask;
import varpedia.Varpedia;

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

	@FXML
	private ImageView loadingGif;

	private String userInput;
	private String fileName;

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

		Image image = new Image("/varpedia/images/loading.gif");
		loadingGif.setImage(image);

		userInput = textBar.getText();

		char[] chars = userInput.toCharArray();

		// Check for inavlid chars in the name
		if(isFileNameValid(chars) ) {
			AppWindow.valueOf("AudioName").setScene(e);
			return;
		}


		fileName = "./creation_files/temporary_files/audio_files/"+ userInput +".wav";

		File file = new File(fileName);

		// If the file exists, give the user the option to override it
		if(file.exists() && file.isFile()) {

			loadingGif.setImage(null);

			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Audio File Already Exists");
			fileExists.setHeaderText("An audio file with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing file?");
			fileExists.showAndWait().ifPresent(selection -> {

				// override
				if(selection == ButtonType.OK) {					

					generateWaveFile(e);
				}

				// don't override
				else {

					try {
						AppWindow.valueOf("AudioName").setScene(e);
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			});

		}


		// if file with given name doesn't exist 
		else {
			generateWaveFile(e);

		}
	}

	/**
	 * The method checks if the user input (file name)
	 * only contains valid characters (alphanumeric, 
	 * hyphens, and underscores)
	 */
	private boolean isFileNameValid(char[] chars) {

		for(char Char : chars) {
			if (!Character.isDigit(Char) && !Character.isLetter(Char) && Char != '-' && Char != '_') {
				loadingGif.setImage(null);

				Alert invalidName = new Alert(Alert.AlertType.ERROR);
				invalidName.setTitle("Invalid Audio File Name");
				invalidName.setHeaderText("You cannot save an audio file with the character '" + Char + "' in its name!");
				invalidName.setContentText("Kindly enter a different name.");
				invalidName.showAndWait();	

				return true;
			}
		}

		return false;
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

		// Run task on different thread.
		GenerateWaveFileTask backgroundTask = new GenerateWaveFileTask(fileName, loadingGif, userInput, e);
		Varpedia.bg.submit(backgroundTask);

	}

	@FXML
	private void returnToSelectSentences(ActionEvent e) throws IOException {
		AppWindow.valueOf("SelectSentences").setScene(e);
	}
}
