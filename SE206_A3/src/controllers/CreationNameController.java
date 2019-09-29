package controllers;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

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
	private TextField searchBar;

	private String userInput;
	
	@FXML
	private void initialize() {
		enterButton.setDisable(false);
		searchBar.setDisable(false);
		
	}
	
	
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

				if(selection != ButtonType.OK) {
					//if they dont want to override, give them a chance to rename it
					try {
						AppWindow.valueOf("CreationName").setScene(e);
						return;
					} catch (IOException e1) {
					}
				}
			});
		}

		

		// if the entered name is valid and unique, or they want to override, then create the creation on a different thread	
		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess creationProcess = new BashProcess();


				String audioFileName = "creation_files/temporary_files/audio_files/" + AssociationClass.getInstance().getAudioFile() + ".wav";
				String videoFileName = "creation_files/temporary_files/video_files/output.mp4";

				//get the audio length
				File audioFile = new File(audioFileName);
				

				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);					
				AudioFormat format = audioInputStream.getFormat();							

				
				long audioFileLength = audioFile.length();
				
				int frameSize = format.getFrameSize();
				
				float audioframeRate = format.getFrameRate();
				
				float durationInSeconds = (audioFileLength / (frameSize * audioframeRate));
				

				//calculate the image duration
				double frameRate = AssociationClass.getInstance().getNumImages()/durationInSeconds;
				frameRate = Math.round(frameRate*1000.0)/1000.0;


				String command = "image_Duration="+frameRate+";"
						+ " ffmpeg -y -framerate $image_Duration -i  ./creation_files/temporary_files/image_files/img%02d.jpg -c:v libx264 -r 24 ./creation_files/temporary_files/video_files/outputishere.mp4  > /dev/null; wait;"
						+ " ffmpeg -y -i ./creation_files/temporary_files/video_files/outputishere.mp4 -vf \"drawtext=fontfile=myfont.tff:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+AssociationClass.getInstance().getSearchTerm()+"'\" -codec:a copy ./creation_files/temporary_files/video_files/output.mp4 > /dev/null; wait;"
						+ " rm -f ./creation_files/temporary_files/image_files/*; "
						+ " rm -f ./creation_files/temporary_files/video_files/outputishere.mp4;"
						+ " ffmpeg -y -i ./"+videoFileName+" -i ./"+audioFileName+" -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet> /dev/null; wait;";

				creationProcess.runCommand(command);

				return null;
			}

			@Override protected void done() {

				Platform.runLater(() -> {

					// Alert the user about the creation being successfully created
					Alert created = new Alert(Alert.AlertType.INFORMATION);

					created.setTitle("Creation Created");
					created.setHeaderText("Creation with the name '" + userInput + "' has been successfully created!");
					created.setContentText("Select the 'View Existing Creations' option from the main menu to manage and play your creations.");
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
}

