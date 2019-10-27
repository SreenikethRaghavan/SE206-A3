package varpedia.tasks;

import java.io.File;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.image.ImageView;
import varpedia.scene.AppWindow;
import varpedia.controllers.AssociationClass;
import varpedia.processes.BashProcess;

public class GenerateWaveFileTask extends Task<Void> {
	private String fileName;
	private boolean audioCreationFailed = false;
	private ImageView loadingGif;
	private String userInput;
	private ActionEvent e;


	public GenerateWaveFileTask(String filename, ImageView loadingGif, String userInput, ActionEvent e) {

		this.fileName = filename;
		this.loadingGif = loadingGif;
		this.userInput = userInput;
		this.e = e;
	}


	@Override
	protected Void call() throws Exception {
		BashProcess createAudio = new BashProcess();

		String selectedText = AssociationClass.getInstance().getSelectedText();
		String selectedVoice = AssociationClass.getInstance().getSelectedVoice();

		// generate audio file using text2wave
		String command = "echo -e \"" + selectedText + "\" | text2wave -eval \"(voice_" + selectedVoice + ")\" > " + fileName;

		createAudio.runCommand(command);

		File testFile = new File(fileName);

		// If the voice cannot pronounce a word in the selected text
		if(testFile.length() == 0) {
			testFile.delete();
			audioCreationFailed = true;
		}

		return null;
	}

	@Override protected void done() {
		Platform.runLater(() -> {

			if (audioCreationFailed) {

				loadingGif.setImage(null);

				Alert creationFailed = new Alert(Alert.AlertType.ERROR);

				creationFailed.setTitle("Audio File Creation Failed");
				creationFailed.setHeaderText("Creation of the Audio File '" + userInput + "' has failed.\n\n"
						+ "The text-to-speech synthesizer cannot pronounce a word in the text you selected!");
				creationFailed.setContentText("Kindly test the audio output before attempting to create "
						+ "an audio file.");
				creationFailed.showAndWait();

				try {
					AppWindow.valueOf("SelectSentences").setScene(e);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				return;			
			}

			// if audio creation is successful
			loadingGif.setImage(null);

			Alert created = new Alert(Alert.AlertType.INFORMATION);

			created.setTitle("Audio File Created");
			created.setHeaderText("Audio File with the name '" + userInput + "' has been successfully created!");
			created.setContentText("You can now listen to the audio file you have created and can merge multiple audio files together.");
			created.showAndWait();

			try {
				AppWindow.valueOf("SelectSentences").setScene(e);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

}
