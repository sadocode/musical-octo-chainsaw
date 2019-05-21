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

public class ReadBody {
	private byte[] postBody;
	private String boundary;
	private File[] files;
	private int fileCount;

	public ReadBody(byte[] pB, String boundary){
		this.boundary = boundary;
		this.fileCount = 0;
		this.postBody = new byte[pB.length];
		System.arraycopy(pB, 0, this.postBody, 0, pB.length);
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
			for(int i = 0; i < this.postBody.length; i++){
				if(newLine == true){
					lineByte = new byte[byteCheck];
					System.arraycopy(this.postBody, indexStart, lineByte, 0, byteCheck);
					readLine = new String(lineByte, "utf-8");
					System.out.print(readLine);

					if(readLine.contains("Content-Disposition: form-data;") && readLine.contains("filename")){
						int endIndex = (int)readLine.length();
						index = readLine.indexOf("filename=");
						filename = readLine.substring(index+10, endIndex - 3);					
						index = filename.lastIndexOf(".");
						filetype = filename.substring(index + 1);			
						//System.out.println("filename :"+filename +" filetype :" + filetype);
						
						fileFlag = 0;
					}

					//1. file Start. it is first line of file
					if(fileFlag == 3 && !readLine.contains(boundary)){
						fileByte = new byte[lineByte.length];
						System.arraycopy(lineByte, 0, fileByte, 0, lineByte.length);
						//mf = new MakeFile(lineByte, filename, filetype);
						//this.files[this.fileCount++] = mf.detectFileType();
						this.fileCount++;
						newFile = true;
					}

					//2. it is about file contents.
					if(fileFlag > 3 && !readLine.contains(boundary)){
						int length = fileByte.length + lineByte.length;
						byte[] tempByte = new byte[fileByte.length];
						System.arraycopy(fileByte, 0, tempByte, 0, fileByte.length);
						fileByte = new byte[length];
						System.arraycopy(tempByte, 0, fileByte, 0, tempByte.length);
						System.arraycopy(lineByte, 0, fileByte, tempByte.length, length);
					}
					if(readLine.equals(boundary) && newFile){
						System.out.println("filename : " + filename + "fileCount : "+ this.fileCount);
						mf = new MakeFile(fileByte, filename, filetype);
						this.files[this.fileCount] = mf.detectFileType();
						newFile = false;
					}

					if(readLine.contains(boundary+"--")){
						System.out.println("@#@#!#");
						break;
					}

					fileFlag++;
					byteCheck = 0;
					indexStart = i;
				}

				byteCheck++;
				newLine = false;

				if(this.postBody[i] == 13 && this.postBody[i+1] == 10){
					byteCheck++;
					if(i + 1 == this.postBody.length)
						break;
					else{
						i++;
						newLine = true;
					}
				}
			}

		} catch (Exception e){
			e.printStackTrace(System.out);
		}
	}
	public void write() throws UnsupportedEncodingException{
		System.out.println("===Start===");
		System.out.println(new String(this.postBody, "utf-8"));
		System.out.println("===End===");
		System.out.println(this.postBody.length);
	}
}
