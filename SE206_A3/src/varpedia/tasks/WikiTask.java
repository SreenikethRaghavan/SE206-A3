package varpedia.tasks;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import varpedia.scene.AppWindow;
import varpedia.processes.BashProcess;

public class WikiTask extends Task<Void> {

	private String searchTerm;
	private ActionEvent e;

	public WikiTask(String searchTerm, ActionEvent e) {
		this.searchTerm = searchTerm;
		this.e = e;
	}

	@Override 
	protected Void call() throws Exception {

		BashProcess wikit = new BashProcess();

		// Use the wikit bash command to search for the wikipedia entry corresponding the search term. 
		// Format the wikit output and store it in a text file
		String command = "touch ./creation_files/text_files/wikipedia_output.txt; "        
				+ "wikit " + searchTerm +" | sed 's/\\([.?!]\\) \\([[:upper:]]\\)/\\1\\n\\2/g' | sed 's/  //g' | tee ./creation_files/text_files/wikipedia_output.txt";

		wikit.runCommand(command);
		return null;
	}

	@Override 
	protected void done() {

		Platform.runLater(() -> {
			BufferedReader reader = null;

			try {
				reader = new BufferedReader(new FileReader("./creation_files/text_files/wikipedia_output.txt"));
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

}
