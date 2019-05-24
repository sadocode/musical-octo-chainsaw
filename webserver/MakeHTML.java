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

public class MakeHTML{
	private ArrayList<String> files;
	private int fileCount;
	private String htmlString;
	private File html;

	public MakeHTML(ArrayList<String> f, int fc){
		this.files = f;
		this.fileCount = fc;
		this.htmlString = "";
		this.html = new File("./temp.html"); 
	}
	public File HTMLFile(){
		try{
			String html1 = "<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>POST N files</h1>\n";
			String html2 = "<div class='card'>\n<h3></h3>\n<img src='' class='playerPic'>\n</div>\n";
			String html3 = "</body>\n</html>";
			String temp = "";
			
			int index1, index2;
			htmlString += html1;
			for(int i = 0; i < fileCount; i++){
				File file = new File(files.get(i));
				index1 = html2.indexOf("<h3>");
				temp =html2.substring(0, index1 + 4) + files.get(i).substring(7);
				index2 = html2.indexOf("<img src=''");
				temp += html2.substring(index1 + 4, index2 + 10) + files.get(i) + html2.substring(index2 + 10);
				htmlString += temp;
			}
			htmlString += html3;

			FileWriter fw = new FileWriter(html, false);
			fw.write(htmlString);
			fw.flush();
	        fw.close();
	    } catch(Exception e){
	    	e.printStackTrace(System.out);
	    } finally{
        	return html;
    	}
	}
}