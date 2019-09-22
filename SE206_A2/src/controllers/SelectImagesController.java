package controllers;

import java.io.IOException;
import FXML.AppWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
		slider.setMax(12);
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

		//int imageNum = (int)slider.getValue();


		BashProcess selectSentences = new BashProcess();
		
		//currently blank
		String command = "";


		selectSentences.runCommand(command); 

		AppWindow.valueOf("CreationName").setScene(e);
		return;


	}


}
