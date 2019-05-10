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
        DataOutputStream outToClient = null;
        Date today = new Date();
        int postType = 1;
        try {
            inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
                /*
                FileInputStream fis = new FileInputStream(file);
                byte[] fileData = new byte[fileLength];
                fis.read(fileData);
                outToClient.write(fileData, 0, fileLength);
                */
                    byte[] fileData = fileRead(file, fileLength);
                    outToClient.write(fileData, 0, fileLength);
                }
            }

            //For POST method
            else{
                String readBody="";
                HashMap<String, String> bodyMap = new HashMap <String, String>();
                if(req.get("method").equals("POST") && req.get("file").equals("200")){
                    
                    if(map.get("Content-Type").contains("boundary=")){//for Test post many files. But only for text files yet
                        index = map.get("Content-Type").indexOf("=");
                        String boundary = map.get("Content-Type").substring(index+1);//for store boundary    
                        bodyMap.put("postType", "2");
                        bodyMap.put("boundary", "--" + boundary);
                        //System.out.println(boundary);
                        
                        int fileflag = 0; // distinguish file is new or elder
                        int lineflag = 3; // to read file start line
                        
                        index = 0;
                        String fileContent = ""; // for read file content
                        String filetype = ""; // for distinguish file mime type
                        

                        while((readBody = inFromClient.readLine())!= null){
                            
                            if(readBody.contains("--" + boundary)){//when boundary read, new file is starting.
                                if(fileflag != 0){
                                    //text file
                                    if(filetype.endsWith("txt")){
                                        makeFile(fileContent, filetype);
                                        bodyMap.put("fileContent"+fileflag, fileContent);
                                    }
                                    else
                                        makeImageFile(fileContent, filetype);//for store image files

                                    bodyMap.put("filename" + fileflag, filetype); //for store name of textfile
                                }
                                fileflag++;
                                lineflag = 3; // to read file start line
                                fileContent = "";
                            }

                            if((index = readBody.indexOf("filename=")) != -1){
                                int endIndex = (int)readBody.length();
                                filetype = readBody.substring(index + 10, endIndex - 1);
                                

                                //System.out.println("@@@ " + filetype);
                                //temp = mimetypeDetect(filetype); //not using..
                            } 
                            if(lineflag <= 0){
                                fileContent += readBody;
                            }

                            lineflag--;
                                


                            if(readBody.contains("--" + boundary + "--")){
                                bodyMap.put("postType", "2");
                                File newHtml = makeHTML(bodyMap, bodyMap.get("filename1"), bodyMap.get("filename2"), bodyMap.get("filename3"));
                                int fileL = (int)newHtml.length();
                                byte[] fileData = fileRead(newHtml, fileL);

                                outToClient.writeBytes("HTTP/1.1 200 OK\r\nContent-Length: "+ newHtml.length()+"\r\nDate: "+today+"\r\n"+mimetypeDetect(".html")+"\r\n");   //filetype -> ".html" change
                                
                                outToClient.write(fileData, 0, fileL);

                                break;
                            }      

                            
                            System.out.println(readBody);
                        }
                    }
                    else{//henry
                        for(int i = 0; i < 2; i++){
                            readBody = inFromClient.readLine();
                            if((index = readBody.indexOf("="))!= -1){
                                String reqH1 = readBody.substring(0, index);
                                String reqH2 = readBody.substring(index+1);
                                bodyMap.put(reqH1, reqH2);
                            } 
                        }
                        bodyMap.put("postType", "1"); // henry
                        File newHtml = makeHTML(bodyMap);
                        int fileL = (int)newHtml.length();
                        byte[] fileData = fileRead(newHtml, fileL);

                        outToClient.writeBytes("HTTP/1.1 200 OK\r\nContent-Length: "+ newHtml.length()+"\r\nDate: "+today+"\r\n"+mimetypeDetect(bodyMap.get("url"))+"\r\n");
                        System.out.println(bodyMap.get("url"));
                        outToClient.write(fileData, 0, fileL);
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
    public File makeImageFile(String dataOfFile, String fileName) throws IOException{
        String filePath = "./temp/" + fileName;
        System.out.println("filePath : " + filePath);
        File newFile = new File(filePath);
        

        byte[] targetBytes = dataOfFile.getBytes();
        Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode(targetBytes);


        FileOutputStream fos = new FileOutputStream(filePath);
        fos.write(encodedBytes);

        fos.flush();
        fos.close();
        /*
        String writeString = encodedBytes.toString();
        System.out.println(writeString);
        FileWriter fw = new FileWriter(newFile, false);
        fw.write(dataOfFile);
        fw.flush();
        fw.close();
        */
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
        /*
        else if(map.get("postType").equals("2")){
            fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>POST N files</h1>\n<div class='card'>\n<h3>" + map.get("filename1") + "</h3>\n<p>" + map.get("fileContent1") + "</p>\n</div><div class='card'>\n<h3>" + map.get("filename2") + "</h3>\n<p>" + map.get("fileContent2") + "</p>\n</div><div class='card'>\n<h3>" + map.get("filename3") + "</h3>\n<p>" + map.get("fileContent3") + "</p>\n</div>\n</body>\n</html>");
        }
        */
        else if(map.get("postType").equals("2")){
            File file1 = new File("./temp/" + map.get("filename1"));
            File file2 = new File("./temp/" + map.get("filename2"));
            File file3 = new File("./temp/" + map.get("filename3"));

            fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>POST N files</h1>\n<div class='card'>\n<h3>" + map.get("filename1") + "</h3>\n<p>" + map.get("fileContent1") + "</p>\n</div><div class='card'>\n<h3>" + map.get("filename2") + "</h3>\n<p>" + map.get("fileContent2") + "</p>\n</div><div class='card'>\n<h3>" + map.get("filename3") + "</h3>\n<p>" + map.get("fileContent3") + "</p>\n</div>\n</body>\n</html>");
        }

        fw.flush();
        fw.close();
        return html;
    }
    public File makeHTML(HashMap<String, String> map, String filename1, String filename2, String filename3) throws IOException{
        File file1 = new File("./temp/" + filename1);
        File file2 = new File("./temp/" + filename2);
        File file3 = new File("./temp/" + filename3);
        File html = new File("./temp.html");
        FileWriter fw = new FileWriter(html, false);

        fw.write("<html>\n<head>\n<link rel='stylesheet' href='./css/styles.css' type='text/css'>\n</head>\n<body>\n<h1>POST N files</h1>\n<div class='card'>\n<h3>" + map.get("filename1") + "</h3>\n<p>" + map.get("fileContent1") + "</p>\n</div><div class='card'>\n<h3>" + map.get("filename2") + "</h3>\n<p>" + map.get("fileContent2") + "</p>\n</div><div class='card'>\n<h3>" + map.get("filename3") + "</h3>\n<p>" + map.get("fileContent3") + "</p>\n</div>\n</body>\n</html>");

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

   

    //not using..
    public String imageType(String content){
        String[] imageTypes = {"jpg", "jpeg", "bmp", "gif", "png", "ico", "svg", "webp"};
        String result="";
        for(int i = 0; i < imageTypes.length; i++){
            if(content.endsWith(imageTypes[i])){
                result = imageTypes[i];
                return result;
            }
        }
        
        return "notImageFile";
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
