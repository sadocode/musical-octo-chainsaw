package server;

import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.LinkedList;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
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
			//endButton 	
		}
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
        		//clients에  id, socket.getOutputStream 을 입력해준다.
        		//
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
		//client 유무 검사
		//client 무 -> 서버 list에만 띄우고 서버 종료
		//client 유 -> broadcast 후 서버 종료
	}
	private void joinClient()
	{
		
	}
	public static void addList()
	{
		
	}
	public static void broadcasting()
	{
		// 
		// Map clients의 모든 client로 메시지 전송.
	}
}
