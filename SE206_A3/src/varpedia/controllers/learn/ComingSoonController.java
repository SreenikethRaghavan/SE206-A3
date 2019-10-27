package varpedia.controllers.learn;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import varpedia.scene.AppWindow;

/**
 * This scene is just a placeholder while we don't have minecraft fortnite or any other games.
 * @author Hazel Williams
 */
public class ComingSoonController {

    @FXML
    private Button backButton;
    
    @FXML
    void returnToMainMenu(ActionEvent event) throws IOException {
		AppWindow.valueOf("LearnMenu").setScene(event);
	
    }

}