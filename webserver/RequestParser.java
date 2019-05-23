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
	private String code;
	private String method;
	private File filePath;
	private static String default_path = "./index.html";
	private String filePathToString;

	private String bpMethod;
	private String bpFilePath;

	public RequestParser(HashMap<String, String> reqheaders){
		this.code = "";
		this.method = "";
		this.filePathToString ="";
		this.requestHeaders = reqheaders;
	}
	public HashMap<String, String> getRequestHeaders(){
		return requestHeaders;
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
	public void setBPVariables(String method, String filePath){
		this.bpMethod = method;
		this.bpFilePath = filePath;
	}
	public void parsing(){
		try{
			if(bpMethod.equals("GET"))
				method = "GET";
			else if(bpMethod.equals("POST"))
				method = "POST";
			else{
				code = "405";
			}

			if(bpFilePath.contains(" ")){
				code = "400";
			}

			if(bpFilePath.startsWith("/")){
				if(bpFilePath.length() > 1){
					filePath = new File(bpFilePath.substring(1));
					filePathToString = bpFilePath.substring(1);
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