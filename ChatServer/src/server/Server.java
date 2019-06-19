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
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.util.Collections;

public class Server extends JFrame implements ActionListener{
	private Socket socket;
	private ServerSocket serverSocket;
	
	private JPanel top;
	private JPanel bottom;
	private JLabel portLabel;
	private JTextField portField;
	private java.awt.List list;
	private JButton startButton;
	private JButton endButton;
	
	private int port;
	private Map clients;
	private List<Thread> threadList;
	private Collections collections;
	
	public Server()
	{
		this.setFrame();
		this.clients = new HashMap();
		this.collections.synchronizedMap(this.clients);
		
	}
	public static void main(String args[])
	{
		Server server = new Server();
		
	}
	private void setFrame()
	{
		this.portLabel = new JLabel("서버 포트 입력 ->");
		this.portField = new JTextField(3);
		this.startButton = new JButton("Server Start");
		this.endButton = new JButton("Server End");
		this.list = new java.awt.List();
		this.top = new JPanel();
		this.bottom = new JPanel();
		this.top.setSize(500,200);
		this.top.setLayout(new FlowLayout());
		this.top.add(portLabel);
		this.top.add(portField);
		this.top.add(startButton);
		this.bottom.setSize(500, 200);
		this.bottom.add(endButton);
		this.setLayout(new BorderLayout());
		this.add("North", top);
		this.add("Center", list);
		this.add("South", bottom);
		this.setTitle("Server");
		this.setSize(500,1000);
		this.startButton.addActionListener(this);
		this.endButton.addActionListener(this);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
		if(obj == this.startButton)
		{
			this.startServer();
		}
		else if(obj == this.endButton)
		{
			
		}
		//startButton.
		//endButton 	�늻瑜� �븣�쓽 event.
	}
	public void test()
	{
		//test
	}
	private void startServer()
	{
		this.port = Integer.parseInt(portField.getText());
		
		try
		{
			this.serverSocket = new ServerSocket(this.port);
			this.list.add("서버가 시작되었습니다.");
			ServerThread serverThread;
			
			while(!Thread.currentThread().isInterrupted() && (this.socket = this.serverSocket.accept()) != null)
        	{
        		serverThread = new ServerThread(this.socket);
        		serverThread.start();
        		this.threadList.add(serverThread);
        		if(this.threadList.size() > 10)
        			break;
        		
        		
        	}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		finally 
        {
        	try
        	{
        		this.serverSocket.close();
        	}
        	catch(Exception e)
        	{
        		this.serverSocket = null;
        	}
        }
		
		
	}
	private void endServer()
	{
		
		//client �쑀臾� 寃��궗
		//client 臾� -> �꽌踰� list�뿉留� �쓣�슦怨� �꽌踰� 醫낅즺
		//client �쑀 -> broadcast �썑 �꽌踰� 醫낅즺
	}
	public static void joinClient()
	{
		
	}
	public static void broadcasting()
	{
		// Map clients�쓽 紐⑤뱺 client濡� 硫붿떆吏� �쟾�넚.
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
		//startButton -> �빐�떦 port濡� �냼耳� �뿴湲�
		//endButton -> �꽌踰� 媛뺤젣 醫낅즺.
		//				醫낅즺�쟾�뿉 �젒�냽�맂 �겢�씪�씠�뼵�듃 �솗�씤.
		//				�겢�씪�씠�뼵�듃媛� �뾾�쑝硫� 諛붾줈 醫낅즺
		//				�겢�씪�씠�뼵�듃媛� �엳�쑝硫� 紐⑤뱺 �겢�씪�씠�뼵�듃�뿉寃� 醫낅즺 硫붿떆吏� 蹂대궡怨� 醫낅즺
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