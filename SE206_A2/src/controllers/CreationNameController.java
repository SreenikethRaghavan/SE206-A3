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
import javafx.scene.text.Text;
import wikispeak.BashProcess;


/**
 * Controller for the creation name scene,
 * where the user is allowed to enter the
 * name for their creation. 
 * 
 * Multi-threading has been implemented using 
 * Anonymous inner classes using the Task<T>
 * class. 
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class CreationNameController {

	@FXML
	private Button enterButton;

	@FXML
	private Text instructions;

	@FXML
	private TextField searchBar;

	private String userInput;


	@FXML
	private void createCreation(ActionEvent e) throws IOException {

		enterButton.setDisable(true);

		userInput = searchBar.getText();

		if(userInput == null || userInput.equals("")){
			AppWindow.valueOf("CreationName").setScene(e);
			return;
		}

		// Check for invalid creation names
		char[] chars = userInput.toCharArray();

		for(char Char : chars) {
			if (!Character.isDigit(Char) && !Character.isLetter(Char) && Char != '-' && Char != '_') {
				Alert invalidName = new Alert(Alert.AlertType.ERROR);
				invalidName.setTitle("Invalid Creation Name");
				invalidName.setHeaderText("You cannot save a creation with the character '" + Char + "' in its name!");
				invalidName.setContentText("Kindly enter a different name.");
				invalidName.showAndWait();

				AppWindow.valueOf("CreationName").setScene(e);
				return;
			}
		}



		String fileName = "./creation_files/creations/"+ userInput +".mp4";

		File file = new File(fileName);

		if(file.exists() && file.isFile()) {

			// give the user the option to override the existing creation
			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Creation Already Exists");
			fileExists.setHeaderText("A creation with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing creation?");
			fileExists.showAndWait().ifPresent(selection -> {

				if(selection == ButtonType.OK) {

					// Run the creation process on a different thread
					Task<Void> task = new Task<Void>() {
						@Override protected Void call() throws Exception {

							BashProcess creationProcess = new BashProcess();
							String command = "cat ./creation_files/temporary_files/text_files/wikipedia_output.txt | text2wave -o \"./creation_files/temporary_files/audio_files/" + userInput + ".wav\";"
									+ "temp_audio_length=$(soxi -D \"./creation_files/temporary_files/audio_files/" + userInput +".wav\");" 
									+ "audio_length=${temp_audio_length/.*};"
									+ "audio_length=$((audio_length+1));"
									+ "ffmpeg -y -f lavfi -i color=c=orange:s=500x500:d=$audio_length -vf \"drawtext=fontfile=:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text="+ AssociationClass.getInstance().getSearchTerm() + "\" \"./creation_files/temporary_files/video_files/" +userInput+".mp4\" -v quiet;"
									+ "ffmpeg -y -i \"./creation_files/temporary_files/video_files/" + userInput+".mp4\" -i \"./creation_files/temporary_files/audio_files/" + userInput + ".wav\" -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet";

							creationProcess.runCommand(command);

							return null;
						}

						@Override protected void done() {

							Platform.runLater(() -> {

								// Alert the user about the creation being successfully created
								Alert created = new Alert(Alert.AlertType.INFORMATION);

								created.setTitle("Creation Created");
								created.setHeaderText("Creation with the name '" + userInput + "' has been successfully created!");
								created.setContentText("Select the 'View Existing Creations' option to manage and play your creations.");
								created.showAndWait();

								try {
									AppWindow.valueOf("MainMenu").setScene(e);
									return;
								} catch (IOException e1) {
								}


							});
						}
					};


					Thread thread = new Thread(task);

					thread.setDaemon(true);

					thread.start();


				}

				else { // if the user doesn't want to override the file, allow them to enter a new creation name

					try {
						AppWindow.valueOf("CreationName").setScene(e);
						return;
					} catch (IOException e1) {
					}

				}
			});
		}

		else {

			// if the entered name is valid and unique, then create the creation on a different thread
			Task<Void> task = new Task<Void>() {
				@Override protected Void call() throws Exception {

					BashProcess creationProcess = new BashProcess();

					String command = "cat ./creation_files/temporary_files/text_files/wikipedia_output.txt | text2wave -o \"./creation_files/temporary_files/audio_files/" + userInput +".wav\";"
							+ "temp_audio_length=$(soxi -D \"./creation_files/temporary_files/audio_files/" + userInput +".wav\");" 
							+ "audio_length=${temp_audio_length/.*};"
							+ "audio_length=$((audio_length+1));"
							+ "ffmpeg -f lavfi -i color=c=orange:s=500x500:d=$audio_length -vf \"drawtext=fontfile=:fontsize=30: fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text="+ AssociationClass.getInstance().getSearchTerm() + "\" \"./creation_files/temporary_files/video_files/" +userInput+".mp4\" -v quiet;"
							+ "ffmpeg -y -i \"./creation_files/temporary_files/video_files/" + userInput+".mp4\" -i \"./creation_files/temporary_files/audio_files/"+userInput+".wav\" -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet";

					creationProcess.runCommand(command);

					return null;
				}

				@Override protected void done() {
					// alert user about creation being created
					Platform.runLater(() -> {

						Alert created = new Alert(Alert.AlertType.INFORMATION);

						created.setTitle("Creation Created");
						created.setHeaderText("Creation with the name '" + userInput + "' has been successfully created!");
						created.setContentText("Select the 'View Existing Creations' option to manage and play your creations.");
						created.showAndWait();

						try {
							AppWindow.valueOf("MainMenu").setScene(e);
							return;
						} catch (IOException e) {

						}


					});

				}
			};


			Thread thread = new Thread(task);

			thread.setDaemon(true);

			thread.start();
		}
	}
}

