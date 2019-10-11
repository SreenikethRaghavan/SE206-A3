package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import FXML.AppWindow;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.ListView;
import wikispeak.BashProcess;

/**
 * Controller for the Audio Files scene,
 * where the user is allowed to merge
 * multiple files together or use a single
 * file for their creation. 
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class AudioFilesController {

	@FXML
	private ListView<String> existingFiles;

	@FXML
	private ListView<String> filesToMerge;

	@FXML
	private Button playButton;

	@FXML
	private Button deleteButton;

	@FXML
	private Button addButton;

	@FXML
	private Button removeButton;

	@FXML
	private Button mergeButton;

	@FXML
	private ImageView mergeImage;


	private ObservableList<String> mergeList = FXCollections.observableArrayList();

	private ObservableList<String> existingList = FXCollections.observableArrayList();


	@FXML
	private void initialize() {

		ObservableList<String> audioFiles = FXCollections.observableArrayList();

		File[] files = new File("./creation_files/temporary_files/audio_files").listFiles();

		// remove audio file extensions (.wav)
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
			// if no audio files exist
			Alert noAudioFiles = new Alert(Alert.AlertType.INFORMATION);
			noAudioFiles.setTitle("No Existing Audio Files");
			noAudioFiles.setHeaderText("There are currently no existing audio files to display.");
			noAudioFiles.setContentText("Kindly go back and create an audio file to view it here. ");
			noAudioFiles.showAndWait();

		}


		// sort alphabetically
		Collections.sort(audioFiles);


		existingFiles.setItems(audioFiles);

		filesToMerge.setItems(mergeList);

		existingList = audioFiles;

		existingFiles.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		filesToMerge.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		playButton.disableProperty().bind(
				Bindings.isNull(existingFiles.getSelectionModel().selectedItemProperty())
				.and(Bindings.isNull(filesToMerge.getSelectionModel().selectedItemProperty())));

		deleteButton.disableProperty().bind(
				Bindings.isNull(existingFiles.getSelectionModel().selectedItemProperty())
				.and(Bindings.isNull(filesToMerge.getSelectionModel().selectedItemProperty())));

		mergeButton.disableProperty().bind(
				Bindings.size(mergeList).isEqualTo(0));

		addButton.disableProperty().bind(
				Bindings.isNull(existingFiles.getSelectionModel().selectedItemProperty()));

		removeButton.disableProperty().bind(
				Bindings.isNull(filesToMerge.getSelectionModel().selectedItemProperty()));
	}

	@FXML
	private void deleteFile() {

		String selected = existingFiles.getSelectionModel().getSelectedItem();

		if (selected == null) {

			selected = filesToMerge.getSelectionModel().getSelectedItem();
		}

		final String selectedFile = selected;

		Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);

		deleteAlert.setTitle("Deletion Confirmation");
		deleteAlert.setHeaderText("Are you sure that you want to delete the audio file '" + selectedFile + "'?");
		deleteAlert.setContentText("This action CANNOT be undone!");

		deleteAlert.showAndWait().ifPresent(selection -> {

			if(selection == ButtonType.OK) {
				File file = new File("./creation_files/temporary_files/audio_files/"+ selectedFile +".wav");
				file.delete();

				if (mergeList.contains(selectedFile)) {

					mergeList.remove(selectedFile);
					filesToMerge.setItems(mergeList);
				}

				else {

					existingList.remove(selectedFile);
					existingFiles.setItems(existingList);
				}

			}
		});


	}



	@FXML
	private void playFile() {

		playButton.disableProperty().unbind();
		deleteButton.disableProperty().unbind();

		playButton.setDisable(true);
		deleteButton.setDisable(true);


		String selected = existingFiles.getSelectionModel().getSelectedItem();

		if (selected == null) {
			selected = filesToMerge.getSelectionModel().getSelectedItem();
		}

		final String selectedFile = selected;

		// play the file on a different thread
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

					playButton.disableProperty().bind(
							Bindings.isNull(existingFiles.getSelectionModel().selectedItemProperty())
							.and(Bindings.isNull(filesToMerge.getSelectionModel().selectedItemProperty())));

					deleteButton.disableProperty().bind(
							Bindings.isNull(existingFiles.getSelectionModel().selectedItemProperty())
							.and(Bindings.isNull(filesToMerge.getSelectionModel().selectedItemProperty())));

				});

			}
		};

		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();
	}

	/**
	 * Add a file to the right side view 
	 * for every file the user wishes to
	 * add to the merge list. 
	 */
	@FXML
	private void addToMergeList() {

		String selection = existingFiles.getSelectionModel().getSelectedItem();

		if (selection != null) {

			mergeList.add(selection);
			existingList.remove(selection);

			existingFiles.setItems(existingList);
			filesToMerge.setItems(mergeList);

			if (mergeList.size() > 1) {

				mergeButton.setText("Merge Audio Files");

				Image image = new Image("/images/merge.jpg");

				mergeImage.setImage(image);
			}
		}
	}

	@FXML
	private void removeFromMergeList() {

		String selection = filesToMerge.getSelectionModel().getSelectedItem();

		if (selection != null) {

			mergeList.remove(selection);
			existingList.add(selection);

			Collections.sort(existingList);

			existingFiles.setItems(existingList);
			filesToMerge.setItems(mergeList);

			if (mergeList.size() <= 1) {

				mergeButton.setText("Use Audio File");

				Image image = new Image("/images/audioNote.png");

				mergeImage.setImage(image);
			}

		}

	}

	@FXML
	private void mergeFiles(ActionEvent e) throws Exception {

		if (mergeButton.getText().equals("Use Audio File")) {

			String selected = mergeList.get(0);

			AssociationClass.getInstance().storeAudioFile(selected);

			AppWindow.valueOf("SelectImages").setScene(e);

			return;
		}

		else {

			List<String> filesToMerge = new ArrayList<String>();

			for (String file : mergeList) {

				filesToMerge.add(file);
			}

			AssociationClass.getInstance().storeFilesToMerge(filesToMerge);

			AppWindow.valueOf("MergeName").setScene(e);

			return;

		}
	}


	@FXML
	private void returnToSelectSentences(ActionEvent e) throws IOException {

		AppWindow.valueOf("SelectSentences").setScene(e);
		return;
	}

	@FXML
	private void deselectExistingList() {
		existingFiles.getSelectionModel().clearSelection();
	}

	@FXML
	private void deselectMergeList() {
		filesToMerge.getSelectionModel().clearSelection();
	}
}
