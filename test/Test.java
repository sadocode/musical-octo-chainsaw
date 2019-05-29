package test;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.io.OutputStreamWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test {

	public static void main(String[] args) {
		try {
			String[] cmd = new String[] {"cmd", "dir", "/w"};
			//ProcessBuilder pb = new ProcessBuilder(cmd);
			ProcessBuilder pb = new ProcessBuilder("cmd");
			pb.redirectErrorStream(true);
			Process p = pb.start();
		
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
			
			bw.write("dir\n");
			bw.flush();
			System.out.println("e");
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String outputLine = "";
			String outputMessage = "";
			while((outputLine = br.readLine()) != null) {
				outputMessage += outputLine + "\r\n";
				System.out.print(outputLine);
			}
			p.waitFor();
			System.out.println("###");
			System.out.print(outputMessage);
			p.waitFor();
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}

}
