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

public class ServerThread extends Thread {
    private static final String webRoute = ".";
    private static final String default_path = "./index.html";
    private Socket socket;
    public byte[] smallBuffer;
    public ReadBody rb;

    public ServerThread(Socket socket){
        this.socket = socket;
        this.smallBuffer = new byte[1];
    }
    
    public void run(){
        System.out.println("Thread Created");
        InputStream inputRead = null;
        DataOutputStream outToClient = null;
        Date today = new Date();
        int postType = 1;
        int timeout = 1000;
        try {
            socket.setSoTimeout(timeout);
            inputRead = socket.getInputStream();
            outToClient = new DataOutputStream(socket.getOutputStream());
            System.out.printf("New Client Connect! Connected IP : %s, Port : %d\n", socket.getInetAddress(), socket.getPort());
            
            byte[] bigBuffer = new byte[65536];
            
            int dataSize = 0;
            int index = 0;
            int dataSizeAll = 0;
            

            //this one!
            dataSize = inputRead.read(bigBuffer, 0, 65536);
            index += dataSize;
            smallBuffer = new byte[dataSize];
            System.arraycopy(bigBuffer, 0, smallBuffer, 0, dataSize);
            

            /*
            while(inputRead.available() > 0){
                dataSize = inputRead.read(bigBuffer, index, 1000000);
                index += dataSize;
                System.out.println("dataSize : "+dataSize + " available : "+inputRead.available());
            }
            */
            //have to receive bigger than 65536 bytes
            /*
            while(true){            
                dataSize = inputRead.read(bigBuffer, 0, 65536);
                dataSizeAll += dataSize;
                //byte[] tempBuffer = new byte[dataSize];
                //System.arraycopy(bigBuffer, 0, tempBuffer, 0, dataSize);
                smallBuffer = new byte[dataSize];
                System.arraycopy(bigBuffer, 0, smallBuffer, 0, dataSize);
                if(dataSizeAll > 100000)
                    break;

            }
            */

             
            
                      
            
            HashMap<String, String> req = rq(smallBuffer);
            
            File file = new File(req.get("filename"));
            int fileLength = (int)file.length();

            //response builder
            String response ="";
            if(req.get("method").equals("405")){
                response = responseBuilder("405");
            } else if(req.get("filename").equals("400")){
                response = responseBuilder("400");
            } else if(req.get("file").equals("404")){
                response = responseBuilder("404");
            } else if(req.get("file").equals("304")){
                if(req.get("method").equals("GET"))
                    response = responseBuilder("304", "GET", req.get("filename"), fileLength, file);
                else
                    response = responseBuilder("304", "POST", req.get("filename"), fileLength, file);
            } else{
                if(req.get("method").equals("GET"))
                    response = responseBuilder("200", "GET", req.get("filename"), fileLength, file);
                else
                    response = responseBuilder("200", "POST", req.get("filename"), fileLength, file);
            }
            //response builder

            if(req.get("method").equals("GET")){ // all GET methos will be here
                outToClient.writeBytes(response);
                outToClient.writeBytes("\r\n");
                if(req.get("file").equals("200") && req.get("method").equals("GET")){
                
                    byte[] fileData = fileRead(file, fileLength);
                    outToClient.write(fileData, 0, fileLength);
                }
            } 



            






            socket.close();
        } catch(Exception e){
            e.printStackTrace(System.out);
        } finally{

        }
    }
    /**
        @param  req
        @return  HashMap<String, String> map

        request headers are in Hashmap rq
        request body read in an another method
    */
    public HashMap<String, String> rq(byte[] req) throws UnsupportedEncodingException{
        byte[] request = new byte[req.length];
        System.arraycopy(req, 0, request, 0, req.length);
        HashMap<String, String> map = new HashMap<String,String>();
        
        try {
            /*
                indexStart => check new line bit
                lineNumber => set line numbers
                newLine => if line ended, newLine becomes true
                st => for tokenizing first line of request headers
                index => temp variable for index
                boundary => store boundary.
                byteCheck => check byte number of the line
                lineByte => store bytes
                readLine => readL
            */
            int indexStart =0;
            String readLine = "";
            int lineNumber = 0;
            boolean newLine = false;
            StringTokenizer st;
            int index;
            String boundary = "";
            int byteCheck = 0;
            byte[] lineByte;
            boolean boundaryFlag = false; 

            
            for(int i = 0; i < request.length; i++){
                
                if(newLine == true){

                    //store byte[] lineByte to readLine
                    lineByte = new byte[byteCheck];
                    System.arraycopy(request, indexStart, lineByte, 0, byteCheck);
                    readLine = new String(lineByte, "utf-8");
                    //System.out.print(readLine);

                    //only for first line of request header
                    if(lineNumber == 1){
                        st = new StringTokenizer(readLine);
                        map.put("request", readLine);
                        map.put("method", st.nextToken());
                        map.put("filename", st.nextToken());
                    }

                    //for parsing request headers and store headers in Hashmap
                    if((index = readLine.indexOf(":"))!= -1){
                        String reqH1 = readLine.substring(0, index);
                        String reqH2 = readLine.substring(index+2);
                        map.put(reqH1, reqH2);
                    }

                    //when the method of request is post
                    if(readLine.contains("boundary=")){
                        index = map.get("Content-Type").indexOf("=");
                        boundary = "--" + map.get("Content-Type").substring(index +1);
                        map.put("boundary", boundary);
                        // @@@Didn't implement post method yet!!!!
                    }

                    if(readLine.equals(boundary)){
                        //lineByte = new byte[request.length - i-2 + byteCheck];
                        lineByte = new byte[request.length - indexStart];
                        System.arraycopy(request, indexStart, lineByte, 0, lineByte.length);
                        rb = new ReadBody(lineByte, map.get("boundary"));
                        //rb.write();
                        rb.parsing();
                        break;
                    }
                    //if the method is post and last boundary read -> break
                    
                    if(map.containsKey("boundary") && readLine == (map.get(boundary) + "--")){
                        break;
                    }    
                    
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
            }//for
            int l = rb.getFileCount();
            ArrayList<String> a = rb.getFiles();
            for(int i = 0; i < l; i++)
                System.out.println(a.get(i));

        } catch(Exception e){
            e.printStackTrace(System.out);
        } finally {
            //return map;
            HashMap<String, String> newMap = requestParser(map);
            return newMap;
        }        
    }

    public HashMap<String, String> requestParser(HashMap<String, String> map){
        
        HashMap<String, String> requestParser = new HashMap <String, String>();

        try{
            if(map.get("method").equals("GET")){
                requestParser.put("method", "GET");
            }else if(map.get("method").equals("POST")){
                requestParser.put("method", "POST");
            }else {
                requestParser.put("method", "405");//405 METHOD NOT ALLOWED
                return requestParser;
            }
          
            if(map.get("filename").startsWith("/")){
                if(map.get("filename").length() > 1){
                    requestParser.put("filename", map.get("filename").substring(1));
                } else {
                    requestParser.put("filename", default_path);
                }
            }
            //System.out.println("filename : " + requestParser.get("filename"));
            if(requestParser.get("filename").contains(" ")){
                requestParser.put("filename", "400");////////400 BADREQUEST
                return requestParser;
            }

            File file = new File(requestParser.get("filename"));
            if(file.exists()){
                if((map.get("If-None-Match") != null) && map.get("If-None-Match").equals("\""+ Long.toString(file.lastModified()) +"\"")){
                    requestParser.put("file","304");//304 NOT MODIFIED
                } else{
                    requestParser.put("file","200");//200 OK
                }
            } else {
                requestParser.put("file","404");//404 FILE NOT FOUND
                return requestParser;
            }
        
        } catch(NullPointerException npe){
            System.out.println("NullPointerException occured!");
        } finally{
            return requestParser;
        }

        //1. from hashmap map
        //2. method get / post /else-> 405
        //3. filename  else->400
        //4. file cache remain->304 else 200
        //5. file not found ->404 
    }

/**
*   @param dataOfFile   data of image
    @param fileName     filename of image
    @param filetype     type of image (png, jpg, ico)
    @return             image File
*/
    public File makeImageFile(String dataOfFile, String fileName, String filetype) throws IOException{
        String filePath = "./temp/" + fileName + "." + filetype;
        System.out.println("File Receivied!! : " + filePath);
        File newFile = new File(filePath);

        
        byte[] targetBytes = dataOfFile.getBytes();
        //store the file after decode to utf8 
        Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(targetBytes);

        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(decodedBytes);
        fos.flush();
        fos.close();
        /*
        FileWriter fw = new FileWriter(newFile, false);
        fw.write(dataOfFile);
        fw.flush();
        fw.close();
        */
        return newFile; 
    }
    /**
        @param dataOfFile   data of file
        @param fileName     name of file
        @throws IOException
        @return             file
    */
    public File makeFile(String dataOfFile, String fileName) throws IOException{//first parameter byte? String?
        System.out.println("dataOfFile : " + dataOfFile);
        String filePath = "./temp/" + fileName;
        System.out.println("filePath : " + filePath);
        File newFile = new File(filePath);
        FileWriter fw = new FileWriter(newFile, false);
        //String fileContent = dataOfFile.toString();
        //fw.write(fileContent);
        
        fw.write(dataOfFile);
        fw.flush();
        fw.close();
        System.out.println("bye");
        return newFile;
    }
    /**
        when post request arrived, server makes temp.html

        @param map  informations of post files
        @return     temp.html
    */
    public File makeHTML(HashMap<String, String> map) throws IOException{
        String name = map.get("name");
        String url = map.get("url");
        File html = new File("./temp.html");
        FileWriter fw = new FileWriter(html, false);
        if(map.get("postType").equals("1"))
            fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>New</h1>\n<div class='card'>\n<h3>" + name + "</h3>\n<img src=" + url + " alt=" + name + " class='playerPic'>\n</div>\n</body>\n</html>");
    
        else if(map.get("postType").equals("2")){
            String file1 = "./temp/" + map.get("filename1");
            String file2 = "./temp/" + map.get("filename2");

            fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>POST N files</h1>\n<div class='card'>\n<h3>" + map.get("filename1") + "</h3>\n<img src='" + file1 + "' class='playerPic'>\n</div><div class='card'>\n<h3>" + map.get("filename2") + "</h3>\n<img src='" + file2 + "' class='playerPic'>\n</div>\n</body>\n</html>");
        }

        fw.flush();
        fw.close();
        return html;
    }

    public byte[] fileRead(File file, int fileLength) throws IOException{
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } catch(Exception e){
            System.out.println("ERRRRR");
        }
        finally {
            if (fileIn != null) 
                fileIn.close();
        }
        return fileData;
    }

    public String responseBuilder(String request){
        String response = "";
        if(request.equals("405")){
            System.out.println("405 Method Not Allowed");
            response = "HTTP/1.1 405 Method Not Allowed \r\nConnection: close\r\n";
        } else if(request.equals("400")){
            System.out.println("400 BAD REQUEST");
            response = "HTTP/1.1 400 Bad Request Message \r\nConnection: close\r\n";
        } else if(request.equals("404")){
            System.out.println("404 Requested File Not Found");
            response = "HTTP/1.1 404 Not Found \r\nConnection: close\r\n";
        } else {
            response = "error";
        }

        return response; 
    }
    public String responseBuilder(String request, String reqmethod, String filename, int fileLength, File file){
        Date today = new Date();
        String response = "";
        File refile = file;

        if(request.equals("200")){
            response = "HTTP/1.1 200 OK \r\n";
        } else{
            response = "HTTP/1.1 304 NOT MODIFIED \r\n";
        }

        response += mimetypeDetect(filename) + "Content-Length: " + fileLength + "\r\nDate: " + today + "\r\nLast-Modified: " + new Date(refile.lastModified()) + "\r\netag: \"" + refile.lastModified() + "\"\r\n";

        if(reqmethod.equals("POST")){
       
            
        }
        return response;
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
/*
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

public class ServerThread extends Thread {
    private static final String webRoute = ".";
    private static final String default_path = "./index.html";
    private Socket socket;
    public ServerThread(Socket socket){
        this.socket = socket;
    }
    
    public void run(){
        System.out.println("Thread Created");
        BufferedReader inFromClient = null;
        InputStream inputRead1 = null;
        InputStreamReader inputRead2 = null;
        DataOutputStream outToClient = null;
        Date today = new Date();
        int postType = 1;
        try {
            inputRead1 = socket.getInputStream();
            inputRead2 = new InputStreamReader(inputRead1);
            inFromClient = new BufferedReader(inputRead2);
            //inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outToClient = new DataOutputStream(socket.getOutputStream());
            System.out.printf("New Client Connect! Connected IP : %s, Port : %d\n", socket.getInetAddress(), socket.getPort());
            
            
            //for storing request headers
            String requestFirst = inFromClient.readLine();
            StringTokenizer firstLineToken = new StringTokenizer(requestFirst);
            HashMap<String, String> map = new HashMap <String, String>();
            map.put("request", requestFirst);
            map.put("method", firstLineToken.nextToken());
            map.put("filename", firstLineToken.nextToken());
            int index;
            String requestHeaderLine;
            for(int i = 0; i < 20; i++){
                requestHeaderLine = inFromClient.readLine();
                if((index = requestHeaderLine.indexOf(":"))!= -1){
                    String reqH1 = requestHeaderLine.substring(0, index);
                    String reqH2 = requestHeaderLine.substring(index+2);
                    map.put(reqH1, reqH2);
                } else {
                        break;
                }
            }
             //for storing request headers to hashmap map


            HashMap<String, String> req = new HashMap <String, String>(requestParser(map));
            File file = new File(req.get("filename"));
            int fileLength = (int)file.length();

            //response builder
            String response ="";
            if(req.get("method").equals("405")){
                response = responseBuilder("405");
            } else if(req.get("filename").equals("400")){
                response = responseBuilder("400");
            } else if(req.get("file").equals("404")){
                response = responseBuilder("404");
            } else if(req.get("file").equals("304")){
                if(req.get("method").equals("GET"))
                    response = responseBuilder("304", "GET", req.get("filename"), fileLength, file);
                else
                    response = responseBuilder("304", "POST", req.get("filename"), fileLength, file);
            } else{
                if(req.get("method").equals("GET"))
                    response = responseBuilder("200", "GET", req.get("filename"), fileLength, file);
                else
                    response = responseBuilder("200", "POST", req.get("filename"), fileLength, file);
            }
            //response builder
            if(!req.get("method").equals("POST")){ // all GET methos will be here
                outToClient.writeBytes(response);
                outToClient.writeBytes("\r\n");
                if(req.get("file").equals("200") && req.get("method").equals("GET")){
                
                    byte[] fileData = fileRead(file, fileLength);
                    outToClient.write(fileData, 0, fileLength);
                }
            }
            

            //<For POST Request>
            String readBody="";
            String filename ="";
            String filetype = "";
            String fileContent ="";
            int numberflag = 0;
            int fileflag = 0;

            char smallBuff;
            int dataOfCharacter;
            HashMap<String, String> bodyMap = new HashMap <String, String>();
            if(req.get("method").equals("POST") && req.get("file").equals("200")){
                System.out.println("@@");    
                if(map.get("Content-Type").contains("boundary=")){//for Test post many files. But only for text files yet
                    index = map.get("Content-Type").indexOf("=");
                    String boundary = map.get("Content-Type").substring(index+1);//for store boundary    
                    bodyMap.put("postType", "2");
                    bodyMap.put("boundary", "--" + boundary);
                    System.out.println("##");

                    
                    //For Test4
                    //byte[] bigBuff = new byte[2048];
                    //int dataSize = 0;
                    //int len = -1;

                    //while((len = inputRead1.read()) > 0){
                        //bigBuff[dataSize] = (byte)len;
                        //dataSize++;
                        //System.out.println("&&");
                        //System.out.println(bigBuff.toString());
                    //}
                    //System.out.println("**");
                    //System.out.println(bigBuff.toString());
                    
                    
                    
                    //For Test3
                    //while((dataOfCharacter=inputRead1.read()) != -1){
                        //smallBuff = (char)dataOfCharacter;
                        //System.out.print(smallBuff);
                    //}
                    



                    //For Test2
                    //while((dataOfCharacter=inFromClient.read()) != -1){
                        //smallBuff = (char)dataOfCharacter;
                        //System.out.print(smallBuff);
                    //}
                    

                    //(v1.1.0)
                    while((readBody = inFromClient.readLine())!= null){
                        System.out.println(readBody);
                        if(readBody.contains(bodyMap.get("boundary"))){
                            if(fileflag != 0){
                                //recieve file
                                if(filename.contains("filetext")){
                                    System.out.println("wh??");
                                    makeImageFile(fileContent, filename, filetype);
                                
                                    bodyMap.put("filename" + numberflag, filename + "." + filetype);
                                    bodyMap.put("fileContent" + numberflag, fileContent);
                                }
                                fileContent ="";
                            }
                            fileflag++; //inform start of file
                        }
                        
                        if(readBody.contains("Content-Type: image/")){
                            fileflag = 0; // do not receive real image file. we only receive encoded image files 
                        }

                        if((index = readBody.indexOf("name=")) != -1){ // for store name of the file
                            int endIndex = (int)readBody.length();
                            filename = readBody.substring(index + 6, endIndex -1);
                        }

                        //detect base64. receive
                        if(readBody.contains("data:image") && readBody.contains(";base64,")){
                            index = 11;
                            if(readBody.substring(index, index + 10).contains("png")){
                                filetype = "png";
                            } else if(readBody.substring(index, index + 10).contains("jpg") || readBody.substring(index, index + 10).contains("jpeg")){
                                filetype = "jpeg";
                            } else  if(readBody.substring(index, index + 10).contains("x-ico")){
                                filetype = "ico";
                            } else {
                                return; //didnt implement all image types
                            }
                            index = readBody.indexOf(",");
                            fileContent += readBody.substring(index + 1);
                            numberflag++;
                        }
                        if(readBody.contains("--" + boundary + "--")){
                            bodyMap.put("postType", "2");
                            File newHtml = makeHTML(bodyMap);
                            int fileL = (int)newHtml.length();
                            byte[] fileData = fileRead(newHtml, fileL);

                            outToClient.writeBytes("HTTP/1.1 200 OK\r\nContent-Length: "+ newHtml.length()+"\r\nDate: "+today+"\r\n"+mimetypeDetect(".html")+"\r\n");   //filetype -> ".html" change
                            
                            outToClient.write(fileData, 0, fileL);

                            break;
                        }      
                    }
                }
            }
            



            outToClient.flush();

            //for printing request headers to commandLine
            Iterator<String> keySetIterator = map.keySet().iterator();
            while(keySetIterator.hasNext()){
                String key = keySetIterator.next();
                System.out.println(key + ": " + map.get(key));
            }
            System.out.println("----------------------------");
            //for printing request headers to commandLine

            socket.close();
        } catch(Exception e){
            System.out.println("EXCEP");
        } finally{

        }
    }


    //filetype means png, jpg, ico ..
    public File makeImageFile(String dataOfFile, String fileName, String filetype) throws IOException{
        String filePath = "./temp/" + fileName + "." + filetype;
        System.out.println("File Receivied!! : " + filePath);
        File newFile = new File(filePath);

        
        byte[] targetBytes = dataOfFile.getBytes();
        //store the file after decode to utf8 
        Decoder decoder = Base64.getDecoder();
        byte[] decodedBytes = decoder.decode(targetBytes);

        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(decodedBytes);
        fos.flush();
        fos.close();
        
        //FileWriter fw = new FileWriter(newFile, false);
        //fw.write(dataOfFile);
        //fw.flush();
        //fw.close();
        
        return newFile; 
    }
    public File makeFile(String dataOfFile, String fileName) throws IOException{//first parameter byte? String?
        System.out.println("dataOfFile : " + dataOfFile);
        String filePath = "./temp/" + fileName;
        System.out.println("filePath : " + filePath);
        File newFile = new File(filePath);
        FileWriter fw = new FileWriter(newFile, false);
        //String fileContent = dataOfFile.toString();
        //fw.write(fileContent);
        
        fw.write(dataOfFile);
        fw.flush();
        fw.close();
        System.out.println("bye");
        return newFile;
    }
    public File makeHTML(HashMap<String, String> map) throws IOException{
        String name = map.get("name");
        String url = map.get("url");
        File html = new File("./temp.html");
        FileWriter fw = new FileWriter(html, false);
        if(map.get("postType").equals("1"))
            fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>New</h1>\n<div class='card'>\n<h3>" + name + "</h3>\n<img src=" + url + " alt=" + name + " class='playerPic'>\n</div>\n</body>\n</html>");
    
        else if(map.get("postType").equals("2")){
            String file1 = "./temp/" + map.get("filename1");
            String file2 = "./temp/" + map.get("filename2");

            fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>POST N files</h1>\n<div class='card'>\n<h3>" + map.get("filename1") + "</h3>\n<img src='" + file1 + "' class='playerPic'>\n</div><div class='card'>\n<h3>" + map.get("filename2") + "</h3>\n<img src='" + file2 + "' class='playerPic'>\n</div>\n</body>\n</html>");
        }

        fw.flush();
        fw.close();
        return html;
    }
    public byte[] fileRead(File file, int fileLength) throws IOException{
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
        
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } catch(Exception e){
            System.out.println("ERRRRR");
        }
        finally {
            if (fileIn != null) 
                fileIn.close();
        }
        return fileData;
    }
    
    public HashMap<String, String> requestParser(HashMap<String, String> map){
        
        HashMap<String, String> requestParser = new HashMap <String, String>();

        try{
            if(map.get("method").equals("GET")){
                requestParser.put("method", "GET");
            }else if(map.get("method").equals("POST")){
                requestParser.put("method", "POST");
            }else {
                requestParser.put("method", "405");//405 METHOD NOT ALLOWED
                return requestParser;
            }
          
            if(map.get("filename").startsWith("/")){
                if(map.get("filename").length() > 1){
                    requestParser.put("filename", map.get("filename").substring(1));
                } else {
                    requestParser.put("filename", default_path);
                }
            }
            System.out.println("filename : " + requestParser.get("filename"));
            if(requestParser.get("filename").contains(" ")){
                requestParser.put("filename", "400");////////400 BADREQUEST
                return requestParser;
            }

            File file = new File(requestParser.get("filename"));
            if(file.exists()){
                if((map.get("If-None-Match") != null) && map.get("If-None-Match").equals("\""+ Long.toString(file.lastModified()) +"\"")){
                    requestParser.put("file","304");//304 NOT MODIFIED
                } else{
                    requestParser.put("file","200");//200 OK
                }
            } else {
                requestParser.put("file","404");//404 FILE NOT FOUND
                return requestParser;
            }
        
        } catch(NullPointerException npe){
            System.out.println("NullPointerException occured!");
        } finally{
            return requestParser;
        }

        //1. from hashmap map
        //2. method get / post /else-> 405
        //3. filename  else->400
        //4. file cache remain->304 else 200
        //5. file not found ->404 
    }
    public String responseBuilder(String request){
        String response = "";
        if(request.equals("405")){
            System.out.println("405 Method Not Allowed");
            response = "HTTP/1.1 405 Method Not Allowed \r\nConnection: close\r\n";
        } else if(request.equals("400")){
            System.out.println("400 BAD REQUEST");
            response = "HTTP/1.1 400 Bad Request Message \r\nConnection: close\r\n";
        } else if(request.equals("404")){
            System.out.println("404 Requested File Not Found");
            response = "HTTP/1.1 404 Not Found \r\nConnection: close\r\n";
        } else {
            response = "error";
        }

        return response; 
    }
    public String responseBuilder(String request, String reqmethod, String filename, int fileLength, File file){
        Date today = new Date();
        String response = "";
        File refile = file;

        if(request.equals("200")){
            response = "HTTP/1.1 200 OK \r\n";
        } else{
            response = "HTTP/1.1 304 NOT MODIFIED \r\n";
        }

        response += mimetypeDetect(filename) + "Content-Length: " + fileLength + "\r\nDate: " + today + "\r\nLast-Modified: " + new Date(refile.lastModified()) + "\r\netag: \"" + refile.lastModified() + "\"\r\n";

        if(reqmethod.equals("POST")){
       
            
        }
        return response;
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
*/