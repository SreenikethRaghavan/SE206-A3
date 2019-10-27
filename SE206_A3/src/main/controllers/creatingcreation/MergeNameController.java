package main.controllers.creatingcreation;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import main.FXML.AppWindow;
import main.controllers.AssociationClass;
import main.wikispeak.BashProcess;

/**
 * Controller for the Merge Name scene,
 * where the user is allowed to enter a 
 * name for the merged audio file.
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class MergeNameController {

	@FXML
	private TextField textBar;

	@FXML
	private Button saveButton;

	private String userInput;

	private List<String> mergeList;

	@FXML
	private void initialize() {

		String defaultName = "combined_audio";

		String fileName = "./creation_files/temporary_files/audio_files/"+ defaultName +".wav";
		File file = new File(fileName);

		// if a file with the suggested default name already exists then keep generating 
		// new names with numbers at the end of them till a unique file name is generated 

		int fileCount = 1;

		while(file.exists() && file.isFile()) {

			defaultName = "combined_audio_" + fileCount;

			fileName = "./creation_files/temporary_files/audio_files/"+ defaultName +".wav";
			file = new File(fileName);

			fileCount++;
		}

		// auto-fill the text field with the default name so that the user doesn't have to 
		// type a new name every time
		textBar.setText(defaultName);

		saveButton.disableProperty().bind(
				Bindings.isEmpty(textBar.textProperty()));
	}

	@FXML
	private void mergeAudioFiles(ActionEvent e) throws IOException {

		userInput = textBar.getText();

		// Get the list of files in the order they wish to merge them
		mergeList = AssociationClass.getInstance().getFilesToMerge();

		if (mergeList.contains(userInput)) {

			Alert sameFile = new Alert(Alert.AlertType.ERROR);
			sameFile.setTitle("Invalid Audio File Name");
			sameFile.setHeaderText("You cannot save an audio file with the same name as one of the files being merged!");
			sameFile.setContentText("Kindly enter a different name.");
			sameFile.showAndWait();

			AppWindow.valueOf("MergeName").setScene(e);		

			return;
		}


		char[] chars = userInput.toCharArray();

		// Check for invalid chars in the name
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

	/**
	 *The main merging method called to merge
	 *multiple audio files together.
	 * 
	 */
	private void merging() {

		AssociationClass.getInstance().storeAudioFile(userInput);

		BashProcess playAudio = new BashProcess();

		String path = "\"./creation_files/temporary_files/audio_files/";

		// use the sox bash command to merge multiple audio files together
		String command = "sox ";


		// Add path of each individual file
		for (String audioFile : mergeList) {

			command += path + audioFile + ".wav\" ";
		}

		// add path of the combined file
		command += path + userInput + ".wav\"";

		playAudio.runCommand(command);

		Alert created = new Alert(Alert.AlertType.INFORMATION);

		created.setTitle("Audio File Created");
		created.setHeaderText("Combined Audio File with the name '" + userInput + "' has been successfully created!");
		created.showAndWait();

	}

}
