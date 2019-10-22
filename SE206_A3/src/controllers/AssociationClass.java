package controllers;

import java.util.List;

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
