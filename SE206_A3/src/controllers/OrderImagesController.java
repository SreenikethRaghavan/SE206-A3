package controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;


import FXML.AppWindow;
import javafx.application.Platform;
//import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import wikispeak.BashProcess;


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

	private int ImageNum=0;

	private ObservableList<String> sorted;

	@FXML
	private int updateList() {
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

		int index = 1;

		for(String image : images) {
			sorted.add(image);
			index++;

		}
		
		listView.setItems(sorted);

		listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		return index-1;

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
		
		//i dont think saving it is still necessary, but ah well.
		ImageNum = updateList();


	}

	@FXML
	private void renameImages(ActionEvent e) throws IOException {
		//this is where the action happens
		//TODO: make video afterward

		
		
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
		
		Double imageDuration = ((double)index)/5.0;

		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {


				//make video afterward

				BashProcess creationProcess = new BashProcess();


				//TODO: All of this will likely ultimately be moved to the CreationNameController in the end. It is here for now so it doesn't step on anyones toes.
				//line 1: declare duration
				//line 2: this resizes and renames all the images to fit in the video. it also adds padding around the edges so that it fits the 320x240 frame instead of distorting the ratios
				//line 3: makes inital slideshow from resized images
				//line 4: adds the text onto the video (-y -> force override)
				//line 5 and 6: clean up extra files.
				String command = "image_Duration="+imageDuration+";"
						//+ " index_num=1; for i in $( ls creation_files/temporary_files/image_files); do ffmpeg -y -i ./creation_files/temporary_files/image_files/$i -vf \"scale=320:240:force_original_aspect_ratio=decrease,pad=350:250:(ow-iw)/2:(oh-ih)/2\" ./creation_files/temporary_files/image_files/img$(printf \"%02d\" $index_num).jpg -loglevel 'quiet'; index_num=$((index_num+1)); done;"
						+ " ffmpeg -framerate $image_Duration -i  ./creation_files/temporary_files/image_files/img%02d.jpg -c:v libx264 -r 24 ./creation_files/temporary_files/video_files/outputishere.mp4 -loglevel 'quiet' > /dev/null; wait;"
						+ " ffmpeg -y -i ./creation_files/temporary_files/video_files/outputishere.mp4 -vf \"drawtext=fontfile=myfont.tff:fontsize=60:fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+AssociationClass.getInstance().getSearchTerm()+"'\" -codec:a copy ./creation_files/temporary_files/video_files/output.mp4 > /dev/null; wait;"
						+ " rm -f ./creation_files/temporary_files/image_files/*; "
						+ " rm -f ./creation_files/temporary_files/video_files/outputishere.mp4;";

				creationProcess.runCommand(command);


				return null;
			}

			@Override protected void done() {
				// alert user about creation being created
				Platform.runLater(() -> {

					Alert created = new Alert(Alert.AlertType.INFORMATION);

					created.setTitle("Slideshow Created");
					created.setHeaderText("Wow that actually worked!");
					created.setContentText("Select the 'View Existing Creations' option to manage and play your creations.");
					created.showAndWait();

					try {
						AppWindow.valueOf("CreationName").setScene(e);
						return;
					} catch (IOException e1) {
						System.out.println("That didnt work: "+ e1);
					}

				});

			}
		};

		Thread thread = new Thread(task);

		//doesn't need to keep running if the program is exited?
		thread.setDaemon(true);

		thread.start();


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
		
		

		AppWindow.valueOf("MainMenu").setScene(e);
		return;
	}
}
