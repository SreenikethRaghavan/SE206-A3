package controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import FXML.AppWindow;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import wikispeak.BashProcess;

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

		Image image = new Image("/images/loading.gif");
		loadingGif.setImage(image);
		
		searchButton.disableProperty().unbind();

		backButton.setDisable(true);
		searchButton.setDisable(true);

		String searchTerm = searchBar.getText();

		AssociationClass.getInstance().storeSearchTerm(searchTerm);


		// Process finding the word using wikit and storing the result in a file on a different thread
		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {

				BashProcess wikit = new BashProcess();

				// Use the wikit bash command to search for the wikipedia entry corresponding the search term. 
				// Format the wikit output and store it in a text file
				String command = "touch ./creation_files/temporary_files/text_files/wikipedia_output.txt; "        
						+ "wikit " + searchTerm +" | sed 's/\\([.?!]\\) \\([[:upper:]]\\)/\\1\\n\\2/g' | sed 's/  //g' | tee ./creation_files/temporary_files/text_files/wikipedia_output.txt";


				wikit.runCommand(command);

				return null;
			}

			@Override protected void done() {

				Platform.runLater(() -> {
					BufferedReader reader = null;

					try {
						reader = new BufferedReader(new FileReader("./creation_files/temporary_files/text_files/wikipedia_output.txt"));
					} catch (FileNotFoundException e1) {

					}
					String text;
					try {
						text = reader.readLine();
						reader.close();

						// if the search term returns no result, give the user an option to enter a new term or return to the main menu 
						if (text.equals(searchTerm + " not found :^(")) {

							AppWindow.valueOf("SearchError").setScene(e);
							return;
						} 

						// Let the user choose the number of sentences they wish to include in their creation
						AppWindow.valueOf("SelectSentences").setScene(e);
						return;

					}
					catch (IOException i) {

					}
				});

			}
		};


		Thread thread = new Thread(task);

		thread.setDaemon(true);

		thread.start();

	}

	@FXML
	private void returnToMainMenu (ActionEvent e) throws IOException {

		AppWindow.valueOf("MainMenu").setScene(e);

		return;
	}

}
