package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;


import FXML.AppWindow;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;


/**
 * Controller for the order images scene,
 * where the user can reorder and delete images in the slideshow before they go into a video
 * 
 * @author Hazel Williams
 * 
 */
public class OrderImagesController {


	@FXML
	private ListView<String> listView;

	@FXML
	private Button backButton;

	@FXML
	private Button nextButton;

	@FXML
	private ImageView imageView;
	
	@FXML
	private ImageView deleteImageView;
	@FXML
	private ImageView moveUpImageView;
	@FXML
	private ImageView moveDownImageView;

	private ObservableList<String> sorted;
	
	/**
	 * updateList is used to initialize and reset the listView of the images that have been downloaded for the slideshow.
	 * calling it will reset the order of the images, as it creates a new array and fills it directly from the directory,
	 * not taking into account any order changes the user has made. This is useful for initalizing, but changing the list
	 * view is actually done with individual actions on an existing array.
	 */
	@FXML
	private void updateList() {
		List<String> images = new ArrayList<String>();

		File[] files = new File("creation_files/temporary_files/image_files/").listFiles();
		
		//adding all the downloading image file names to the array
		if (files.length != 0) {
			for (File file : files) {
				if (file.isFile()) {
					String name = file.getName();
					//name = name.substring(0, name.lastIndexOf("."));
					images.add(name);
				}
			}
		}
		else {
			// if no images exist
			images.add("Error: no images found.");

		}
		
		//this isnt neccessary, but why not haha
		sorted = FXCollections.observableArrayList();

		

		for(String image : images) {
			sorted.add(image);

		}
		
		listView.setItems(sorted);

		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

	}


	@FXML
	private void initialize() {
		//setting up the images for the buttons
		Image deleteImage = new Image((new File("src/images/bin.png").toURI().toString()));
		deleteImageView.setImage(deleteImage);
		
		Image upImage = new Image((new File("src/images/upArrow.png").toURI().toString()));
		moveUpImageView.setImage(upImage);
		
		Image downImage = new Image((new File("src/images/downArrow.png").toURI().toString()));
		moveDownImageView.setImage(downImage);
		
		//preview the image :)
		//essentially i have set up a imageview which i just switch the image on for whatever list item is currently selected. it is
		//updated every time you click on the listView, which could be clicking the same item or a new item, but will always display
		//the selected item.
		listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {			
				String selected = listView.getSelectionModel().getSelectedItem();
				Image selectedImage = new Image((new File("creation_files/temporary_files/image_files/"+selected).toURI().toString()));
				imageView.setImage(selectedImage);

			}
		});
		
		
		updateList();


	}
	
	/**
	 * This is activated on trying to go to the next scene.
	 * In order for ffmpeg to generate the slideshow, it needs the images to have a naming pattern of img01 img02... etc
	 * where img01 comes before img02 in the slideshow. 
	 * This function renames all the images to that pattern, following the order the user has edited.
	 * It then navigates to the creation scene.
	 * No multithreading is necessary for this as even with the maximum number of images, 10, this process happens to quickly
	 * to notice any GUI delays. Plus, multithreading this risks errors in the next scene as it relies on the images already being renamed.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void renameImages(ActionEvent e) throws IOException {
		
		//for each image in the array
		//rename its corresponding file to the img00 format
		int index = 0;
		for(String imageName : sorted) {
			index++;
			//sorted.add(image);
			File f1 = new File("creation_files/temporary_files/image_files/"+imageName);
			File f2 = new File("creation_files/temporary_files/image_files/img0"+index+".jpg");
			if(index==10) {
				f2 = new File("creation_files/temporary_files/image_files/img10.jpg");
			}
			boolean b = f1.renameTo(f2);
			if(b) {
				//System.out.println("Renamed successfully");
			}

		}
		
		//store the number of images, post user deleting unwanted ones, so that it can be retrieved in the creation scene to determine the framerate.
		AssociationClass.getInstance().storeNumImages(index);
		
		//move to the next scene
		try {
			AppWindow.valueOf("CreationName").setScene(e);
			return;
		} catch (IOException e1) {
			System.out.println("That didnt work: "+ e1);
		}


		//AppWindow.valueOf("CreationName").setScene(e);
		return;
	}
	
	
	//deletes the selected image (after prompting confirmation)
	/**
	 * Deletes the selected image, after prompting the user for confirmation.
	 * It also prevents the deletion if there is only one image left, so that no errors are produced by not having any images.
	 * This could be edited to give the user the option to make a creation with no images, instead selecting a solid colour background.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void deleteImage(ActionEvent e) throws IOException {
		//determine the currently selected item
		String selected = listView.getSelectionModel().getSelectedItem();

		// if something is selected
		if (selected != null) {
			if(sorted.size() <= 1) {
				//cant delete the last element - would create errors in creation generation
				Alert noDeleteAlert = new Alert(Alert.AlertType.INFORMATION);
				noDeleteAlert.setTitle("Sorry");
				noDeleteAlert.setHeaderText("You cannot delete the last image");
				noDeleteAlert.setContentText("There must be at least 1 image in the creation");
				
				noDeleteAlert.show();
				
			} else {
				
				int index = listView.getSelectionModel().getSelectedIndex();
				Alert deleteAlert = new Alert(Alert.AlertType.CONFIRMATION);

				deleteAlert.setTitle("Deletion Confirmation");
				deleteAlert.setHeaderText("Are you sure that you want to delete the this image?");
				deleteAlert.setContentText("This action CANNOT be undone!");

				deleteAlert.showAndWait().ifPresent(selection -> {
					//confirm before deleting the file
					if(selection == ButtonType.OK) {
						//delete from directory
						File file = new File("./creation_files/temporary_files/image_files/"+ selected);
						file.delete();
						//delete from the array
						sorted.remove(index);
						//update the list to reflect this change, but dont reset the ordering.
						listView.setItems(sorted);
					}
				});
				
			}
			
		}

		
	}
	
	//moves the selected image up in the list
	@FXML
	private void moveUp(ActionEvent e) throws IOException {

		String selected = listView.getSelectionModel().getSelectedItem();
		
		if(selected != null) {
			//we have got something selected
			int index = listView.getSelectionModel().getSelectedIndex();
			//want to temporarily store the item and swap it with the index thats just above it
			if(index != 0) {
				//if its 0 we dont want to go up again, avoids arrayoutofbounds
				String temp = sorted.get(index-1);
				sorted.remove(index-1);
				sorted.add(index,temp);
				//update view
				listView.setItems(sorted);
			}
			
		}
		
		return;
	}
	
	//moves the selected image down in the list
	@FXML
	private void moveDown(ActionEvent e) throws IOException {
		
		String selected = listView.getSelectionModel().getSelectedItem();
		Image selectedImage = new Image((new File("creation_files/temporary_files/image_files/"+selected).toURI().toString()));
		imageView.setImage(selectedImage);
		//int index = listView.getSelectionModel().getSelectedIndex();
		
		if(selected != null) {
			//we have got something selected
			int index = listView.getSelectionModel().getSelectedIndex();
			//want to temporarily store the item and swap it with the index thats just below it
			if(index != sorted.size()-1) {
				//the last item cannot go down any further
				String temp = sorted.get(index+1);
				sorted.remove(index+1);
				sorted.add(index,temp);
				//update the view
				listView.setItems(sorted);
			}

		}
		
		return;
	}
	

	/**
	 * Returns to the download images scene. Before that, it deletes all the images already downloaded.
	 * This is important because the Flickr API terms of service specifies that you can't display more
	 * than 30 images at once. So to avoid breaching this, we need to make sure the users can only have
	 * a limited number of images downloaded at once.
	 * @param e
	 * @throws IOException
	 */
	@FXML
	private void returnToSelectImages(ActionEvent e) throws IOException {
		//Delete any images currently there
		for(String imageName : sorted) {
			File file = new File("./creation_files/temporary_files/image_files/"+ imageName);
			file.delete();

		}
		
		

		AppWindow.valueOf("SelectImages").setScene(e);
		return;
	}
}
