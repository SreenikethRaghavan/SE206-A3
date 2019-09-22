package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import FXML.AppWindow;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
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

		BufferedReader reader = new BufferedReader(new FileReader("./creation_files/temporary_files/text_files/wikipedia_output.txt"));

		List<String> lines = new ArrayList<String>();
		String sentence;


		while ((sentence = reader.readLine()) != null) {
			lines.add(sentence);
		}

		reader.close();

		List<String> searchResult = new ArrayList<String>();

		for (String line : lines) {
			sentence  = line + "\n";
			searchResult.add(sentence);
		}


		String result = "";
		for (String line : searchResult) {
			result += line;
		}

		sentenceDisplay.setText(result);

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
	private void createAudio(ActionEvent e) throws IOException {

		String selectedText = sentenceDisplay.getSelectedText();

		String[] words = selectedText.split("\\s+");

		if (words.length > 40) {
			Alert invalidWordCount = new Alert(Alert.AlertType.ERROR);
			invalidWordCount.setTitle("Word Count Exceeded");
			invalidWordCount.setHeaderText("The text you wish to play cannot exceed 40 words!");
			invalidWordCount.setContentText("Kindly select a smaller chunk of text.");
			invalidWordCount.showAndWait();

			return;
		}

		AssociationClass.getInstance().storeSelectedText(selectedText);

		AppWindow.valueOf("AudioName").setScene(e);
		return;
	}

	@FXML
	private void testAudio(ActionEvent e) throws IOException {

		String selectedText = sentenceDisplay.getSelectedText();

		String[] words = selectedText.split("\\s+");

		if (words.length > 40) {
			Alert invalidWordCount = new Alert(Alert.AlertType.ERROR);
			invalidWordCount.setTitle("Word Count Exceeded");
			invalidWordCount.setHeaderText("The text you wish to play cannot exceed 40 words!");
			invalidWordCount.setContentText("Kindly select a smaller chunk of text.");
			invalidWordCount.showAndWait();

			return;
		}

		String command = "echo -e \" " + selectedText + "\" | festival --tts";
		BashProcess testAudio = new BashProcess();
		testAudio.runCommand(command);
		return;

	}


}
