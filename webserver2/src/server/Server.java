package server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.io.FileInputStream;

public class Server
{
    public static void main(String args[]) throws Exception
    {
    	Properties info = new Properties();
        int port = 0;
        String webRoute = "";
        String home = "";
        try(FileInputStream fis = new FileInputStream("info.properties")) {
        	info.load(fis);
        	port = Integer.parseInt(info.getProperty("PORT"));
        	webRoute = info.getProperty("WEBROUTE");
        	home = info.getProperty("HOME");
        } catch(Exception e) {
        	e.printStackTrace(System.out);
        }
    	
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("WebServer Socket Created. PortNumber : " + port);
      
        Socket socket = null;
        NewServerThread serverThread = null;
        List<Thread> list = new LinkedList<>();
        
        try {
        	while(!Thread.currentThread().isInterrupted() && (socket = serverSocket.accept()) != null)
        	{
        		serverThread = new NewServerThread(socket, webRoute, home);
        		serverThread.start();
        		list.add(serverThread);
        		if(list.size() >= 300)
        			break;
        	}
        } catch(Exception e) {
        	e.printStackTrace(System.out);
        } finally {
        	serverSocket.close();
        }
        
        
    }
}
