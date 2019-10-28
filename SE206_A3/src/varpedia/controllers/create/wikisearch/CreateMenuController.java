package varpedia.controllers.create.wikisearch;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import varpedia.scene.AppWindow;
import varpedia.controllers.AssociationClass;
import varpedia.processes.BashProcess;
import varpedia.tasks.WikiTask;
import varpedia.Varpedia;

/**
 * Controller for the creation menu scene,
 * where the user is allowed to enter the
 * term they wish to search. 
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class CreateMenuController {

	@FXML
	private TextField searchBar;

	@FXML
	private Button backButton;
	@FXML
	private Button searchButton;

	@FXML
	private ImageView loadingGif;


	@FXML
	private void initialize() {

		// delete the temporary files folder every time a new term is searched for
		String deleteTempFiles = "rm -rf ./creation_files/temporary_files";
		// create a new temporary files folder for each new search term
		String makeDirectories = "mkdir -p ./creation_files/temporary_files/audio_files; mkdir -p ./creation_files/temporary_files/video_files; "
				+ "mkdir -p ./creation_files/temporary_files/image_files; mkdir -p ./creation_files/temporary_files/text_files;";

		BashProcess directories = new BashProcess();
		directories.runCommand(deleteTempFiles);
		directories.runCommand(makeDirectories);

		// bind the search button to the text field so that it is disabled when 
		// the text field is empty
		searchButton.disableProperty().bind(
				Bindings.isEmpty(searchBar.textProperty()));

	}


	/**
	 * Method called when the user enters the
	 * term they wish to search for. 
	 * 
	 */
	@FXML
	private void searchTerm(ActionEvent e) throws IOException {

		Image image = new Image("varpedia/images/loading.gif");
		loadingGif.setImage(image);

		searchButton.disableProperty().unbind();

		backButton.setDisable(true);
		searchButton.setDisable(true);

		String searchTerm = searchBar.getText();

		AssociationClass.getInstance().storeSearchTerm(searchTerm);

		// search for the term using wikit
		WikiTask wikitask = new WikiTask(searchTerm, e);
		Varpedia.bg.submit(wikitask);

	}

	@FXML
	private void returnToMainMenu (ActionEvent e) throws IOException {

		AppWindow.valueOf("MainMenu").setScene(e);

		return;
	}

}
