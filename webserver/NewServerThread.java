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
import java.io.BufferedInputStream;

public class NewServerThread extends Thread{
    private static final String default_path = "./index.html";
    private Socket socket;
    public byte[] smallBuffer;
    public ReadBody rb;

    public NewServerThread(Socket socket){
        this.socket = socket;
        this.smallBuffer = new byte[1];
    }
    public void run(){
        System.out.println("Thread Created");
        InputStream inputRead = null;
        DataOutputStream outToClient = null;
        Date today = new Date();
        int postType = 1;
        int timeout = 100;
        byte[] bigBuffer = new byte[65536];

        try{
            //socket.setSoTimeout(timeout);
            
            inputRead = socket.getInputStream();
            outToClient = new DataOutputStream(socket.getOutputStream());
            System.out.printf("New Client Connect! Connected IP : %s, Port : %d\n", socket.getInetAddress(), socket.getPort());

            int dataSize = 0;
            int index = 0;
            int dataSizeAll = 0;
            int readCount = 0;
            
            
            while((dataSize = inputRead.read(bigBuffer, 0, 65536)) > 0){
                
                if(readCount == 0 && dataSize < 65536 && dataSize > 0){
                    smallBuffer = new byte[dataSize];
                    System.arraycopy(bigBuffer, 0, smallBuffer, 0, dataSize);
                    break;
                } else if (readCount != 0){
                    byte[] tempBuffer = new byte[smallBuffer.length];
                    System.arraycopy(smallBuffer, 0, tempBuffer, 0, smallBuffer.length);
                    smallBuffer = new byte[tempBuffer.length + dataSize];
                    System.arraycopy(tempBuffer, 0, smallBuffer, 0, tempBuffer.length);
                    System.arraycopy(bigBuffer, 0, smallBuffer, tempBuffer.length, dataSize);
                }
                readCount++;
                dataSizeAll += dataSize;
                

            }
            
            
            /*
            dataSize = inputRead.read(bigBuffer, 0, 65536);
            smallBuffer = new byte[dataSize];
            System.arraycopy(bigBuffer, 0, smallBuffer, 0, dataSize);
            */
            

            ByteProcessing bp = new ByteProcessing(smallBuffer);
            bp.processBytes();

            ReadBody rb;
            ArrayList<String> files = new ArrayList<String>();
            int fileCount = 0;
            if(bp.getMethod().equals("POST")){
                rb = bp.getReadBody();
                rb.parsing();
                rb.write();
                files = rb.getFiles();
                fileCount = rb.getFileCount();
            }

            RequestParser rp = new RequestParser(bp.getRequestHeaders());
            rp.setBPVariables(bp.getMethod(), bp.getFilePath());
            rp.parsing();            

            ResponseBuilder responsebuilder = new ResponseBuilder(rp);
            responsebuilder.build();
            String response = responsebuilder.getResponse();        
            
            File file = new File(rp.getFilePathToString());
            FileRead fr = new FileRead(file);

            if(rp.getMethod().equals("GET")){
                outToClient.writeBytes(response);
                outToClient.writeBytes("\r\n");
                if(rp.getCode().equals("200")){
                    fr.fread();
                    outToClient.write(fr.getFileData(), 0, fr.getFileLength());
                }
            } else if(rp.getMethod().equals("POST")){
                MakeHTML mhtml = new MakeHTML(files, fileCount);
                File html = mhtml.HTMLFile();
                outToClient.writeBytes(response);
                outToClient.writeBytes("\r\n");
                fr = new FileRead(html);
                fr.fread();
                outToClient.write(fr.getFileData(), 0, fr.getFileLength());
                System.out.println("@@@html.length:" + html.length());
            }




            socket.close();
        } catch(Exception e){
            e.printStackTrace(System.out);
        }
    }
}