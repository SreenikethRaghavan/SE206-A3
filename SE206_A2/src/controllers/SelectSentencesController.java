package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import FXML.AppWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import wikispeak.BashProcess;


/**
 * Controller for the select sentences scene,
 * where the user is allowed to view the results
 * and choose the number of sentences they wish to 
 * keep. 
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class SelectSentencesController {

	@FXML
	private Slider slider;

	@FXML
	private TextArea sentenceDisplay;


	/**
	 * Initialise the scene. Read the text file and display the 
	 * sentences in a numbered format in the Text Area. 
	 * 
	 */
	@FXML
	private void initialize() throws IOException {

		sentenceDisplay.setEditable(false);

		BufferedReader reader = new BufferedReader(new FileReader("./creation_files/temporary_files/text_files/wikipedia_output.txt"));

		List<String> lines = new ArrayList<String>();
		String sentence;

		int numLines = 0;
		while ((sentence = reader.readLine()) != null) {
			lines.add(sentence);
			numLines++;
		}

		reader.close();

		List<String> searchResult = new ArrayList<String>();

		int index = 1;
		for (String line : lines) {
			sentence  = index +". " + line + "\n";
			searchResult.add(sentence);
			index++;
		}


		String result = "";
		for (String line : searchResult) {
			result += line;
		}

		sentenceDisplay.setText(result);

		slider.setMin(1);
		slider.setMax((double)numLines);
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
	private void readSlider(ActionEvent e) throws IOException {

		int sentenceNum = (int)slider.getValue();


		BashProcess selectSentences = new BashProcess();

		String command = "echo \"`head -n " + sentenceNum + " ./creation_files/temporary_files/text_files/wikipedia_output.txt`\" > ./creation_files/temporary_files/text_files/wikipedia_output.txt";


		selectSentences.runCommand(command); 

		AppWindow.valueOf("CreationName").setScene(e);
		return;


	}


}
