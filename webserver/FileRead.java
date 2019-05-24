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
import java.util.ArrayList;

public class FileRead{
	private File file;
	private int fileLength;
	private FileInputStream fi;
	private byte[] fileData;

	public FileRead(File f){
		this.file = f;
		this.fileLength = (int)f.length();
		this.fileData = new byte[(int)f.length()];
	}
	public byte[] getFileData(){
		return fileData;
	}
	public int getFileLength(){
		return fileLength;
	}
	public void fread() throws IOException{
		try{
			fi = new FileInputStream(file);
			fi.read(fileData);
		} catch(Exception e){
			e.printStackTrace(System.out);
		} finally{
			if(fi != null)
				fi.close();
		}
	}
}