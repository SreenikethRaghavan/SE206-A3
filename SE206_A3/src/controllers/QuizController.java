package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import FXML.AppWindow;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

/**
 * Controller for the Quiz scene where the user 
 * is is allowed to test their learning. The user
 * is expected to enter the term which describes 
 * the image being displayed (which will be taken 
 * from one of the creations they have created and
 * the expected answer is the term they searched for.  
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class QuizController {

	@FXML
	private ImageView imageView;

	@FXML
	private Button enterButton;

	@FXML
	private TextField answerField;

	@FXML
	private Text result;

	private List<Image> imageList = new ArrayList<Image>();

	private int wrongAnswerCount = 0;

	@FXML
	private void initialize() {

		File[] files = new File("./creation_files/quiz_files/quiz_images").listFiles();

		List<File> fileList = Arrays.asList(files);

		// to randomise the image being shown (don't want the same image to be the first question every time)
		Collections.shuffle(fileList);

		for (File file : files) {
			if (file.isFile()) {

				Image image = new Image("file:" + file.getPath().substring(2));
				imageList.add(image);				
			}
		}

		// select an image at random and quiz the user on it
		if(!imageList.isEmpty()) {
			Image randomImage = imageList.get((int)Math.random()*imageList.size());
			imageView.setImage(randomImage);		

			// in order to avoid repeat questions
			imageList.remove(randomImage);

		}

		else {

			// if the user has created no creations
			Alert noCreations = new Alert(Alert.AlertType.INFORMATION);
			noCreations.setTitle("No Existing Creations");
			noCreations.setHeaderText("There are currently no creations to quiz you on.");
			noCreations.setContentText("Kindly quit and create a creation to unlock the quiz component. ");
			noCreations.showAndWait();	
		}

		enterButton.disableProperty().bind(
				Bindings.isEmpty(answerField.textProperty()));

	}

	@FXML
	private void checkAnswer(ActionEvent e) throws IOException {

		String answer = answerField.getText();

		Path path = Paths.get(imageView.getImage().getUrl());

		String imageName = path.getFileName().toString();
		String correctAnswer = imageName.substring(0, imageName.lastIndexOf("."));

		if(answer.equalsIgnoreCase(correctAnswer)) {

			result.setText("Correct answer. Well done, that was impressive! :D");
			result.setFill(Color.GREEN);

			wrongAnswerCount = 0;

			// avoid duplication of questions
			imageList.remove(imageView.getImage());	
			answerField.clear();

			if(!imageList.isEmpty()) {

				Image randomImage = imageList.get((int)Math.random()*imageList.size());
				imageView.setImage(randomImage);

			}

			else {

				Alert completedQuiz = new Alert(Alert.AlertType.INFORMATION);
				completedQuiz.setTitle("Quiz Completed");
				completedQuiz.setHeaderText("Congratulations! You have successfully completed the quiz :D");
				completedQuiz.setContentText("Create new creations to unlock additional quiz questions.");
				completedQuiz.showAndWait();

				AppWindow.valueOf("MainMenu").setScene(e);
			}

		}

		// if the user answer is wrong
		else {

			if (wrongAnswerCount == 2 || wrongAnswerCount == 3) {

				wrongAnswerCount++;

				// generate hint by replacing certain characters in the correct answer with blanks
				char[] chars = correctAnswer.toCharArray();

				String hint = "";

				int index = 0;

				for(char Char : chars) {

					if (Char == ' ') {

						hint += "  ";
					}

					else if (index % 2 != 0) {

						hint += Char;
					}


					else {

						hint += '_';
					}

					index++;

				}

				result.setText("Wrong answer. HINT: " + hint);
				result.setFill(Color.BLUE);
				answerField.clear();
			}

			// generate new hint which reveals more characters than the previous hint
			else if (wrongAnswerCount == 4) {

				wrongAnswerCount++;


				char[] chars = correctAnswer.toCharArray();

				String hint = "";

				int index = 0;

				for(char Char : chars) {

					if (Char == ' ') {

						hint += "  ";
					}

					else if (index == 0 || index == (chars.length)/2 || index == chars.length - 1) {

						hint += '_';
					}

					else {

						hint += Char;
					}

					index++;

				}

				result.setText("Wrong answer. HINT: " + hint);
				result.setFill(Color.BLUE);
				answerField.clear();

			}

			// reveal all characters in the hint apart from the 1st one
			else if (wrongAnswerCount > 4) {

				wrongAnswerCount++;

				char[] chars = correctAnswer.toCharArray();

				String hint = "";

				int index = 0;

				for(char Char : chars) {

					if (Char == ' ') {

						hint += "  ";
					}

					else if (index == 0) {

						hint += '_';
					}

					else {

						hint += Char;
					}

					index++;

				}

				result.setText("Wrong answer. HINT: " + hint);
				result.setFill(Color.BLUE);	
				answerField.clear();
			}

			else {

				result.setText("Wrong answer. Please try again! :(");
				result.setFill(Color.RED);
				wrongAnswerCount++;
				answerField.clear();
			}
		}

	}

	@FXML
	private void returnToMainMenu(ActionEvent e) throws IOException {

		AppWindow.valueOf("MainMenu").setScene(e);
	}
}
