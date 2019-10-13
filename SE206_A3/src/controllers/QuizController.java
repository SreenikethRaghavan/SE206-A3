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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class QuizController {

	@FXML
	private ImageView imageView;

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
		Collections.shuffle(fileList);

		for (File file : files) {
			if (file.isFile()) {

				Image image = new Image("file:" + file.getPath().substring(2));
				imageList.add(image);				
			}
		}

		if(!imageList.isEmpty()) {
			Image randomImage = imageList.get((int)Math.random()*imageList.size());
			imageView.setImage(randomImage);		
			imageList.remove(randomImage);

		}

		else {

			Alert noCreations = new Alert(Alert.AlertType.INFORMATION);
			noCreations.setTitle("No Existing Creations");
			noCreations.setHeaderText("There are currently no creations to quiz you on.");
			noCreations.setContentText("Kindly quit and create a creation to unlock the quiz component. ");
			noCreations.showAndWait();	
		}

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

		else {

			if (wrongAnswerCount == 2 || wrongAnswerCount == 3) {

				wrongAnswerCount++;

				char[] chars = correctAnswer.toCharArray();

				String hint = "";

				int index = 0;

				for(char Char : chars) {

					if (index % 2 != 0) {

						hint += Char;
					}

					else {

						hint += '_';
					}

					index++;

				}

				result.setText("Wrong answer.\nHINT: " + hint);
				result.setFill(Color.BLUE);
				answerField.clear();
			}

			else if (wrongAnswerCount == 4) {

				wrongAnswerCount++;


				char[] chars = correctAnswer.toCharArray();

				String hint = "";

				int index = 0;

				for(char Char : chars) {

					if (index == 0 || index == (chars.length)/2 || index == chars.length - 1) {

						hint += '_';
					}

					else {

						hint += Char;
					}

					index++;

				}

				result.setText("Wrong answer.\nHINT: " + hint);
				result.setFill(Color.BLUE);
				answerField.clear();

			}

			else if (wrongAnswerCount > 4) {

				wrongAnswerCount++;


				char[] chars = correctAnswer.toCharArray();

				String hint = "";

				int index = 0;

				for(char Char : chars) {

					if (index == 0) {

						hint += '_';
					}

					else {

						hint += Char;
					}

					index++;

				}

				result.setText("Wrong answer.\nHINT: " + hint);
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
