package main.tasks;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javafx.concurrent.Task;
import main.controllers.AssociationClass;
import main.wikispeak.BashProcess;

/**
 * 
 * 
 * @author Sreeniketh Raghavan
 * @author Hazel Williams
 *
 */
public class CreateCreationTask  extends Task<Void> {
	
	private Boolean backgroundMusic = AssociationClass.getInstance().isBGMusic();
	private String backMusic = "funkTest.mp3";
	private String userInput;
	
	public CreateCreationTask(String userInput) {
		this.userInput = userInput;
	}

	@Override 
	protected Void call() throws Exception {

		BashProcess creationProcess = new BashProcess();

		//storing the names of the file in advance for code readability reasons
		String audioFileName = "creation_files/temporary_files/audio_files/" + AssociationClass.getInstance().getAudioFile() + ".wav";
		String videoFileName = "creation_files/temporary_files/video_files/output.mp4";

		//get the audio length
		//soxi wasn't behaving, so I have gone with a java based approach.
		File audioFile = new File(audioFileName);

		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);					
		AudioFormat format = audioInputStream.getFormat();							

		long audioFileLength = audioFile.length();

		int frameSize = format.getFrameSize();

		float audioframeRate = format.getFrameRate();

		float durationInSeconds = (audioFileLength / (frameSize * audioframeRate));


		//calculate the image duration and round it to something ffmpeg will tolerate. 3dp accuracy is sufficient for matching the audio length to the slideshow length.
		double frameRate = AssociationClass.getInstance().getNumImages()/durationInSeconds;
		frameRate = Math.round(frameRate*1000.0)/1000.0;

		//default font size is 60
		int fontSize = 60;

		if(AssociationClass.getInstance().getSearchTerm().length() >= 10) {
			fontSize = 40;
		}

		if(AssociationClass.getInstance().getSearchTerm().length() >= 16) {
			fontSize = 30;
		}

		if(AssociationClass.getInstance().getSearchTerm().length() > 20) {
			//this should be able to fit ~30 characters so. should be sufficient.
			fontSize = 20;
		}

		String fileTerm = AssociationClass.getInstance().getSearchTerm().trim().replace(' ', '-');

		//This command is where both the slideshow and the final creation is generated
		//line 1: establish the image_Duration variable. As this variable is only used in one place in the following code, this could be refactored out. 
		//		  However its working at the moment so no point breaking it.
		//line 2: Create the initial slide show by reading in the images, which were previously renamed and numbered in order in OrderImagesController. 
		//		  This output will be deleted after it is used.
		//line 3: Add the text to the previously generated slideshow. This is saved to the videoFileName location, and is used in the final creation. 
		//		  There is potential for customisation here with font and/or colours.
		//line 4, 5: Removing the extra files. This is important in case the user makes multiple creations in one session, as leaving these files will 
		//			 cause duplicate images in the slideshow and risk breaching Flickr's 30 image maximum user condition.
		//line 6: Combine the audio file and the video file to make the final creation video.
		String command = "image_Duration="+frameRate+";"
				+ " ffmpeg -y -framerate $image_Duration -i  ./creation_files/temporary_files/image_files/img%02d.jpg -c:v libx264 -r 24 ./creation_files/quiz_files/quiz_images/"+fileTerm+".mp4  > /dev/null; wait;"
				+ " ffmpeg -y -i ./creation_files/quiz_files/quiz_images/"+fileTerm+".mp4 -vf \"drawtext=fontfile=myfont.tff:fontsize="+fontSize+":fontcolor=white:x=(w-text_w)/2:y=(h-text_h)/2:text='"+AssociationClass.getInstance().getSearchTerm()+"'\" -codec:a copy ./creation_files/temporary_files/video_files/output.mp4 > /dev/null; wait;"
				+ " rm -f ./creation_files/temporary_files/image_files/*; ";


		if(backgroundMusic == true) {
			//need to add the background music.
			//resizing the sound to match the audio clip
			command = command +" ffmpeg -y -i "+backMusic+" -af apad -t "+durationInSeconds+" -filter:a \"volume=0.3\" ./creation_files/temporary_files/resizedFunk.wav; wait;";
			//merging sound
			command = command +" ffmpeg -y -i ./"+audioFileName+" -i ./creation_files/temporary_files/resizedFunk.wav -filter_complex amerge -ac 2 -c:a libmp3lame -q:a 4 ./creation_files/temporary_files/temp1.wav; wait;";
			//using sound to make creation
			command = command +" ffmpeg -y -i ./"+videoFileName+" -i ./creation_files/temporary_files/temp1.wav -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet> /dev/null; wait;";
		} else {
			//create with no background music
			command = command + " ffmpeg -y -i ./"+videoFileName+" -i ./"+audioFileName+" -strict experimental \"./creation_files/creations/"+userInput+".mp4\" -v quiet> /dev/null; wait;";
		}

		creationProcess.runCommand(command);

		return null;
	}

}
