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

	/**
		@param dof
			file bytes
		@param fn
			filename
		@param ft
			filetype
	*/
	public MakeFile(byte[] dof, String fn, String ft)throws IOException{
		this.filename = fn;
		this.filePath = "./temp/" + this.filename;
		this.filetype = ft;
		this.dataOfFile = new byte[dof.length];
		System.arraycopy(dof, 0, dataOfFile, 0, dof.length);
	}

	/**
		@return this.newFile
	*/
	public File newFile() throws IOException{
		String[] imageTypes = {"jpg", "ico", "png", "jpeg", "bmp"};
		if(Arrays.asList(imageTypes).contains(filetype))
			newFile = imageFile();
		else 
			newFile = textFile();
		return newFile;
	}

	/**
		make imageFile
		@return image type file 
	*/
	public File imageFile() throws IOException{
		File file = new File(filePath);
		FileOutputStream fos = new FileOutputStream(filePath);
		fos.write(dataOfFile);
		fos.flush();
		fos.close();
		return file;
	}

	/**
		make textFile
		@return text type file 
	*/
	public File textFile() throws IOException{
		System.out.println("text");
		return newFile;
	}
}