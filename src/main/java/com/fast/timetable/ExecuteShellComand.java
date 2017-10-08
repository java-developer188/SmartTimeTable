package com.fast.timetable;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class ExecuteShellComand {
	boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");

	public static void main(String[] args) {

		ExecuteShellComand obj = new ExecuteShellComand();

		String domainName = "./RefineAndConvert.sh";

		String output = obj.executeCommand(domainName);
		System.out.println(output);

	}
	
	public void executeOpenRefineScript(){
		executeScripts("./RefineAndConvert.sh");
	}
	
	public void executeCompleteSchemaScript(){
		executeScripts("./fullInitializeScript.sh");
	}
	
	public void executePartialSchemaScript(){
		executeScripts("./partialInitializeScript.sh");
	}
	
	public void executeScripts(String scriptPath){
		String output = executeCommand(scriptPath);
		System.out.println(output);
		
	}

	private String executeCommand(String command) {

		StringBuffer output = new StringBuffer();

		Process p;
		try {
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return output.toString();

	}

	public void execute() throws IOException, Exception {
		ProcessBuilder builder = new ProcessBuilder();
		if (isWindows) {
			builder.command("cmd.exe", "/c", "dir");
		} else {
			builder.command("./RefineAndConvert.sh");
		}
		builder.directory(new File(System.getProperty("user.home")));
		Process process = builder.start();
		StreamGobbler streamGobbler = new StreamGobbler(process.getInputStream(), System.out::println);
		Executors.newSingleThreadExecutor().submit(streamGobbler);
		int exitCode = process.waitFor();
		assert exitCode == 0;
	}

	private static class StreamGobbler implements Runnable {
		private InputStream inputStream;
		private Consumer<String> consumer;

		public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
			this.inputStream = inputStream;
			this.consumer = consumer;
		}

		@Override
		public void run() {
			new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(consumer);
		}
	}

}