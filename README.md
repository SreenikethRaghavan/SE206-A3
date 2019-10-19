Authors: Sreeniketh Raghavan (srag400) and Hazel Williams (hwil965)

Target audience: Children 8 and up

For development information consult the GITHub Wiki.

========================================================================================
		  _______  _______  _______  _______  ______  _________ _______ 
	|\     /|(  ___  )(  ____ )(  ____ )(  ____ \(  __  \ \__   __/(  ___  )
	| )   ( || (   ) || (    )|| (    )|| (    \/| (  \  )   ) (   | (   ) |
	| |   | || (___) || (____)|| (____)|| (__    | |   ) |   | |   | (___) |
	( (   ) )|  ___  ||     __)|  _____)|  __)   | |   | |   | |   |  ___  |
	 \ \_/ / | (   ) || (\ (   | (      | (      | |   ) |   | |   | (   ) |
	  \   /  | )   ( || ) \ \__| )      | (____/\| (__/  )___) (___| )   ( |
	   \_/   |/     \||/   \__/|/       (_______/(______/ \_______/|/     \|
=========================================================================================


RUNNING THE JAR FILE: 

Test the jar file on the UPDATED Virtual Box image. The java jdk version which needs to be used is jdk 13 
(java 13) which can be found in /home/student/Downloads/openjdk-13_linux-x64_bin/jdk-13/bin/java on the 
NEW image (if the path for the jdk has not been changed).


We also need the JavaFX jars to be available in the location 
/home/student/Downloads/openjfx-13-rc+2_linux-x64_bin-sdk/javafx-sdk-13/lib directory 
in the Virtual Box. 


Extract the SE206_A3.zip zip file in an appropriate directory. Once extracted, the directory should contain a 
SE206_A3 directory inside it. This directory contains: 

1. The src folder (contains the source code for the assignment)
2. The SE206_A3.jar file
3. executeJar.sh BASH script (used to run the jar file as it contains the JVM arguments needed to execute the jar file)
4. The libs folder (contains the flickr libraries which are IMPORTANT)
5. The flickr-api-keys.txt file (contains the flickr API key which is IMPORTANT)

NOTE: The BASH script, the jar file, the libs folder and the flickr-api-keys.txt file must ALL be in the 
SAME DIRECTORY on the SAME LEVEL. 

Run the executeJar.sh script file using ./executeJar.sh from the terminal. Change the permissions of the script 
file if need be. This should run the Runnable jar file and allow the user to use the application and test it thoroughly. 

=========================================================================================

HOW TO USE THE INTERFACE:

You will start on a menu that gives you three options, as explained below

=========================================================================================

	   ______  _______     ________       _     _________  ________  
	 .' ___  ||_   __ \   |_   __  |     / \   |  _   _  ||_   __  | 
	/ .'   \_|  | |__) |    | |_ \_|    / _ \  |_/ | | \_|  | |_ \_| 
	| |         |  __ /     |  _| _    / ___ \     | |      |  _| _  
	\ `.___.'\ _| |  \ \_  _| |__/ | _/ /   \ \_  _| |_    _| |__/ | 
	 `.____ .'|____| |___||________||____| |____||_____|  |________|

-----------------------------------------------------------------------------------------

This is where you get to create a new creation! Here's how:

Getting things started: 
Search for a search term. 
Edit the text in the text area. 
Select the text you wish to use. 
Choose a voice and test the audio. If nothing is played, choose a different chunk of text. Create the audio file. 
If you have already searched for a term, you will be given the option to either make a new creation or use the term you
have already searched. If you choose to use the previous, you will skip to the text editing screen.

Combining audio files:
Play or delete existing audio files.Choose 2 or more audio files to combine or use a single existing file. 

Selecting number of images:
You will be prompted to choose the number of images to download for your creation slideshow. 
You can download between 1 and 10 images, the maximum is there to preserve the terms of service of the Flickr API. 
On the next page you will be able to preview, reorder and delete any unwanted images, and you can always come back 
to this page if you decide you want a different number of images.

Ordering images:
Select and image in the list to preview it. Use the buttons to move it up and down in the list to change the order of 
the slideshow, images further up the list will be displayed first. You can also delete unwanted images by selecting 
them and clicking the trashbin. When you are satisified with the order of these images, click next to move to the next screen.

=========================================================================================

	  _____     ________       _       _______     ____  _____  
	 |_   _|   |_   __  |     / \     |_   __ \   |_   \|_   _| 
	   | |       | |_ \_|    / _ \      | |__) |    |   \ | |   
	   | |   _   |  _| _    / ___ \     |  __ /     | |\ \| |   
	  _| |__/ | _| |__/ | _/ /   \ \_  _| |  \ \_  _| |_\   |_  
	 |________||________||____| |____||____| |___||_____|\____| 
                                                            
-----------------------------------------------------------------------------------------

Play the quiz to learn some stuff!

Guess the subject of the creation images! You will get hints if you are struggling.
Type your answer in the text box and click enter.



=========================================================================================

	 ____      ____  _     _________    ______  ____  ____  
	|_  _|    |_  _|/ \   |  _   _  | .' ___  ||_   ||   _| 
	  \ \  /\  / / / _ \  |_/ | | \_|/ .'   \_|  | |__| |   
	   \ \/  \/ / / ___ \     | |    | |         |  __  |   
	    \  /\  /_/ /   \ \_  _| |_   \ `.___.'\ _| |  | |_  
	     \/  \/|____| |____||_____|   `.____ .'|____||____| 

-----------------------------------------------------------------------------------------

Review existing creations!


Playing a Creation:
Select the desired creation from the right hand list and click the play button.
While the video is playing you will have the option to pause/play it, and to stop
the video entirely. You may also start playing another video by selecting it and clicking play.
This will stop the previous video, and start playing the new one.
Feel free to delete videos while a video is playing, it won't effect it.

Deleting a Creation:
Select the creation you wish to delete from the right hand list and click delete.
You will be prompted for confirmation. If you confirm, the video will be deleted
and you will be unable to play it again. 

