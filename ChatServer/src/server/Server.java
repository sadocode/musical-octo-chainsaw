package server;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.LinkedList;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;

public class Server extends JFrame implements ActionListener{
	private Socket socket;
	private ServerSocket serverSocket;
	
	private JFrame serverFrame;
	private JPanel top;
	private JPanel bottom;
	private JLabel portLabel;
	private JTextField portField;
	private java.awt.List list;
	private JButton startButton;
	private JButton endButton;
	
	private int port;
	private Map clients;
	
	public Server()
	{
		//jframe 생성.
		
		//Map clients 초기화 및 동기화 처리해줄것.
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//startButton.
		//endButton 	누를 때의 event.
	}
	
	private void startServer(int port)
	{
		//해당 port를 가지고 서버를 시작하.ㅁ
		//serverSocket = new ServerSocket(port);
		
	}
	private void endServer()
	{
		//client 유무 검사
		//client 무 -> 서버 list에만 띄우고 서버 종료
		//client 유 -> broadcast 후 서버 종료
	}
	private void joinClient()
	{
		
	}
	private void broadcasting()
	{
		// Map clients의 모든 client로 메시지 전송.
	}
	public static void main(String args[])
	{
		
	}
}





/*
public class Server implements ActionListener{
	private int port;
	private ServerSocket serverSocket;
	private Map clients;
	private ServerFrame serverFrame;
	
	public Server()
	{
		this.clients = new HashMap();
		this.serverFrame = new ServerFrame();
		this.serverFrame.getStartButton().addActionListener(this);
		this.serverFrame.getEndButton().addActionListener(this);
		
	}
	public Map getClients()
	{
		return clients;
	}
	private void setClients()
	{
		//
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		//startButton -> 해당 port로 소켓 열기
		//endButton -> 서버 강제 종료.
		//				종료전에 접속된 클라이언트 확인.
		//				클라이언트가 없으면 바로 종료
		//				클라이언트가 있으면 모든 클라이언트에게 종료 메시지 보내고 종료
	}
	
	private void setEnvironment() 
	{
	   	
	}
	
	public void setServerSocket()
    {
    	try 
    	{
    		this.serverSocket = new ServerSocket(this.port);
    		System.out.println("WebServer Socket Created. PortNumber : " + this.port);    		
    		//ChatFrame serverFrame = new ChatFrame("Server");
    		 
    	} 
    	catch(IOException ioe) 
    	{
    		ioe.printStackTrace(System.out);
    	}
    }
    
	
	public static void main(String args[]) throws Exception
    {
    	Server server = new Server();
    	server.setEnvironment();
    	server.setServerSocket();
    	
        Socket socket = null;
        List<Thread> list = new LinkedList<>();
        
        ServerThread serverThread;
        
        try 
        {
        	while(!Thread.currentThread().isInterrupted() && (socket = server.serverSocket.accept()) != null)
        	{
        		serverThread = new ServerThread(socket);
        		serverThread.start();
        		list.add(serverThread);
        		if(list.size() > 10)
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
*/