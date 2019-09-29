package controllers;

import java.util.List;

/**
 * A singleton class used for storing the term 
 * searched by the user, which is then displayed 
 * in the video creation.
 * 
 * @author Sreeniketh Raghavan
 * 
 */

public class AssociationClass {

	private static AssociationClass controller;

	private String searchTerm;

	private List<String> filesToMerge;

	private String audioFile; 

	private String selectedVoice;
	
	private double numImages;

	private AssociationClass() { 

	}

	public static AssociationClass getInstance() {

		if (controller == null) {
			controller = new AssociationClass();
			return controller;
		}
		return controller;
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
