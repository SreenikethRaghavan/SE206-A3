package varpedia.controllers.create.wikisearch;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import varpedia.scene.AppWindow;
import varpedia.controllers.AssociationClass;


public class CreateCreationMenuController {

    @FXML
    private Button previousBtn;

    @FXML
    private Button backButton;
    
	@FXML
	private void initialize() {
		//set up the previously searched term
		previousBtn.setText("USE " + AssociationClass.getInstance().getSearchTerm().toUpperCase());
	}
	
    @FXML
    void goToSelectSentences(ActionEvent event) throws IOException {
    	AppWindow.valueOf("SelectSentences").setScene(event);
    }

    @FXML
    void goToWIki(ActionEvent event) throws IOException {
    	AppWindow.valueOf("CreateMenu").setScene(event);
		return;
    }

    @FXML
	private void returnToMainMenu (ActionEvent e) throws IOException {

		AppWindow.valueOf("MainMenu").setScene(e);

		return;
	}

}