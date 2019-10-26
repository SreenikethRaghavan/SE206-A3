package main.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import main.WikiSpeak;
import main.FXML.AppWindow;
import main.tasks.DownloadImagesTask;



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
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Button enterButton;
	
	@FXML
	private Button backButton;

	/**
	 * Initialise the scene. Just need to make sure the slider has the correct values.
	 * 
	 */
	@FXML
	private void initialize() throws IOException {
		
		progressBar.setVisible(false);
		progressBar.setProgress(0.0);
		
		enterButton.setDisable(false);
		backButton.setDisable(false);

		slider.setMin(1);
		slider.setMax(10);
	}
	
	/**
	 * Return to previous scene.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void goBack(ActionEvent e) throws IOException {

		AppWindow.valueOf("AudioFiles").setScene(e);
		return;
	}
	
	/**
	 * This is what enables the use of the Flickr API.
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static String getAPIKey(String key) throws Exception {

		String config = System.getProperty("user.dir") 
				+ System.getProperty("file.separator")+ "flickr-api-keys.txt"; 
		File file = new File(config); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith(key)) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());
	}
	

	/**
	 * Read the slider value and use it to get the number of 
	 * sentences specified by the user.
	 * Then use that number to specify the number of images we wish to download.
	 * Finally, resize the images to the dimension of the video, padding to make sure to respect the original dimensions of the images.
	 */
	@FXML
	private void readImageSlider(ActionEvent e) throws IOException {
		
		progressBar.setVisible(true);
		enterButton.setDisable(true);
		backButton.setDisable(true);
		
		int imageNum = (int)slider.getValue();
	
		//place holder value. TODO: get the length of the audio file, this value should be imageNum/audioLength
		//this is no longer used.
		Double imageDuration = ((double)imageNum)/5.0;
		
		//make a new task to get images
		DownloadImagesTask imageTask = new DownloadImagesTask(imageNum, progressBar, imageDuration, e);
		WikiSpeak.bg.submit(imageTask);
		
		
		return;


	}


}
