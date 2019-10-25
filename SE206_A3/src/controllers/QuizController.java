package controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import FXML.AppWindow;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Controller for the Quiz scene where the user 
 * is is allowed to test their learning. The user
 * is expected to enter the term which describes 
 * the image being displayed (which will be taken 
 * from one of the creations they have created and
 * the expected answer is the term they searched for.  
 * 
 * @author Sreeniketh Raghavan
 * @author Hazel Williams
 * 
 */
public class QuizController {

	@FXML
	private MediaView mediaView;

	@FXML
	private Button enterButton;

	@FXML
	private TextField answerField;

	@FXML
	private Text result;


	private int wrongAnswerCount = 0;
	private int xpIncrease=0;
	private MediaPlayer player;

	String fileLocation = "./creation_files/quiz_files/quiz_images";
	ObservableList<String> urlList;

	private ObservableList<String> getURLList() {
		List<String> creations = new ArrayList<String>();

		File[] files = new File(fileLocation).listFiles();

		// remove creation extensions (.mp4)
		// is this necessary? 
		if (files.length != 0) {
			for (File file : files) {
				if (file.isFile()) {
					String name = file.getName();
					name = name.substring(0, name.lastIndexOf("."));
					creations.add(name);
				}
			}
		}

		// to randomise the video being shown (don't want the same video to be the first question every time)
		Collections.shuffle(creations);

		ObservableList<String> sorted = FXCollections.observableArrayList();

		// add the videos to the observable list
		for(String creation : creations) {
			sorted.add(fileLocation+"/"+creation+".mp4");
		}

		return sorted;


	}


	@FXML
	private void initialize() {

		//get the list of mp4 urls in the folder
		urlList = getURLList();

		// select an video at random and quiz the user on it
		String randomVideoURL = urlList.get((int)Math.random()*urlList.size());
		File fileUrl = new File(randomVideoURL);
		Media video = new Media(fileUrl.toURI().toString());
		player = new MediaPlayer(video);
		player.setAutoPlay(true);
		//repeats the video once the video ends
		player.setOnEndOfMedia(new Runnable() {
			@Override
			public void run() {
				player.seek(Duration.ZERO);
				player.play();
			}
		}); 
		mediaView.setMediaPlayer(player);

		// in order to avoid repeat questions
		urlList.remove(randomVideoURL);

		enterButton.disableProperty().bind(
				Bindings.isEmpty(answerField.textProperty()));

	}

	@FXML
	private void checkAnswer(ActionEvent e) throws IOException {

		String answer = answerField.getText();

		Path path = Paths.get(mediaView.getMediaPlayer().getMedia().getSource());



		String videoName = path.getFileName().toString();
		String correctAnswer = videoName.substring(0, videoName.lastIndexOf(".")).trim().replace('-', ' ');;

		if(answer.equalsIgnoreCase(correctAnswer)) {

			if(wrongAnswerCount==0) {
				xpIncrease = 20;
			} else if(wrongAnswerCount<2) {
				xpIncrease = 10;
			} else if(wrongAnswerCount<3) {
				xpIncrease = 5;
			} else {
				xpIncrease = 1;
			}

			result.setText("Correct answer. Well done! :D (+"+xpIncrease+"XP)");
			result.setFill(Color.GREEN);
			AssociationClass.getInstance().increaseXP(xpIncrease);

			wrongAnswerCount = 0;

			// avoid duplication of questions
			urlList.remove(mediaView.getMediaPlayer().getMedia().getSource());
			answerField.clear();

			if(!urlList.isEmpty()) {
				String randomVideoURL = urlList.get((int)Math.random()*urlList.size());
				File fileUrl = new File(randomVideoURL);
				Media video = new Media(fileUrl.toURI().toString());
				//stop whatever video was playing
				player.stop();
				player = new MediaPlayer(video);
				player.play();
				player.setAutoPlay(true);
				//repeats the video once the video ends
				player.setOnEndOfMedia(new Runnable() {
					@Override
					public void run() {
						player.seek(Duration.ZERO);
						player.play();
					}
				}); 
				mediaView.setMediaPlayer(player);

				// in order to avoid repeat questions
				urlList.remove(randomVideoURL);

			}

			else {

				Alert completedQuiz = new Alert(Alert.AlertType.INFORMATION);
				completedQuiz.setTitle("Quiz Completed");
				completedQuiz.setHeaderText("Congratulations! You have successfully completed the quiz :D");
				completedQuiz.setContentText("Create new creations to unlock additional quiz questions.");
				completedQuiz.showAndWait();

				AppWindow.valueOf("LearnMenu").setScene(e);
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

		AppWindow.valueOf("LearnMenu").setScene(e);
	}
}
