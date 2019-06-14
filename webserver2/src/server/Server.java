package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;

public class Server
{
	private Properties info;
	private int port;
	private String webRoot;
	private String home;
	private ServerSocket serverSocket;
	
    public void setEnvironment() 
    {
    	try(FileInputStream fis = new FileInputStream("info.properties")) {
    		this.info = new Properties();
    		this.info.load(fis);
    		this.port = Integer.parseInt(info.getProperty("PORT"));
    		this.webRoot = info.getProperty("WEBROOT");
    		this.home = info.getProperty("HOME");
    	} catch(IOException ioe) {
    		ioe.printStackTrace(System.out);
    	}
    }
    public void setServerSocket()
    {
    	try {
    		this.serverSocket = new ServerSocket(this.port);
    		System.out.println("WebServer Socket Created. PortNumber : " + this.port);
    		
    	} catch(IOException ioe) {
    		ioe.printStackTrace(System.out);
    	}
    }
    
	public static void main(String args[]) throws Exception
    {
    	Server server = new Server();
    	server.setEnvironment();
    	
    	server.setServerSocket();
      
        Socket socket = null;
        NewServerThread serverThread = null;
        List<Thread> list = new LinkedList<>();
        
        try 
        {
        	while(!Thread.currentThread().isInterrupted() && (socket = server.serverSocket.accept()) != null)
        	{
        		serverThread = new NewServerThread(socket, server.webRoot, server.home);
        		serverThread.start();
        		list.add(serverThread);
        		if(list.size() >= 300)
        			break;
        	}
        } 
        catch(Exception e) 
        {
        	e.printStackTrace(System.out);
        } 
        finally 
        {
        	if(server.serverSocket != null)
        		server.serverSocket.close();
        	server.serverSocket = null;
        }

    }
    
}
