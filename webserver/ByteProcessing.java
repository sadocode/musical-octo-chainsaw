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

public class ByteProcessing{
	private byte[] request;
	private String method;
	private String filePath;
	private String boundary;
	private HashMap<String, String> requestHeaders;
	private ReadBody rb;

	public ByteProcessing(byte[] req){
		this.requestHeaders = new HashMap<String, String>();
		this.method = "";
		this.filePath = "";
		this.boundary = "";
		this.request = new byte[req.length];
		System.arraycopy(req, 0, request, 0, req.length);
	}
	public ReadBody getReadBody(){
		return rb;
	}
	public String getBoundary(){
		return boundary;
	}
	public String getFilePath(){
		return filePath;
	}
	public String getMethod(){
		return method;
	}
	public HashMap<String, String> getRequestHeaders(){
		return requestHeaders;
	}
	public void processBytes(){
		try{
			int indexStart = 0;
			String readLine = "";
			int lineNumber = 0;
			boolean newLine = false;
			StringTokenizer st;
			int index;
			int byteCheck = 0;
			byte[] lineByte;
			
			for(int i = 0; i < request.length; i++){
                
                if(newLine == true){
                    lineByte = new byte[byteCheck];
                    System.arraycopy(request, indexStart, lineByte, 0, byteCheck);
                    readLine = new String(lineByte, "utf-8");        

                    //only for first line of request header
                    if(lineNumber == 1){
                        st = new StringTokenizer(readLine);                 
                        method = st.nextToken();                      
                        filePath = st.nextToken();
                    }

                    //for parsing request headers and store headers in Hashmap
                    if((index = readLine.indexOf(":"))!= -1){
                        String reqH1 = readLine.substring(0, index);
                        String reqH2 = readLine.substring(index+2);
                        requestHeaders.put(reqH1, reqH2);
                    }

                    //when the method of request is post
                    if(readLine.contains("boundary=")){
                        index = requestHeaders.get("Content-Type").indexOf("=");
                        boundary = "--" + requestHeaders.get("Content-Type").substring(index +1);
                        requestHeaders.put("boundary", boundary);
                    }

                    if(readLine.equals(boundary)){
                        lineByte = new byte[request.length - indexStart];
                        System.arraycopy(request, indexStart, lineByte, 0, lineByte.length);
                        rb = new ReadBody(lineByte, boundary);
                        break;
                    }
                    //if the method is post and last boundary read -> break
                    
                    // reset line offsets
                    byteCheck = 0;
                    indexStart = i;         
                }

                byteCheck++;
                newLine = false;
                
                //if \r\n checked
                if(request[i] == 13 && request[i+1] ==10){                  
                    byteCheck++;
                    if(i + 1 == request.length){ //finish of the request!!
                        // all last line of requests is '\r\n'. we don't have to store it. 
                        break;
                    } 
                    else 
                    {//only line finished. not all of the request
                        i++;
                        newLine = true;
                        lineNumber++;
                    } 
                }
            }
		} catch(Exception e){
			e.printStackTrace(System.out);
		}	
	}
}