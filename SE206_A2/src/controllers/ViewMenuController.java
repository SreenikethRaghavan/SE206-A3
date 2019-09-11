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
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import wikispeak.BashProcess;


/**
 * Controller for the view menu scene,
 * where the user is allowed to play and delete creations. 
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class ViewMenuController {


	@FXML
	private ListView<String> listView;

	@FXML
	private Button playButton;

	@FXML
	private Button deleteButton;

	@FXML
	private void initialize() {

		List<String> creations = new ArrayList<String>();

		playButton.setDisable(false);
		deleteButton.setDisable(false);

		File[] files = new File("./creation_files/creations").listFiles();

		// remove creation extensions (.mp4)
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
			Alert noCreationsAlert = new Alert(Alert.AlertType.INFORMATION);
			noCreationsAlert.setTitle("No Existing Creations");
			noCreationsAlert.setHeaderText("There are currently no existing creations to display.");
			noCreationsAlert.setContentText("Kindly create a new creation to view it on the list. ");
			noCreationsAlert.showAndWait();

		}


		// sort alphabetically
		Collections.sort(creations);


		ObservableList<String> sorted = FXCollections.observableArrayList();

		int index = 1;

		// number the creations
		for(String creation : creations) {
			String creationName = index + ". " + creation;
			sorted.add(creationName);
			index++;

		}

		listView.setItems(sorted);

		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);


	}

	@FXML
	private void deleteCreation() {

		String selected = listView.getSelectionModel().getSelectedItem();

		// if something is selected
		if (selected != null) {

			String selectedCreation = selected.substring(3);

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
		initialize();
	}



	@FXML
	private void playCreation() {

		playButton.setDisable(true);
		deleteButton.setDisable(true);


		String selected = listView.getSelectionModel().getSelectedItem();

		if (selected != null) {


			String selectedCreation = selected.substring(3);

			Task<Void> task = new Task<Void>() {
				@Override protected Void call() throws Exception {

					BashProcess playCreation = new BashProcess();

					// play using ffplay on a different thread
					String command = "ffplay -autoexit \"./creation_files/creations/" + selectedCreation + ".mp4\" &> /dev/null";

					playCreation.runCommand(command);

					return null;
				}
				@Override protected void done() {
					Platform.runLater(() -> {
						initialize();

					});

				}
			};

			Thread thread = new Thread(task);

			thread.setDaemon(true);

			thread.start();

		}

	}


	@FXML
	private void returnToMainMenu(ActionEvent e) throws IOException {

		AppWindow.valueOf("MainMenu").setScene(e);
		return;
	}
}
