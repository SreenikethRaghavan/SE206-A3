package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import FXML.AppWindow;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.ListView;
import wikispeak.BashProcess;

public class AudioFilesController {

	@FXML
	private ListView<String> existingFiles;

	@FXML
	private ListView<String> filesToMerge;

	@FXML
	private Button playButton;

	@FXML
	private Button deleteButton;

	private ObservableList<String> mergeList = FXCollections.observableArrayList();


	@FXML
	private void initialize() {

		List<String> audioFiles = new ArrayList<String>();

		File[] files = new File("./creation_files/temporary_files/audio_files").listFiles();

		// remove creation extensions (.wav)
		if (files.length != 0) {
			for (File file : files) {
				if (file.isFile()) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf("."));
					audioFiles.add(name);
				}
			}
		}

		else {
			// if no creations exist
			Alert noAudioFiles = new Alert(Alert.AlertType.INFORMATION);
			noAudioFiles.setTitle("No Existing Audio Files");
			noAudioFiles.setHeaderText("There are currently no existing audio files to display.");
			noAudioFiles.setContentText("Kindly go back and create an audio file to view it here. ");
			noAudioFiles.showAndWait();

		}


		// sort alphabetically
		Collections.sort(audioFiles);


		ObservableList<String> sorted = FXCollections.observableArrayList();

		int index = 1;

		// number the creations
		for(String file : audioFiles) {
			String audioName = index + ". " + file;
			sorted.add(audioName);
			index++;

		}

		existingFiles.setItems(sorted);

		existingFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		filesToMerge.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


	}

	@FXML
	private void deleteFile() {

		String selected = existingFiles.getSelectionModel().getSelectedItem();

		// if something is selected
		if (selected != null && selected != "") {

			String selectedFile = selected.substring(3);

			Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);

			deleteAlert.setTitle("Deletion Confirmation");
			deleteAlert.setHeaderText("Are you sure that you want to delete the audio file '" + selectedFile + "'?");
			deleteAlert.setContentText("This action CANNOT be undone!");

			deleteAlert.showAndWait().ifPresent(selection -> {

				if(selection == ButtonType.OK) {
					File file = new File("./creation_files/temporary_files/audio_files/"+ selectedFile +".wav");
					file.delete();
				}
			});

		}
		// update view
		initialize();
	}



	@FXML
	private void playFile() {

		playButton.setDisable(true);
		deleteButton.setDisable(true);


		String selected = existingFiles.getSelectionModel().getSelectedItem();

		if (selected != null && selected != "") {


			String selectedFile = selected.substring(3);

			Task<Void> task = new Task<Void>() {
				@Override protected Void call() throws Exception {

					BashProcess playAudio = new BashProcess();

					String command = "play \"./creation_files/temporary_files/audio_files/" + selectedFile + ".wav\" 2> /dev/null";

					playAudio.runCommand(command);

					return null;
				}

				@Override protected void done() {

					Platform.runLater(() -> {
						playButton.setDisable(false);
						deleteButton.setDisable(false);

					});

				}
			};

			Thread thread = new Thread(task);

			thread.setDaemon(true);

			thread.start();

		}

	}

	@FXML
	private void addToMergeList() {

		String selection = existingFiles.getSelectionModel().getSelectedItem();

		if (selection != null && selection != "") {

			if (!mergeList.contains(selection.substring(3))) {
				mergeList.add(selection.substring(3));

			}
		}

		filesToMerge.setItems(mergeList);

	}

	@FXML
	private void removeFromMergeList() {

		String selection = filesToMerge.getSelectionModel().getSelectedItem();

		if (selection != null && selection != "") {

			mergeList.remove(selection);

		}

		filesToMerge.setItems(mergeList);
	}

	@FXML
	private void mergeFiles(ActionEvent e) throws Exception {

		List<String> filesToMerge = new ArrayList<String>();

		for (String file : mergeList) {

			filesToMerge.add(file);
		}

		AssociationClass.getInstance().storeFilesToMerge(filesToMerge);

		AppWindow.valueOf("MergeName").setScene(e);

		return;
	}


	@FXML
	private void useExistingFile(ActionEvent e) throws Exception {

		String selected = existingFiles.getSelectionModel().getSelectedItem();

		AssociationClass.getInstance().storeAudioFile(selected.substring(3));

		AppWindow.valueOf("SelectImages").setScene(e);

		return;
	}




	@FXML
	private void returnToSelectSentences(ActionEvent e) throws IOException {

		AppWindow.valueOf("SelectSentences").setScene(e);
		return;
	}
}
