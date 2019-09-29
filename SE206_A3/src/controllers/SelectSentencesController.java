package controllers;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import FXML.AppWindow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
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
	private RadioButton defaultVoiceButton;

	@FXML
	private RadioButton maleNZVoiceButton;

	@FXML
	private RadioButton femaleNZVoiceButton;

	@FXML
	private Button backButton;

	@FXML
	private Button testAudioButton;

	@FXML
	private Button createAudioButton;

	@FXML
	private Button skipStepButton;


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

	@FXML
	private void goNext(ActionEvent e) throws IOException {

		AppWindow.valueOf("AudioFiles").setScene(e);
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

		if (selectedText == null || selectedText.equals("")) {

			Alert noTextSelected = new Alert(Alert.AlertType.ERROR);
			noTextSelected.setTitle("No Text Selected");
			noTextSelected.setHeaderText("You must select some text to create an audio file from.");
			noTextSelected.setContentText("Kindly select a part/chunk of text.");
			noTextSelected.showAndWait();

			return;
		}

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


		if (defaultVoiceButton.isSelected()) {
			AssociationClass.getInstance().storeSelectedVoice("kal_diphone");
		}

		else if (maleNZVoiceButton.isSelected()) {
			AssociationClass.getInstance().storeSelectedVoice("akl_nz_jdt_diphone");
		}

		else if (femaleNZVoiceButton.isSelected()) {
			AssociationClass.getInstance().storeSelectedVoice("akl_nz_cw_cg_cg");
		}

		else {
			Alert noVoice = new Alert(Alert.AlertType.ERROR);
			noVoice.setTitle("No Voice Selected");
			noVoice.setHeaderText("You have not selected a voice!");
			noVoice.setContentText("Kindly select a voice to use for the audio file.");
			noVoice.showAndWait();

			return;
		}

		AppWindow.valueOf("AudioName").setScene(e);
		return;
	}

	@FXML
	private void testAudio(ActionEvent e) throws IOException {

		String selectedText = sentenceDisplay.getSelectedText();

		if (selectedText == null || selectedText.equals("")) {
			Alert noTextSelected = new Alert(Alert.AlertType.ERROR);
			noTextSelected.setTitle("No Text Selected");
			noTextSelected.setHeaderText("You must select some text to test the audio output.");
			noTextSelected.setContentText("Kindly select a part/chunk of text.");
			noTextSelected.showAndWait();

			return;
		}

		String[] words = selectedText.split("\\s+");

		if (words.length > 40) {
			Alert invalidWordCount = new Alert(Alert.AlertType.ERROR);
			invalidWordCount.setTitle("Word Count Exceeded");
			invalidWordCount.setHeaderText("The text you wish to play cannot exceed 40 words!");
			invalidWordCount.setContentText("Kindly select a smaller chunk of text.");
			invalidWordCount.showAndWait();

			return;
		}

		if (defaultVoiceButton.isSelected()) {
			sayText(selectedText, "kal_diphone");
			return;
		}

		else if (maleNZVoiceButton.isSelected()) {
			sayText(selectedText, "akl_nz_jdt_diphone");
			return;
		}

		else if (femaleNZVoiceButton.isSelected()) {
			sayText(selectedText, "akl_nz_cw_cg_cg");
			return;
		}

		else {
			Alert noVoice = new Alert(Alert.AlertType.ERROR);
			noVoice.setTitle("No Voice Selected");
			noVoice.setHeaderText("You have not selected a voice!");
			noVoice.setContentText("Kindly select a voice to test the text with.");
			noVoice.showAndWait();
		}

	}

	private void sayText(String text, String voice) throws IOException {

		backButton.setDisable(true);
		skipStepButton.setDisable(true);
		testAudioButton.setDisable(true);
		createAudioButton.setDisable(true);


		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess createAudio = new BashProcess();

				String command = "echo -e \"(voice_" + voice + ") ;; \\n (SayText \\\"" + text + "\\\")\" | festival -i";

				createAudio.runCommand(command);

				return null;
			}

			@Override protected void done() {

				Platform.runLater(() -> {

					backButton.setDisable(false);
					skipStepButton.setDisable(false);
					testAudioButton.setDisable(false);
					createAudioButton.setDisable(false);
				});
			}
		};

		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();		

	}

}
