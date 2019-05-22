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

public class RequestParser{
	
	private HashMap<String, String> requestHeaders;
	private ByteProcessing bp;
	private String code;
	private String method;
	private File filePath;
	private static String default_path = "./index.html";
	private String filePathToString;

	public RequestParser(ByteProcessing bpp){
		this.code = "";
		this.method = "";
		this.filePathToString ="";
		this.bp = bpp;
		this.requestHeaders = bpp.getRequestHeaders();
	}
	public String getFilePathToString(){
		return filePathToString;
	}
	public String getCode(){
		return code;
	}
	public String getMethod(){
		return method;
	}
	public File getFilePath(){
		return filePath;
	}
	public void parsing(){
		try{
			if(bp.getMethod().equals("GET"))
				method = "GET";
			else if(bp.getMethod().equals("POST"))
				method = "POST";
			else{
				code = "405";
			}

			if(bp.getFilePath().contains(" ")){
				code = "400";
			}

			if(bp.getFilePath().startsWith("/")){
				if(bp.getFilePath().length() > 1){
					filePath = new File(bp.getFilePath().substring(1));
					filePathToString = bp.getFilePath().substring(1);
				} else {
					filePath = new File(default_path);
					filePathToString = default_path;
				}
			}

			if(filePath.exists()){
				if((requestHeaders.get("If-None-Match") != null) && requestHeaders.get("If-None-Match").equals("\""+Long.toString(filePath.lastModified()) + "\"")){
					code = "304";
				} else {
					code = "200";
				}
			} else {
				filePath = new File("noFileIsHere");
				code = "404";
			}

		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
}