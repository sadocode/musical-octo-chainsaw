package webserver;
import java.io.File;
import java.io.IOException;
import java.lang.String;
import java.util.*;
import java.util.HashMap;
import java.util.Date;

public class RequestParser {
	private byte[] request;
	public int byteLength;
	public String requestMethod = null;
	public String requestURL = "./index.html"; 
	
	public RequestParser(byte[] request, int length){
		this.request = request;
		this.byteLength = length;
	}
/* 
	public void readRequest(){
		try{
			String requestToString = new String(request, "utf-8");
			System.out.println("<<<Recevied Request>>>\n" + requestToString);

			
			int indexStart = 0;	int indexEnd = 0; String readLine = "";
			for(int i = 0; i < byteLength; i++){
				readLine += new String(request[i]);

				//if i~i+3 :  \r\n then indexStart and indexEnd change. 
				if(request[i] == '0x5C' && request[i+1] =='0x72' && request[i+2] == '0x5C' && request[i+3] == '0x6E'){					
					
					indexEnd = i + 3;
					readLine += new String(request[i+1]);
					readLine += new String(request[i+2]);
					readLine += new String(request[i+3]);

					if(i + 4 == byteLength){ //finish of the request!!
						
						break;
					} else {//only line finished not all of the request
						indexStart = i + 4;
						i += 3;
					} 
				}
			}
		} catch(Exception e){
			System.out.println("Exception : RequestParser readRequest");
		}	
	}
*/
}
