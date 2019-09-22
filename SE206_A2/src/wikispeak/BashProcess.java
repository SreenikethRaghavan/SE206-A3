package wikispeak;


/**
 * Class used to pass in a list of Bash commands
 * as a string, which are then processed using 
 * a ProcessBuilder object.
 * 
 * @author Sreeniketh Raghavan
 * 
 */
public class BashProcess {

	public void runCommand(String command) {

		String bashCommand = "mkdir -p ./creation_files/temporary_files/audio_files; mkdir -p ./creation_files/temporary_files/video_files; "
				+ "mkdir -p ./creation_files/temporary_files/text_files; mkdir -p ./creation_files/creations; mkdir -p ./creation_files/temporary_files/image_files; " + command;

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("bash","-c",bashCommand);
			Process process = processBuilder.start();
			int exitStatus = process.waitFor();

			if (exitStatus != 0) {
				return;
			}

			process.destroy();
		}

		catch (Exception e) {
			e.printStackTrace();
		}
	}
}