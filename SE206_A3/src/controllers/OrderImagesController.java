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
 * where the user can reorder the images in the slideshow before they go into a video
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

	@FXML
	private void updateList() {
		List<String> images = new ArrayList<String>();

		File[] files = new File("creation_files/temporary_files/image_files/").listFiles();

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

		sorted = FXCollections.observableArrayList();

		

		for(String image : images) {
			sorted.add(image);

		}
		
		listView.setItems(sorted);

		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		//return index-1;

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
		
		AssociationClass.getInstance().storeNumImages(index);
		
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
	@FXML
	private void deleteImage(ActionEvent e) throws IOException {

		String selected = listView.getSelectionModel().getSelectedItem();

		// if something is selected
		if (selected != null) {
			if(sorted.size() <= 1) {
				//cant delete the last element
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

					if(selection == ButtonType.OK) {
						File file = new File("./creation_files/temporary_files/image_files/"+ selected);
						file.delete();
						//delete from the array
						sorted.remove(index);
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
				//if its 0 we dont want to go up again
				String temp = sorted.get(index-1);
				sorted.remove(index-1);
				sorted.add(index,temp);
				//
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
			//want to temporarily store the item and swap it with the index thats just above it
			if(index != sorted.size()-1) {
				String temp = sorted.get(index+1);
				sorted.remove(index+1);
				sorted.add(index,temp);
				//
				listView.setItems(sorted);
			}

		}
		
		return;
	}
	

	//actually returns to main menu lmaoo
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
