import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;


public class KeystoreGeneration{	
	public static String keystoreGen(String certFile, String certName) throws IOException, InterruptedException {
		//System.out.println("Trying to convert arm key file to jks key file.");
		String java_bin = getJavaBinDir();
		String cmd1 = "cd " + java_bin;
		String cmd2 = "keytool -import -file "
				 + certFile + " -keystore " +  certName
				+ " -storepass test1234 " + " -noprompt -trustcacerts";
		
		String rsp = "";
		if (System.getProperty("os.name").toLowerCase().contains("windows")){
			rsp = executeCmd(cmd1 );
			rsp = executeCmd(cmd2);
		}
		else {
			rsp = executeCmd(java_bin + cmd2);
		}
		return rsp;
	}


	private static  String getJavaBinDir() throws IOException,
			InterruptedException {
		//System.out.println("Trying to locate JDK directory...");
		String java_bin = "";
		char separator = '\\';
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			java_bin = executeCmd("for %i in (javac.exe) do @echo.   %~$PATH:i");
		} else {
			separator = '/';
			java_bin = executeCmd("readlink -f /usr/bin/java");
		}
		java_bin = java_bin.substring(0, java_bin.lastIndexOf(separator) + 1);
		return java_bin;

	}

	private static  String executeCmd(String cmd) throws IOException,
			InterruptedException {
	
		String output = "";
		String error = "";
		
		cmd = System.getProperty("os.name").toLowerCase().contains("windows") ? "cmd.exe /c " + cmd.replace("/", "\\") : cmd;
		//System.out.println("Execute Command: " + cmd);

		Process p = Runtime.getRuntime().exec(cmd);
		p.waitFor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

		String line;
		while ((line = reader.readLine()) != null) {
			output += line;
		}
		
		while ((line = stdError.readLine()) != null) {
			error += line;
		}
		System.out.println(error);
		return output;
	}
	
}