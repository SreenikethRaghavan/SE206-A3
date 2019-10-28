package varpedia.controllers.create;

import java.io.File;
import java.io.IOException;


import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import varpedia.scene.AppWindow;
import varpedia.controllers.AssociationClass;
import varpedia.processes.BashProcess;
import varpedia.tasks.CreateCreationTask;

import varpedia.Varpedia;


/**
 * Controller for the creation name scene,
 * where the user is allowed to enter the
 * name for their creation. 
 * 
 * Multi-threading has been implemented using the Task<T>
 * class. 
 * 
 * @author Sreeniketh Raghavan
 * @author Hazel Williams
 * 
 */
public class CreationNameController {

	@FXML
	private Button enterButton;

	@FXML
	private TextField searchBar;

	@FXML
	private ImageView loadingGif;

	private String userInput;


	@FXML
	private void initialize() {

		// generate default name for the creation
		String searchTerm = AssociationClass.getInstance().getSearchTerm();
		String defaultName = searchTerm + "_creation";
		defaultName = defaultName.replace(' ', '_');

		String fileName = "./creation_files/creations/"+ defaultName +".mp4";
		File file = new File(fileName);

		int fileCount = 1;

		// while file with the default name exists, generate a new name with a number at the end
		while(file.exists() && file.isFile()) {

			defaultName = searchTerm + "_creation_" + fileCount;
			defaultName = defaultName.replace(' ', '_');

			fileName = "./creation_files/creations/"+ defaultName +".mp4";
			file = new File(fileName);

			fileCount++;
		}

		searchBar.setText(defaultName);
		enterButton.setDisable(false);
		enterButton.disableProperty().bind(
				Bindings.isEmpty(searchBar.textProperty()));
	}

	//task to actually create the creation is in this, to avoid code duplication between overwrite/don't overwrite case.
	private void makeCreation(ActionEvent e) {

		Image image = new Image("/varpedia/images/loading.gif");
		loadingGif.setImage(image);


		CreateCreationTask createTask = new CreateCreationTask(userInput);
		Varpedia.bg.submit(createTask);

		createTask.setOnSucceeded(ev -> {
			// Alert the user about the creation being successfully created
			Alert created = new Alert(Alert.AlertType.INFORMATION);

			//TODO: make sure Bob doesnt interact with the GUI
			loadingGif.setImage(null);

			created.setTitle("Creation Created");
			created.setHeaderText("Creation with the name '" + userInput + "' has been successfully created!");
			created.setContentText("Select the 'View Existing Creations' option from the main menu to manage and play your creations.");
			created.showAndWait();

			// delete the temporary files folder
			String command = "rm -rf ./creation_files/temporary_files";
			BashProcess deleteTempFiles= new BashProcess();
			deleteTempFiles.runCommand(command);

			//currently the user relies on this message to navigate away from the creation screen, in future we will revamp this so that the user can go and perform other tasks while the creation is being generated.
			try {
				AppWindow.valueOf("MainMenu").setScene(e);
				return;
			} catch (IOException e1) {
			}
		});
	}


	/**
	 * createCreation is triggered by clicking the enter button.
	 * It first checks whether the provided potential file name is valid. If it isn't the user will be prompted to try again.
	 * Then it checks whether a file by that name already exists, asking the user if they wish to overwrite if so.
	 * Finally, if it has made it past all of those hurdles, it will create the creation. It uses audio and images generated
	 * and labeled from previous steps, so all that really happens in this task is the final putting it all together.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void createCreation(ActionEvent e) throws IOException {

		Image image = new Image("/varpedia/images/loading.gif");
		loadingGif.setImage(image);

		//want to disable the enter button so they can't spam a lot of file creations, avoids them causing overwrite issues.
		enterButton.disableProperty().unbind();
		enterButton.setDisable(true);
		userInput = searchBar.getText();

		char[] chars = userInput.toCharArray();
		// Check for invalid creation names
		if(checkForInvalidName(chars)) {
			AppWindow.valueOf("CreationName").setScene(e);
			return;
		}

		String fileName = "./creation_files/creations/"+ userInput +".mp4";
		File file = new File(fileName);

		if(file.exists() && file.isFile()) {

			loadingGif.setImage(null);

			// give the user the option to override the existing creation
			Alert fileExists = new Alert(Alert.AlertType.CONFIRMATION);
			fileExists.setTitle("Creation Already Exists");
			fileExists.setHeaderText("A creation with the name '" + userInput + "' already exists!");
			fileExists.setContentText("Would you like to override the existing creation?");
			fileExists.showAndWait().ifPresent(selection -> {

				if(selection != ButtonType.OK) {
					//if they don't want to override, give them a chance to rename it, reset the place.
					initialize();
				} else {
					//they do want to override it
					makeCreation(e);
				}
				//if they want to override it, continue on.
			});
		} else {
			//no overwrite issues? make the thing.
			makeCreation(e);
		}

	}

	/**
	 * The method checks if the user input (file name)
	 * only contains valid characters (alphanumeric, 
	 * hyphens, and underscores)
	 */
	private boolean checkForInvalidName(char[] chars) {

		for(char Char : chars) {
			if (!Character.isDigit(Char) && !Character.isLetter(Char) && Char != '-' && Char != '_') {

				loadingGif.setImage(null);

				Alert invalidName = new Alert(Alert.AlertType.ERROR);
				invalidName.setTitle("Invalid Creation Name");
				invalidName.setHeaderText("You cannot save a creation with the character '" + Char + "' in its name!");
				invalidName.setContentText("Kindly enter a different name.");
				invalidName.showAndWait();
				return true;

			}
		}
		return false;
	}
}

