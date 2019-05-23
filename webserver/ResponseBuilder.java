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

public class ResponseBuilder{
	private RequestParser rp;
	private Date today = new Date();
	private String response;
	private boolean isError = false;
	private File file;

	public ResponseBuilder(RequestParser rpp){
		this.rp = rpp;
		this.response = "";
		this.file = rpp.getFilePath();
	}
	public String getResponse(){
		return response;
	}
	public void build(){
		if(rp.getCode().equals("405")){
			System.out.println("405 Method Not Allowed");
            response = "HTTP/1.1 405 Method Not Allowed \r\nConnection: close\r\n";
            isError = true;
		} else if(rp.getCode().equals("400")){
            System.out.println("400 BAD REQUEST");
            response = "HTTP/1.1 400 Bad Request Message \r\nConnection: close\r\n";
            isError = true;
        } else if(rp.getCode().equals("404")){
            System.out.println("404 Requested File Not Found");
            response = "HTTP/1.1 404 Not Found \r\nConnection: close\r\n";
            isError = true;
        } else if(rp.getCode().equals("200")){
        	response = "HTTP/1.1 200 OK \r\n";
        } else if(rp.getCode().equals("304")){
        	response = "HTTP/1.1 304 NOT MODIFIED \r\n";
        } else {
        	response = "error";
        	isError = true;
        }
        if(!isError){
        	response += mimetypeDetect(rp.getFilePathToString()) + "Content-Length: " + file.length() + "\r\nDate: " + today + "\r\nLast-Modified: " + new Date(file.lastModified()) + "\r\netag: \"" + file.lastModified() + "\"\r\n";
        }

	}
	public String mimetypeDetect(String filename){//Content-Type, Cache-Control header
        String outToClient="";
        if(filename.endsWith(".jpg")){//image
                outToClient="Content-Type: image/jpeg;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".jpeg")){//image
                outToClient="Content-Type: image/jpeg;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".bmp")){//image
                outToClient="Content-Type: image/bmp;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".gif")){//image
                outToClient="Content-Type: image/gif;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".ico")){//image
                outToClient="Content-Type: image/x-ico;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".webp")){//image
                outToClient="Content-Type: image/webm;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".svg")){//image
                 outToClient="Content-Type: image/svg+xml;\r\nCache-Control: max-age=20;\r\n";
            } else if(filename.endsWith(".pdf")){//pdf
                outToClient="Content-Type: application/pdf;\r\nCache-Control: max-age=5;\r\n";
            } else if(filename.endsWith(".ppt")){//ppt
                outToClient="Content-Type: application/ppt;\r\nCache-Control: max-age=5;\r\n";
            } else if(filename.endsWith(".xml")){//xml
                outToClient="Content-Type: application/xml;\r\nCache-Control: max-age=5;\r\n";
            } else if(filename.endsWith(".json")){//xml
                outToClient="Content-Type: application/json;\r\nCache-Control: max-age=5;\r\n";
            } else if(filename.endsWith(".wav")){//audio
                outToClient="Content-Type: audio/wav;\r\nCache-Control: max-age=5;\r\n";
            } else if(filename.endsWith(".mpeg")){
                outToClient="Content-Type: video/mpeg;\r\nCache-Control: max-age=5;\r\n";
            } else if(filename.endsWith(".css")){
                outToClient="Content-Type: text/css;charset=utf-8\r\nCache-Control: no-cache, no-store;\r\n";
            } else if(filename.endsWith(".htm")){
                outToClient="Content-Type: text/html;charset=utf-8\r\nCache-Control: no-cache, no-store\r\n";
            } else if(filename.endsWith(".html")){
                outToClient="Content-Type: text/html;charset=utf-8\r\nCache-Control: no-cache, no-store\r\n";
            } else if(filename.endsWith(".txt")){
                outToClient="Content-Type: text/plain;charset=utf-8\r\nCache-Control: max-age=10;\r\n";
            } else if(filename.endsWith(".js")){
                outToClient="Content-Type: application/js;charset=utf-8\r\nCache-Control: private, max-age=86400\r\n";
            } else if(filename.endsWith(".woff")){
                outToClient="Content-Type: application/x-font-woff;charset=utf-8\r\nCache-Control: max-age=86400\r\n";
            }
        return outToClient;
    }
}