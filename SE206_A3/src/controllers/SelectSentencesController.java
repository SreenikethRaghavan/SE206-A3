package controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import FXML.AppWindow;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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

	private boolean audioTestFailed = false;


	/**
	 * Initialise the scene. Read the text file and display the 
	 * sentences in the Text Area. 
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

		defaultVoiceButton.setSelected(true);

		// Bind test audio and create audio buttons to the text area so that they are disabled when nothing is selected
		testAudioButton.disableProperty().bind(
				Bindings.isEmpty(sentenceDisplay.selectedTextProperty())
				);

		createAudioButton.disableProperty().bind(
				Bindings.isEmpty(sentenceDisplay.selectedTextProperty())
				);

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
	 * Create audio file from selected text
	 * 
	 */
	@FXML
	private void createAudio(ActionEvent e) throws IOException {

		String selectedText = sentenceDisplay.getSelectedText();

		String[] words = selectedText.split("\\s+");

		// The selected text cannot contain more than 40 words
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

		else {
			AssociationClass.getInstance().storeSelectedVoice("akl_nz_cw_cg_cg");
		}

		AppWindow.valueOf("AudioName").setScene(e);
		return;
	}

	@FXML
	private void testAudio(ActionEvent e) throws IOException {

		String selectedText = sentenceDisplay.getSelectedText();

		// Used to count the number of words in the selected text
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
		}

		else if (maleNZVoiceButton.isSelected()) {
			sayText(selectedText, "akl_nz_jdt_diphone");

		}

		else {
			sayText(selectedText, "akl_nz_cw_cg_cg");

		}

	}

	private void sayText(String text, String voice) throws IOException {

		testAudioButton.disableProperty().unbind();
		createAudioButton.disableProperty().unbind();

		backButton.setDisable(true);
		skipStepButton.setDisable(true);
		testAudioButton.setDisable(true);
		createAudioButton.setDisable(true);

		// Use festival on a different thread to allow the user to preview the audio output
		Task<Void> task = new Task<Void>() {

			@Override protected Void call() throws Exception {

				BashProcess testAudio = new BashProcess();

				// create audio file
				String command = "echo -e \"" + text + "\" | text2wave -eval \"(voice_" + voice + ")\" > "
						+ "./creation_files/temporary_files/audio_files/test_audio_output.wav";

				testAudio.runCommand(command);

				File testFile = new File("./creation_files/temporary_files/audio_files/test_audio_output.wav");

				// If the voice cannot pronounce a word in the selected text
				if(testFile.length() == 0) {
					testFile.delete();
					audioTestFailed = true;

					return null;
				}

				else {

					// play audio corresponding to the selected text and the chosen voice
					command = "play \"./creation_files/temporary_files/audio_files/test_audio_output.wav\" 2> /dev/null";

					testAudio.runCommand(command);

					audioTestFailed = false;

					return null;
				}
			}

			@Override protected void done() {

				Platform.runLater(() -> {

					if (audioTestFailed) {

						Alert audioTestFailed = new Alert(Alert.AlertType.ERROR);

						audioTestFailed.setTitle("Audio Test Failed");
						audioTestFailed.setHeaderText("The audio test has unfortunately "
								+ "failed due the text-to-speech synthesizer not being able to pronounce a word in the text you selected!");
						audioTestFailed.setContentText("Kindly select a different part/chunk of text to test the audio output.");
						audioTestFailed.showAndWait();

						enableButtons();

					}

					else {

						File testFile = new File("./creation_files/temporary_files/audio_files/test_audio_output.wav");
						testFile.delete();

						enableButtons();
					}
				});
			}
		};

		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();		

	}

	private void enableButtons() {

		backButton.setDisable(false);
		skipStepButton.setDisable(false);
		testAudioButton.setDisable(false);
		createAudioButton.setDisable(false);

		testAudioButton.disableProperty().bind(
				Bindings.isEmpty(sentenceDisplay.selectedTextProperty())
				);

		createAudioButton.disableProperty().bind(
				Bindings.isEmpty(sentenceDisplay.selectedTextProperty())
				);
	}

}
