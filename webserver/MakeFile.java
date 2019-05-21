package webserver;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.io.FileInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.*;
import java.util.Date;
import java.lang.String;
import java.text.SimpleDateFormat; // POST. date print
import java.io.FileWriter; 
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;    // for Image parsing
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class MakeFile{
	private byte[] dataOfFile;
	private String filename = "";
	private String filePath = "";
	private String filetype = "";
	private File newFile;

	public MakeFile(byte[] dof, String fn, String ft)throws IOException{
		this.filename = fn;
		this.filePath = "./temp/" + this.filename;
		this.filetype = ft;
		this.dataOfFile = new byte[dof.length];
		System.arraycopy(dof, 0, dataOfFile, 0, dof.length);
	}

	public File detectFileType() throws IOException{
		String[] imageTypes = {"jpg", "ico", "png", "jpeg", "bmp"};
		File file;
		if(Arrays.asList(imageTypes).contains(this.filetype))
			file = imageFile();
		else 
			file = textFile();
		return file;
	}

	public File imageFile() throws IOException{
		File file = new File(this.filePath);
		System.out.println(filetype);
		FileOutputStream fos = new FileOutputStream(this.filePath);
		fos.write(this.dataOfFile);
		fos.flush();
		fos.close();
		return file;
	}
	public File textFile() throws IOException{
		System.out.println("text");
		return this.newFile;
	}
}
