package controllers;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
 * @author Hazel Williams
 * 
 */
public class CreationNameController {

	@FXML
	private Button enterButton;

	@FXML
	private TextField searchBar;

	@FXML
	private ImageView loadingGif;

	private String userInput;

	boolean backgroundMusic;

	private String backMusic = "funkTest.mp3";

	@FXML
	private void initialize() {

		backgroundMusic = AssociationClass.getInstance().isBGMusic();

		String searchTerm = AssociationClass.getInstance().getSearchTerm();

		String defaultName = searchTerm + "_creation";
		defaultName = defaultName.replace(' ', '_');

		String fileName = "./creation_files/creations/"+ defaultName +".mp4";
		File file = new File(fileName);

		int fileCount = 1;

		while(file.exists() && file.isFile()) {

			defaultName = searchTerm + "_creation_" + fileCount;
			defaultName = defaultName.replace(' ', '_');

			fileName = "./creation_files/creations/"+ defaultName +".mp4";
			file = new File(fileName);

			fileCount++;
		}

		searchBar.setText(defaultName);
		enterButton.setDisable(false);
		enterButton.disableProperty().bind(
				Bindings.isEmpty(searchBar.textProperty()));
	}

	//task to actually create the creation is in this, to avoid code duplication between overwrite/don't overwrite case.
	private void makeCreation(ActionEvent e) {

		Image image = new Image("/images/loading.gif");
		loadingGif.setImage(image);

		// if the entered name is valid and unique, or they want to override, then create the creation on a different thread	
		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess creationProcess = new BashProcess();

				//storing the names of the file in advance for code readability reasons
				String audioFileName = "creation_files/temporary_files/audio_files/" + AssociationClass.getInstance().getAudioFile() + ".wav";
				String videoFileName = "creation_files/temporary_files/video_files/output.mp4";

				//get the audio length
				//soxi wasn't behaving, so I have gone with a java based approach.
				File audioFile = new File(audioFileName);

				AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);					
				AudioFormat format = audioInputStream.getFormat();							

				long audioFileLength = audioFile.length();

				int frameSize = format.getFrameSize();

				float audioframeRate = format.getFrameRate();

				float durationInSeconds = (audioFileLength / (frameSize * audioframeRate));


				//calculate the image duration and round it to something ffmpeg will tolerate. 3dp accuracy is sufficient for matching the audio length to the slideshow length.
				double frameRate = AssociationClass.getInstance().getNumImages()/durationInSeconds;
				frameRate = Math.round(frameRate*1000.0)/1000.0;

				//default font size is 60
				int fontSize = 60;

				if(AssociationClass.getInstance().getSearchTerm().length() >= 10) {
					fontSize = 40;
				}

				if(AssociationClass.getInstance().getSearchTerm().length() >= 16) {
					fontSize = 30;
				}

				if(AssociationClass.getInstance().getSearchTerm().length() > 20) {
					//this should be able to fit ~30 characters so. should be sufficient.
					fontSize = 20;
				}


				//This command is where both the slideshow and the final creation is generated
				//line 1: establish the image_Duration variable. As this variable is only used in one place in the following code, this could be refactored out. 
				//		  However its working at the moment so no point breaking it.
				//line 2: Create the initial slide show by reading in the images, which were previously renamed and numbered in order in OrderImagesController. 
				//		  This output will be deleted after it is used.
				//line 3: Add the text to the previously generated slideshow. This is saved to the videoFileName location, and is used in the final creation. 
				//		  There is potential for customisation here with font and/or colours.
				//line 4, 5: Removing the extra files. This is important in case the user makes multiple creations in one session, as leaving these files will 
				//			 cause duplicate images in the slideshow and risk breaching Flickr's 30 image maximum user condition.
				//line 6: Combine the audio file and the video file to make the final creation video.
				String command = "image_Duration="+frameRate+";"
						+ " ffmpeg -y -framerate $image_Duration -i  ./creation_files/temporary_files/image_files/img%02d.jpg -c:v libx264 -r 24 ./creation_files/temporary_files/video_files/outputishere.mp4  > /dev/null; wait;"
						+ " ffmpeg -y -i ./creation_files/temporary_files/video_files/outputishere.mp4 -vf \"drawtext=fontfile=myfont.tff:fontsize="+fontSize+":fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+AssociationClass.getInstance().getSearchTerm()+"'\" -codec:a copy ./creation_files/temporary_files/video_files/output.mp4 > /dev/null; wait;"
						+ " rm -f ./creation_files/temporary_files/image_files/*; "
						+ " rm -f ./creation_files/temporary_files/video_files/outputishere.mp4;";


				if(backgroundMusic == true) {
					//need to add the background music.
					//resizing the sound to match the audio clip
					command = command +" ffmpeg -y -i "+backMusic+" -af apad -t "+durationInSeconds+" -filter:a \"volume=0.2\" ./creation_files/temporary_files/resizedFunk.wav; wait;";
					//merging sound
					command = command +" ffmpeg -y -i ./"+audioFileName+" -i ./creation_files/temporary_files/resizedFunk.wav -filter_complex amerge -ac 2 -c:a libmp3lame -q:a 4 ./creation_files/temporary_files/temp1.wav; wait;";
					//using sound to make creation
					command = command +" ffmpeg -y -i ./"+videoFileName+" -i ./creation_files/temporary_files/temp1.wav -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet> /dev/null; wait;";
				} else {
					//create with no background music
					command = command + " ffmpeg -y -i ./"+videoFileName+" -i ./"+audioFileName+" -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet> /dev/null; wait;";
				}

				creationProcess.runCommand(command);

				return null;
			}

			@Override protected void done() {

				Platform.runLater(() -> {

					// Alert the user about the creation being successfully created
					Alert created = new Alert(Alert.AlertType.INFORMATION);

					loadingGif.setImage(null);

					created.setTitle("Creation Created");
					created.setHeaderText("Creation with the name '" + userInput + "' has been successfully created!");
					created.setContentText("Select the 'View Existing Creations' option from the main menu to manage and play your creations.");
					created.showAndWait();

					//currently the user relies on this message to navigate away from the creation screen, in future we will revamp this so that the user can go and perform other tasks while the creation is being generated.
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


	/**
	 * createCreation is triggered by clicking the enter button.
	 * It first checks whether the provided potential file name is valid. If it isn't the user will be prompted to try again.
	 * Then it checks whether a file by that name already exists, asking the user if they wish to overwrite if so.
	 * Finally, if it has made it past all of those hurdles, it will create the creation. It uses audio and images generated
	 * and labeled from previous steps, so all that really happens in this task is the final putting it all together.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void createCreation(ActionEvent e) throws IOException {

		Image image = new Image("/images/loading.gif");
		loadingGif.setImage(image);

		//want to disable the enter button so they can't spam a lot of file creations, avoids them causing overwrite issues.
		enterButton.disableProperty().unbind();

		enterButton.setDisable(true);


		userInput = searchBar.getText();

		// Check for invalid creation names
		char[] chars = userInput.toCharArray();

		for(char Char : chars) {
			if (!Character.isDigit(Char) && !Character.isLetter(Char) && Char != '-' && Char != '_') {

				loadingGif.setImage(null);

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

			loadingGif.setImage(null);

			// give the user the option to override the existing creation
			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Creation Already Exists");
			fileExists.setHeaderText("A creation with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing creation?");
			fileExists.showAndWait().ifPresent(selection -> {

				if(selection != ButtonType.OK) {
					//if they don't want to override, give them a chance to rename it, reset the place.
					initialize();
				} else {
					//they do want to override it
					makeCreation(e);
				}
				//if they want to override it, continue on.
			});
		} else {
			//no overwrite issues? make the thing.
			makeCreation(e);
		}

	}
}

