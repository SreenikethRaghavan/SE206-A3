Authors: Sreeniketh Raghavan (srag400) and Hazel Williams (hwil965)

Target audience: Children 8 and up

For development information consult the GITHub Wiki.

# VARpedia

## RUNNING THE JAR FILE: 

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

NOTE: The BASH script, the jar file, the libs folder, the music file and the flickr-api-keys.txt file must ALL be in the 
SAME DIRECTORY on the SAME LEVEL. 

Run the executeJar.sh script file using ./executeJar.sh from the terminal. Change the permissions of the script 
file if need be. This should run the Runnable jar file and allow the user to use the application and test it thoroughly. 

=========================================================================================
SYSTEM REQUIREMENTS
runs on linux
wikit, festival, ffmpeg packages need to be installed


A User Manual on how to use the application has been provided.

-----------------------------------------------------------------------------------------

## Credits:

### Images
((images sourced from other places to be added by sree))
Other images created for use in VARpedia by Hazel Williams.

### Audio
funkTest.mp4 = Stefan Kartenberg, "*Guitalele's Happy Place*" Jun 29, 2017. Sourced Oct. 2019 Available: http://ccmixter.org/files/JeffSpeed68/56194

### Flickr API
Flickr API libraries were sourced from Flickr4Java: https://github.com/boncey/Flickr4Java
Implemented into VARpedia with code provided from Nasser (thanks Nasser)

### Other code accreditation 
VARpedia code built upon code sourced from Sreeniketh's assignment 2.
