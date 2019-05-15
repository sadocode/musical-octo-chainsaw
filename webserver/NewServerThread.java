package webserver;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.util.*;
import java.lang.String;

public class NewServerThread extends Thread{
	private Socket socket;
	public InputStream is = null;
    public DataOutputStream dos = null;

    public NewServerThread(Socket socket){
        this.socket = socket;
        
        try{
        	this.is = socket.getInputStream();
        	this.dos = new DataOutputStream(socket.getOutputStream());
    	} catch(Exception e){
    		System.out.println("Exception : NewServerThread Constructor");
    	}
    }

    public void run(){
        System.out.println("Thread Created");

        try{
        	System.out.printf("New Client Connect! Connected IP : %s, Port : %d\n", socket.getInetAddress(), socket.getPort());

        	byte[] bigBuffer = new byte[20000];
        	int len = -1;
        	int dataSize = 0;
        	

        	//read request
        	while((len = is.read()) >= 0){
        		bigBuffer[dataSize++] = (byte)len;
        	}

        	//sent to RequestParser class
        	byte[] smallBuffer = new byte[dataSize];
        	for(int i = 0; i < dataSize; i++){
        		smallBuffer[i] = bigBuffer[i];
        	}
        	
        	//String request = smallBuffer.toString();
        	RequestParser rp = new RequestParser(smallBuffer, dataSize);
        	//rp.readRequest();
            
        	//recive response from ResponseBuilder class



        	socket.close();
        } catch(Exception e) {
        	System.out.println("Exception is occured!");
        } finally {

        }
	}
}
