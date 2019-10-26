package main.controllers;

import java.io.IOException;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import main.FXML.AppWindow;
import main.tasks.WikiTask;
import main.WikiSpeak;

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

		Image image = new Image("main/images/loading.gif");
		loadingGif.setImage(image);
		
		searchButton.disableProperty().unbind();

		backButton.setDisable(true);
		searchButton.setDisable(true);

		String searchTerm = searchBar.getText();

		AssociationClass.getInstance().storeSearchTerm(searchTerm);

		WikiTask wikitask = new WikiTask(searchTerm, e);
		WikiSpeak.bg.submit(wikitask);

	}

	@FXML
	private void returnToMainMenu (ActionEvent e) throws IOException {

		AppWindow.valueOf("MainMenu").setScene(e);

		return;
	}

}
