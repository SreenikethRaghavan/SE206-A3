package main.tasks;

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

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import main.FXML.AppWindow;
import main.controllers.AssociationClass;
import main.wikispeak.BashProcess;

public class DownloadImagesTask  extends Task<Void> {
	private int imageNum;
	private ProgressBar progressBar;
	private Double imageDuration;
	private ActionEvent e;
	
	public DownloadImagesTask(int imageNum, ProgressBar progressBar, Double imageDuration, ActionEvent e) {
		this.imageNum = imageNum;
		this.progressBar = progressBar;
		this.imageDuration = imageDuration;
		this.e = e;
	}
	
	/**
	 * This is what enables the use of the Flickr API. Code credit to Nassar for providing the base for this function.
	 * @param key
	 * @throws Exception
	 */
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
	

	@Override 
	protected Void call() throws Exception {
		
		//trying not illegal stuff now :)
		try {
			String apiKey = getAPIKey("apiKey");
			String sharedSecret = getAPIKey("sharedSecret");

			Flickr flickr = new Flickr(apiKey, sharedSecret, new REST());
			
			String query = AssociationClass.getInstance().getSearchTerm();
			int resultsPerPage = imageNum;
			if(AssociationClass.getInstance().getSearchTerm().contentEquals(AssociationClass.getInstance().getSearchTerm().trim().replace(' ', '-'))) {
				//System.out.println("No spaces detected.");
			} else {
				resultsPerPage = resultsPerPage + 1;
				resultsPerPage = resultsPerPage - 1;
				//System.out.println("Spaces Detected.");
			}
			int page = 0;
			
	        PhotosInterface photos = flickr.getPhotosInterface();
	        SearchParameters params = new SearchParameters();
	        params.setSort(SearchParameters.RELEVANCE);
	        params.setMedia("photos"); 
	        params.setText(query);
	        
	        PhotoList<Photo> results = photos.search(params, resultsPerPage, page);
	        
	        
	        for (Photo photo: results) {
	        	try {
	        		BufferedImage image = photos.getImage(photo,Size.LARGE);
	        		//update the progress of downloading the images
	        		//this will need to be refactored so bob doesnt interact with the gui
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
		
		
		BashProcess creationProcess = new BashProcess();
		//A fault was occurring when the user entered a search term that had a space in it
		//the below command was searching to delete any images that began with the search term,
		//but of course ones with a space in them were changed so that they could be a valid file name.
		//This meant the program was not deleting the originally sized images, leading to duplicates
		//and missized images. By changing the search term in the same way we do when downloading the images,
		//this will catch that error.
		String searchTermNotSpaced = AssociationClass.getInstance().getSearchTerm().trim().replace(' ', '-');
		
		//resize and rename the images
		//delete the originals
		String command = "image_Duration="+imageDuration+";"
				//+ " rm -f ./creation_files/temporary_files/image_files/*;wait;"
				+ " index_num=1; for i in $( ls creation_files/temporary_files/image_files); do ffmpeg -y -i ./creation_files/temporary_files/image_files/$i -vf \"scale=320:240:force_original_aspect_ratio=decrease,pad=350:250:(ow-iw)/2:(oh-ih)/2\" ./creation_files/temporary_files/image_files/imageNum$(printf \"%02d\" $index_num).jpg -loglevel 'quiet'; index_num=$((index_num+1)); done;"
				+ "rm -f ./creation_files/temporary_files/image_files/"+searchTermNotSpaced+"*;"
				+"";
		
		creationProcess.runCommand(command);
		

		return null;
	}

	@Override 
	protected void done() {
		// once the images are downloaded we can move on to the next scene
		Platform.runLater(() -> {
			try {
				AppWindow.valueOf("OrderImages").setScene(e);
				return;
			} catch (IOException e1) {
				System.out.println("That didnt work: "+ e1);
			}

		});

	}

}
