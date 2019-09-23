package controllers;

import java.io.IOException;
import FXML.AppWindow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Slider;
import wikispeak.BashProcess;


/**
 * Controller for the select images scene,
 * where the user chooses the number of images
 * they wish to be in the creation slideshow
 * 
 * @author Hazel Williams
 * 
 */
public class SelectImagesController {

	@FXML
	private Slider slider;

	/**
	 * Initialise the scene. Just need to make sure the slider has the correct values.
	 * 
	 */
	@FXML
	private void initialize() throws IOException {

		slider.setMin(1);
		slider.setMax(10);
	}

	@FXML
	private void goBack(ActionEvent e) throws IOException {

		AppWindow.valueOf("CreateMenu").setScene(e);
		return;
	}


	/**
	 * Read the slider value and use it to get the number of 
	 * sentences specified by the user.
	 * 
	 */
	@FXML
	private void readImageSlider(ActionEvent e) throws IOException {
		
		//this will send the task to a new thread to download the images and create the slideshow

		int imageNum = (int)slider.getValue();
		//place holder value. TODO: get the length of the audio file, this value should be imageNum/audioLength
		Double imageDuration = ((double)imageNum)/5;


		BashProcess selectSentences = new BashProcess();
		
		//currently blank
		String command = "";

		selectSentences.runCommand(command); 
		
		//AssociationClass.getInstance().getSearchTerm() 
		
		//make a new task to get images and make the slideshow
		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess creationProcess = new BashProcess();
				
	
				//TODO: All of this will likely ultimately be moved to the CreationNameController in the end. It is here for now so it doesn't step on anyones toes.
				//line 1: set up index variable. was causing bugs (not recognizing it as a variable) when it didnt have a _ as part of it. Be sure to keep this naming format
				//line 2: download the images (# specified by the slider) to the temporary images folder
				//line 3: this currently has a placeholder value while I don't know the length of the audio.
				//line 4: This creates the initial slideshow video (no text). It is possible that we will run into some issues with the sizing of this, 
				//		  as ffmpeg is very judgey about the dimensions of the input, and currently to placate it its using integer rounding to achieve
				//		  even numbers. This may be leading to inconsistent sizing, but its hard to tell because of the scale. Its possible the size
				//	      is consistent as all images are sourced from the _b.jpgs on twitter (corresponds to the size of the image) Hoping that is the
				//		  case atm lol.
				//line 5: (-y -> force override) adds text to the slideshow video. This has to be done in a separate rendering as ffmpeg apparently doesn't like doing lots of things at once.
				//line 6: cleaning up the extra files, deleting images and extra video now we are done with them 
				String command = "index_num=1;"
						+ "wget -q \"https://flickr.com/search/?text="+AssociationClass.getInstance().getSearchTerm()+"\" -O-|tr '\"' '\\n' | grep \"_b.jpg\" | sed 's/\\\\//g' | grep 'live.staticflickr' | sort -u | head -n "+imageNum+" | while read url; do wget -q -O ./creation_files/temporary_files/image_files/img$(printf \"%02d\" $index_num).jpg -P ./creation_files/temporary_files/image_files \"http:$url\";index_num=$((index_num+1));done;"
						+ "image_Duration="+imageDuration+";"
						+ " ffmpeg -r $image_Duration -i  ./creation_files/temporary_files/image_files/img%02d.jpg -vf \"scale=trunc(iw/4)*2:trunc(ih/4)*2\" -c:v libx264 -r 30 -pix_fmt yuv420p ./creation_files/temporary_files/video_files/outputishere.mp4 > /dev/null; wait;"
						+ " ffmpeg -y -i ./creation_files/temporary_files/video_files/outputishere.mp4 -vf \"drawtext=fontfile=myfont.tff:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+AssociationClass.getInstance().getSearchTerm()+"'\" -codec:a copy ./creation_files/temporary_files/video_files/output.mp4 > /dev/null; wait;"
						+ " rm -f ./creation_files/temporary_files/image_files/*; rm -f ./creation_files/temporary_files/video_files/outputishere.mp4;";
				
				creationProcess.runCommand(command);

				return null;
			}

			@Override protected void done() {
				// alert user about creation being created
				Platform.runLater(() -> {

					Alert created = new Alert(Alert.AlertType.INFORMATION);

					created.setTitle("Slideshow Created");
					created.setHeaderText("Wow that actually worked!");
					created.setContentText("Select the 'View Existing Creations' option to manage and play your creations.");
					created.showAndWait();

					try {
						AppWindow.valueOf("CreationName").setScene(e);
						return;
					} catch (IOException e1) {
					}

				});

			}
		};
		
		Thread thread = new Thread(task);

		//doesn't need to keep running if the program is exited?
		thread.setDaemon(true);

		thread.start();
		
		
		//AppWindow.valueOf("CreationName").setScene(e);
		return;


	}


}
