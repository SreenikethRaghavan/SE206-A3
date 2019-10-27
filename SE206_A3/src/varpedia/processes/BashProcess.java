package varpedia.processes;

/**
 * Class used to pass in a list of Bash commands
 * as a string, which are then processed using 
 * a ProcessBuilder object.
 * 
 * @author Sreeniketh Raghavan 
 * @author Hazel Williams
 */
public class BashProcess {

	public void runCommand(String command) {

		try {
			ProcessBuilder processBuilder = new ProcessBuilder("bash","-c",command);
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