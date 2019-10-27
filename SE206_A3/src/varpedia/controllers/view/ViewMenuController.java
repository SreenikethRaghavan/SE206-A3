package varpedia.controllers.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.media.MediaPlayer.Status;
import varpedia.scene.AppWindow;


/**
 * Controller for the view menu scene,
 * where the user is allowed to play and delete creations. 
 * 
 * @author Sreeniketh Raghavan
 * @author Hazel Williams
 * 
 */
public class ViewMenuController {

	@FXML
	private ListView<String> listView;
	
	@FXML
	private Button playButton;

	@FXML
	private Button pausePlayButton;

	@FXML
	private Button stopButton;

	@FXML
	private Button deleteButton;

	@FXML
	private MediaView mediaView;

	private MediaPlayer player;
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// LIST RELATED
	
	/**
	 * Helper function that updates the listview to reflect what is currently stored in the creations folder
	 */
	@FXML
	private void updateList() {
		List<String> creations = new ArrayList<String>();

		File[] files = new File("./creation_files/creations").listFiles();

		// remove creation extensions (.mp4) so they can be displayed neatly
		if (files.length != 0) {
			for (File file : files) {
				if (file.isFile()) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf("."));
					creations.add(name);
				}
			}
		}
		else {
			// if no creations exist
			creations.add("No creations currently exist.");
			creations.add("Please create a creation to view it.");

			playButton.setDisable(true); 
			deleteButton.setDisable(true);
		}

		// sort alphabetically
		Collections.sort(creations);
		ObservableList<String> sorted = FXCollections.observableArrayList();

		int index = 1;

		String creationName = "";

		// number the creations, if there are creations
		for(String creation : creations) {
			if (files.length != 0) {
				//if there are files we want to number them
				creationName = index + ". " + creation;
			} else {
				//else we just want to add them as a message.
				creationName = creation;
			}
			sorted.add(creationName);
			index++;
		}

		listView.setItems(sorted);
		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}


	@FXML
	private void initialize() {
		updateList();
		// while no video is playing the user shouldnt be able to play or pause
		stopButton.setDisable(true);
		pausePlayButton.setDisable(true);
	}
	
	/**
	 * This function will delete a creation, if a creation is selected and the user confirms they wish to delete it.
	 */
	@FXML
	private void deleteCreation() {
		String selected = listView.getSelectionModel().getSelectedItem();
		// if something is selected
		if (selected != null) {

			String selectedCreation = selected.substring(selected.indexOf(".") + 2);
			
			// confirm the user wants to delete
			Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);
			deleteAlert.setTitle("Deletion Confirmation");
			deleteAlert.setHeaderText("Are you sure that you want to delete the creation '" + selectedCreation + "'?");
			deleteAlert.setContentText("This action CANNOT be undone!");
			deleteAlert.showAndWait().ifPresent(selection -> {
				if(selection == ButtonType.OK) {
					File file = new File("./creation_files/creations/"+ selectedCreation +".mp4");
					file.delete();
				}
			});
		}
		// update view
		updateList();
	}


	// ------------------------------------------------------------------------------------------------------------------------------------
	// VIDEO RELATED

	/**
	 * Functionality for the play/pause button
	 */
	@FXML
	private void togglePausePlay() {
		if (player.getStatus() == Status.PLAYING) {
			player.pause();
		} else {
			player.play();
		}
	}

	/**
	 * resets the video to default view, ie stops the video.
	 * useful function to prevent the continuation of audio chunks after a video has 
	 * been stopped.
	 */
	@FXML
	private void stopVideo() {
		player.pause();
		player = null;
		mediaView.setMediaPlayer(null);
		stopButton.setDisable(true);
		pausePlayButton.setDisable(true);
	}


	/**
	 * This function allows overriding, if you are already playing a creation you can click play on another creation and that will play instead.
	 */
	@FXML
	private void playCreation() {

		String selected = listView.getSelectionModel().getSelectedItem();

		if(selected == null) {
			playButton.setDisable(false);
		}

		else {
			stopButton.setDisable(false);
			pausePlayButton.setDisable(false);

			String selectedCreation = selected.substring(selected.indexOf(".") + 2);
			
			//helps to prevent overlap of videos if the user tries to overwrite the video that is currently playing.
			if(player!=null) {
				player.pause();
				player = null;
			}
			
			//gets the selected file and plays it on the media view
			File fileUrl = new File("creation_files/creations/" + selectedCreation + ".mp4");
			Media video = new Media(fileUrl.toURI().toString());
			player = new MediaPlayer(video);
			player.setAutoPlay(true);
			mediaView.setMediaPlayer(player);
			player.setOnEndOfMedia(new Runnable() {
				@Override
				public void run() {
					//clear the media player once the old one is done
					mediaView.setMediaPlayer(null);
					stopButton.setDisable(true);
					pausePlayButton.setDisable(true);
				}
			});
		}
	}
	
	// ------------------------------------------------------------------------------------------------------------------------------------
	// NAVIGATION FUNCTIONS

	@FXML
	private void returnToMainMenu(ActionEvent e) throws IOException {
		if(player != null) {
			player.pause();
			player = null;
		}
		AppWindow.valueOf("MainMenu").setScene(e);
		return;
	}
}
