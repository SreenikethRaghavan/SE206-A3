package varpedia.controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import varpedia.processes.BashProcess;

/**
 * A singleton class used for storing the term 
 * searched by the user, which is then displayed 
 * in the video creation. It is also used to store
 * any information which is generated in one scene
 * but may be required in a different scene, for
 * example the voice and text selected by the user
 * in the select sentences scene is later required
 * in the audio name scene to create the audio
 * file using the name provided by the user. 
 * 
 * @author Sreeniketh Raghavan
 * @author Hazel Williams
 * 
 */

public class AssociationClass {

	private static AssociationClass controller;

	private String searchTerm = "  ";
	
	private int xp = 15;

	private List<String> filesToMerge;

	private String audioFile; 

	private String selectedVoice;

	private double numImages;

	private String selectedText; 
	
	private boolean backgroundMusic = false;

	private AssociationClass() { 

	}
	
	/*
	 * Save the XP to an external file. Could probably save the previous term as well.
	 */
	public void saveProgess() {
		BashProcess saving = new BashProcess();

		// save the xp to saveFile
		String command = "touch ./creation_files/temporary_files/text_files/saveFile.txt; "        
				+ "echo "+xp+" | tee ./creation_files/temporary_files/text_files/saveFile.txt";

		saving.runCommand(command);
	}
	
	/*
	 * Load the XP from an external file.
	 */
	public void loadProgess() throws IOException {
		
		File file = new File("./creation_files/temporary_files/text_files/saveFile.txt"); 
		  
		if(file.exists()) {
			BufferedReader br = new BufferedReader(new FileReader(file)); 
			
			String xptemp = br.readLine();
			if(xptemp == null) {
				xp = 10;
			} else {
				xp = Integer.parseInt(xptemp);
			}
			br.close();
		} else {
			xp = 10;
		}
		
		  
		
	}

	public static AssociationClass getInstance() {

		if (controller == null) {
			controller = new AssociationClass();
			return controller;
		}
		return controller;
	}
	
	public boolean isBGMusic() {
		return backgroundMusic;
	}
	
	public void setBGMusic(boolean x) {
		backgroundMusic = x;
	}
	
	public int getXP() {
		return xp;
	}
	
	public void increaseXP(int add) {
		xp = xp + add;
	}


	public String getSearchTerm() {
		return searchTerm;
	}


	public void storeSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}


	public List<String> getFilesToMerge() {
		return filesToMerge;
	}


	public void storeFilesToMerge(List<String> filesToMerge) {
		this.filesToMerge = filesToMerge;
	}

	public String getAudioFile() {
		return audioFile;
	}


	public void storeAudioFile(String audioFile) {
		this.audioFile = audioFile;
	}

	public String getSelectedText() {
		return selectedText;
	}


	public void storeSelectedText(String selectedText) {
		this.selectedText = selectedText;
	}

	public String getSelectedVoice() {
		return selectedVoice;
	}


	public void storeSelectedVoice(String selectedVoice) {
		this.selectedVoice = selectedVoice;
	}

	public void storeNumImages(int numImages) {
		this.numImages = (double)numImages;
	}

	public double getNumImages() {
		return numImages;
	}
}
