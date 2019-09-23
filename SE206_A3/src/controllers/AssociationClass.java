package controllers;

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

}
