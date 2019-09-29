package controllers;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.flickr4java.flickr.Flickr;
import com.flickr4java.flickr.FlickrException;
import com.flickr4java.flickr.REST;
import com.flickr4java.flickr.photos.Photo;
import com.flickr4java.flickr.photos.PhotoList;
import com.flickr4java.flickr.photos.PhotosInterface;
import com.flickr4java.flickr.photos.SearchParameters;
import com.flickr4java.flickr.photos.Size;

import FXML.AppWindow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import wikispeak.BashProcess;


/**
 * Controller for the select images scene,
 * where the user chooses the number of images
 * they wish to be in the creation slideshow
 * 
 * @author Hazel Williams
 * 
 */
public class SelectImagesController {

	@FXML
	private Slider slider;
	
	@FXML
	private ProgressBar progressBar;
	
	@FXML
	private Button enterButton;
	
	@FXML
	private Button backButton;

	/**
	 * Initialise the scene. Just need to make sure the slider has the correct values.
	 * 
	 */
	@FXML
	private void initialize() throws IOException {
		
		progressBar.setVisible(false);
		progressBar.setProgress(0.0);
		
		enterButton.setDisable(false);
		backButton.setDisable(false);

		slider.setMin(1);
		slider.setMax(10);
	}

	@FXML
	private void goBack(ActionEvent e) throws IOException {

		AppWindow.valueOf("AudioFiles").setScene(e);
		return;
	}
	
	//flickr method
	public static String getAPIKey(String key) throws Exception {

		String config = System.getProperty("user.dir") 
				+ System.getProperty("file.separator")+ "flickr-api-keys.txt"; 
		File file = new File(config); 
		BufferedReader br = new BufferedReader(new FileReader(file)); 
		
		String line;
		while ( (line = br.readLine()) != null ) {
			if (line.trim().startsWith(key)) {
				br.close();
				return line.substring(line.indexOf("=")+1).trim();
			}
		}
		br.close();
		throw new RuntimeException("Couldn't find " + key +" in config file "+file.getName());
	}
	

	/**
	 * Read the slider value and use it to get the number of 
	 * sentences specified by the user.
	 * 
	 */
	@FXML
	private void readImageSlider(ActionEvent e) throws IOException {
		
		//this will send the task to a new thread to download the images and create the slideshow
		progressBar.setVisible(true);
		//progressBar.progressProperty().add(1);
		enterButton.setDisable(true);
		backButton.setDisable(true);
		
		int imageNum = (int)slider.getValue();
		//place holder value. TODO: get the length of the audio file, this value should be imageNum/audioLength
		Double imageDuration = ((double)imageNum)/5.0;
		//System.out.println(imageDuration);
		
		//AssociationClass.getInstance().getSearchTerm() 
		
		//make a new task to get images and make the slideshow
		Task<Void> task = new Task<Void>() {
			@Override protected Void call() throws Exception {
				
				//trying not illegal stuff now :)
				try {
					String apiKey = getAPIKey("apiKey");
					String sharedSecret = getAPIKey("sharedSecret");

					Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
					
					String query = AssociationClass.getInstance().getSearchTerm();
					int resultsPerPage = imageNum;
					int page = 0;
					
			        PhotosInterface photos = flickr.getPhotosInterface();
			        SearchParameters params = new SearchParameters();
			        params.setSort(SearchParameters.RELEVANCE);
			        params.setMedia("photos"); 
			        params.setText(query);
			        
			        PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
			        //System.out.println("Retrieving " + results.size()+ " results");
			        
			        
			        for (Photo photo: results) {
			        	try {
			        		BufferedImage image = photos.getImage(photo,Size.LARGE);
			        		progressBar.setProgress(progressBar.getProgress()+1.0/(results.size()));
				        	String filename = query.trim().replace(' ', '-')+"-"+System.currentTimeMillis()+"-"+photo.getId()+".jpg";
				        	File outputfile = new File("creation_files/temporary_files/image_files",filename);
				        	ImageIO.write(image, "jpg", outputfile);
				        	//System.out.println("Downloaded "+filename);
			        	} catch (FlickrException fe) {
			        		//System.err.println("Ignoring image " +photo.getId() +": "+ fe.getMessage());
						}
			        }
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				//make video afterward
				
				BashProcess creationProcess = new BashProcess();
				
				
				//resize and rename the images
				//delete the originals
				String command = "image_Duration="+imageDuration+";"
						//+ " rm -f ./creation_files/temporary_files/image_files/*;wait;"
						+ " index_num=1; for i in $( ls creation_files/temporary_files/image_files); do ffmpeg -y -i ./creation_files/temporary_files/image_files/$i -vf \"scale=320:240:force_original_aspect_ratio=decrease,pad=350:250:(ow-iw)/2:(oh-ih)/2\" ./creation_files/temporary_files/image_files/imageNum$(printf \"%02d\" $index_num).jpg -loglevel 'quiet'; index_num=$((index_num+1)); done;"
						+ "rm -f ./creation_files/temporary_files/image_files/"+AssociationClass.getInstance().getSearchTerm()+"*;"
						+"";
				
				creationProcess.runCommand(command);
				

				return null;
			}

			@Override protected void done() {
				// alert user about creation being created
				Platform.runLater(() -> {
					
					Alert created = new Alert(Alert.AlertType.INFORMATION);

					created.setTitle("Images Finished Downloading");
					created.setHeaderText("All Images Downloaded");
					created.setContentText("Continue to view images.");
					created.showAndWait();

					try {
						AppWindow.valueOf("OrderImages").setScene(e);
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


}
