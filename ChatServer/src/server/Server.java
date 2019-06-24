
package server;

import java.io.OutputStream;
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
	protected Socket socket;
	protected ServerSocket serverSocket;
	
	
	private JPanel top;
	private JPanel bottom;
	private JLabel portLabel;
	private JTextField portField;
	public static java.awt.List list;
	private JButton startButton;
	private JButton endButton;
	
	private int port;
	public static Map clients;
	
	private List<Thread> threadList;
	public static Collections collections;
	
	public Server()
	{
		this.setFrame();
		clients = new HashMap();
		collections.synchronizedMap(clients);
		
	}
	public static void main(String args[])
	{
		Server server = new Server();
	}
	public static void broadcasting()
	{
		
		// Map clients의 모든 client로 메시지 전송.
	}
	public static void addClient(String id, OutputStream os)
	{
		clients.put(id, os);
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
		this.setSize(500,600);
		this.startButton.addActionListener(this);
		this.endButton.addActionListener(this);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocation(500, 500);
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
			this.endServer(); 	
		}
	}
	
	private void startServer()
	{
		this.port = Integer.parseInt(portField.getText());
		addList("서버가 시작되었습니다.");
		try
		{
			this.serverSocket = new ServerSocket(this.port);
			Thread thread = new StartThread();
			thread.start();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	
	private void endServer()
	{
		if(!clients.isEmpty())
			broadcasting();
			
		list.add("서버가 종료됩니다.");
		
		try
		{
			this.socket.close();
			this.serverSocket.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		//client 유무 검사
		//client 무 -> 서버 list에만 띄우고 서버 종료
		//client 유 -> broadcast 후 서버 종료
	}
	private void joinClient()
	{
		
	}
	public static void addList(String s)
	{
		list.add(s);
	}
	class StartThread extends Thread
	{
		@Override
		public void run()
		{
			StringBuilder sb;
			while(true)
			{
				try
				{
					socket = serverSocket.accept();					
				}
				catch(IOException ioe)
				{
					ioe.printStackTrace(System.out);
				}
				sb = new StringBuilder("[").append(socket.getInetAddress()).append(":").append(socket.getPort()).append("]").append("에서 접속하였습니다.");
				Server.addList(sb.toString());
				ServerThread serverThread = new ServerThread(socket);
				serverThread.start();
			}
		}
	}
}
