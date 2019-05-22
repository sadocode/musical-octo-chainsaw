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


public class ReadBody {
	private byte[] postBody;
	private String boundary;
	private int fileCount;
	private ArrayList<String> files;
	public static String default_path = "./temp/";

	public ReadBody(byte[] pB, String boundary){
		this.files = new ArrayList<String>();
		this.boundary = boundary;
		this.fileCount = 0;
		this.postBody = new byte[pB.length];
		System.arraycopy(pB, 0, this.postBody, 0, pB.length);
	}

	/**
		@return fileCount
	*/
	public int getFileCount(){
		return this.fileCount;
	}

	/**
		@return ArrayList<String> files
	*/
	public ArrayList<String> getFiles(){
		return this.files;
	}

	public void parsing() throws UnsupportedEncodingException{
		byte[] lineByte;
		String readLine = "";
		int index;
		boolean newLine = false;
		int byteCheck = 0;
		int indexStart = 0;
		String filetype = "";
		String filename = "";
		int fileFlag = -100;
		byte[] fileByte = new byte[1];
		MakeFile mf;
		boolean newFile = false;

		try{
			for(int i = 0; i < postBody.length; i++){
				if(newLine == true){
					lineByte = new byte[byteCheck];
					System.arraycopy(postBody, indexStart, lineByte, 0, byteCheck);
					readLine = new String(lineByte, "utf-8");

					//0. check filename, filetype
					if(readLine.contains("Content-Disposition: form-data;") && readLine.contains("filename")){
						int endIndex = (int)readLine.length();
						index = readLine.indexOf("filename=");
						filename = readLine.substring(index+10, endIndex - 3);					
						index = filename.lastIndexOf(".");
						filetype = filename.substring(index + 1);			
						fileFlag = 0;
					}

					//1. file Start. it is first line of file
					if(fileFlag == 3 && !readLine.contains(boundary)){
						fileByte = new byte[lineByte.length];
						System.arraycopy(lineByte, 0, fileByte, 0, lineByte.length);
						files.add(fileCount, default_path + filename); 
						fileCount++;
						newFile = true;
					}

					//2. it is about file contents.
					if(fileFlag > 3 && !readLine.contains(boundary)){
						int length = fileByte.length + lineByte.length;
						byte[] tempByte = new byte[fileByte.length];
						System.arraycopy(fileByte, 0, tempByte, 0, fileByte.length);//copy. fileByte -> tempByte
						fileByte = new byte[length];
						System.arraycopy(tempByte, 0, fileByte, 0, tempByte.length);//copy. tempByte -> fileByte
						System.arraycopy(lineByte, 0, fileByte, tempByte.length, lineByte.length);//append. fileByte <- lineByte
						
					}

					//3. when file finished, make file
					if(readLine.contains(boundary.substring(0, boundary.length()-2)) && newFile){
						mf = new MakeFile(fileByte, filename, filetype);
						mf.newFile();
						newFile = false;
					}

					//4. read body finished
					if(i+1 == postBody.length){
						System.out.println("End of ReadBody.parsing()");
						break;
					}

					//fileFlag : if fileflag >= 3, read file
					//byteCheck : check the byte number of readLine
					//indexStart : start index of readLine
					fileFlag++;
					byteCheck = 0;
					indexStart = i;
				}

				//newLine : if \r\n found, newLine become true
				byteCheck++;
				newLine = false;

				//check newLine began
				if(postBody[i] == 13 && postBody[i+1] == 10){
					byteCheck++;
					if(i+2 != postBody.length){
						i++;
					}
					newLine = true;
				}
			}
		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	public void write() throws UnsupportedEncodingException{
		System.out.println("===Start===");
		System.out.println(new String(postBody, "utf-8"));
		System.out.println("===End===length:"+postBody.length);
	}
}