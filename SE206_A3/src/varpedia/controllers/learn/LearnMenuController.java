package varpedia.controllers.learn;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import varpedia.scene.AppWindow;
import varpedia.controllers.AssociationClass;
import varpedia.gamelevels.Level;

/**
 * This is the controller for the learn menu
 * It displays the current level of the user, a well as shows there
 * current level of access to the games.
 * For this prototype, the user will start with a set number of XP points
 * and level, in order to have enough features unlocked to demonstrate.
 * @author Hazel Williams
 *
 */
public class LearnMenuController {

	// level bar elements
	@FXML
	private ProgressBar xpProgressBar;
	@FXML
	private Text xpText;
	@FXML
	private Text lvlNumTxt;


	// buttons to be put into an array
	@FXML
	private Button btn1;
	@FXML
	private Button btn2;
	@FXML
	private Button btn3;
	@FXML
	private Button btn4;
	private Button[] gameBtns;


	// lock images to be put into an array
	@FXML
	private ImageView lock1;
	@FXML
	private ImageView lock2;
	@FXML
	private ImageView lock3;
	@FXML
	private ImageView lock4;
	private ImageView[] locks;


	// settings pane elements
	@FXML
	private ImageView settingImageView;
	@FXML
	private AnchorPane settingsPane;


	// warning pane elements
	@FXML
	private AnchorPane warningPane;
	@FXML
	private Text availabilityText;
	@FXML
	private Text warningText;

	// other scene elements
	@FXML
	private Button backButton;

	// Scene variables to store the current level and XP of the user.
	private Level currLevel;
	private int currXP; 


	/*
	 * This function sets up the scene, including checking for the current state of the application.
	 * In order for the menu to be properly set up, it needs to know what level the user is currently
	 * at, what games they have currently unlocked, and whether there are resources available to play
	 * those games. By using helper functions, initialize() sets up the scene.
	 */
	@FXML
	private void initialize() {
		locks = new ImageView[] {lock1,lock2,lock3,lock4};
		gameBtns = new Button[] {btn1,btn2,btn3,btn4};
		settingsPane.setVisible(false);
		warningPane.setVisible(false);

		updateLevel();
		String messageText = "";
		String notAvailable ="";

		// now check if there are files to play
		if(!checkForMemoryImages()) {
			//there are no memory images yet.
			gameBtns[1].setDisable(true);
			messageText = "no memory images";
			notAvailable = "memory game is not available.\n\nTo enable it creation a new creation.";
		}

		if(!checkForQuizVideos()) {
			gameBtns[0].setDisable(true);
			if(messageText.length() != 0) {
				messageText = "no memory images or quiz videos";
				notAvailable = "memory game and the quiz game are not available.\n\nTo enable them creation a new creation.";
			} else {
				messageText = "no quiz videos";
				notAvailable = "quiz game is not available.\n\nTo enable it creation a new creation.";
			}
		}

		if(messageText.length() != 0) {
			warningPane.setVisible(true);
			warningText.setText("There are currently "+messageText+".");
			availabilityText.setText("As such, the "+notAvailable);
		}

	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// NAVIGATION FUNCTIONS

	@FXML
	void exitWarning(ActionEvent event) {
		warningPane.setVisible(false);
	}

	@FXML
	void goToCreateCreation(ActionEvent event) throws IOException {
		AppWindow.valueOf("CreateMenu").setScene(event);
	}

	@FXML
	private void goToQuiz(ActionEvent event) throws IOException {
		AppWindow.valueOf("Quiz").setScene(event);
	}

	@FXML
	private void goToMissingWord(ActionEvent event) throws IOException {
		AppWindow.valueOf("Memory").setScene(event);
	}

	@FXML
	private void goToComingSoon(ActionEvent event) throws IOException {
		AppWindow.valueOf("ComingSoon").setScene(event);
	}

	@FXML
	void exitSettings(ActionEvent event) {
		settingsPane.setVisible(false);
	}  

	@FXML
	private void returnToMainMenu(ActionEvent event) throws IOException {
		AppWindow.valueOf("MainMenu").setScene(event);
	}

	/*
	 * Toggles whether the settings pane is visible or not.
	 */
	@FXML
	void showSettings(ActionEvent event) {
		if(settingsPane.isVisible()) {
			settingsPane.setVisible(false);
		} else {
			settingsPane.setVisible(true);
		}
	}

	// ------------------------------------------------------------------------------------------------------------------------------------
	// HELPER FUNCTIONS

	@FXML
	void resetXP(ActionEvent event) {
		AssociationClass.getInstance().increaseXP(-AssociationClass.getInstance().getXP());
		AssociationClass.getInstance().saveProgess();

		updateLevel();
		settingsPane.setVisible(false);

	}

	/*
	 * If this is called then the game hasn't been unlocked yet.
	 */
	private void lockGame(int index) {
		locks[index].setVisible(true);
		gameBtns[index].setDisable(true);
	}

	/*
	 * If this is called then the game has been unlocked.
	 */
	private void unlockGame(int index) {
		locks[index].setVisible(false);
		gameBtns[index].setDisable(false);
	}

	/*
	 * Gets all the relevant information about the current level and updates the GUI
	 * to reflect this information.
	 */
	private void updateLevel() {
		//To find out what level we are on we will first have to get the current XP
		currXP = AssociationClass.getInstance().getXP();
		AssociationClass.getInstance().saveProgess();

		for (Level level : Level.values()) {
			//iterate through each level to find the level with the corresponding xp boundaries
			if(level.isThisLevel(currXP)) {
				currLevel = level;
			}
		}

		//if we dont find a level, then thats an error.
		if(currLevel == null) {
			System.out.println("Error: No level was found. Assigning default Lvl 2.");
			currLevel = Level.L2;
		}

		//now that we have that level, we need to update the text.
		lvlNumTxt.setText(currLevel.getLevelNumber());
		xpText.setText(currLevel.getXPText(currXP));

		//determine which games are unlocked, and which need to be locked.
		for(int i =0; i < locks.length; i++) {
			if(currLevel.isUnlocked(i)) {
				unlockGame(i);
			} else {
				lockGame(i);
			}
		}

		//update the progress bar with the current XP
		xpProgressBar.setProgress(currLevel.getProgress(currXP));
	}

	/*
	 * This helper function checks whether there are resources available for the quiz game
	 */
	private boolean checkForQuizVideos() {
		File[] files = new File("./creation_files/quiz_files/quiz_images").listFiles();
		if(files.length ==0) {
			return false;
		}
		return true;
	}

	/*
	 * This helper function checks whether there are resources available for the memory game
	 */
	private boolean checkForMemoryImages() {
		File[] files = new File("./creation_files/memory_files").listFiles();
		if(files.length ==0) {
			return false;
		}
		return true;
	}

}