package server;

import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.LinkedList;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JTextArea;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.util.Collections;

public class Server extends JFrame implements ActionListener{
	protected Socket socket;
	protected ServerSocket serverSocket;
	
	private JScrollPane jscroll;
	private JPanel top;
	private JPanel bottom;
	private JLabel portLabel;
	private JTextField portField;
	public static JTextArea list;
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
	public static void broadcasting(byte[] packet)
	{
		Iterator it = clients.keySet().iterator();
		
		while(it.hasNext())
		{
			try
			{
				OutputStream os = (OutputStream)clients.get(it.next());
				os.write(packet);
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace(System.out);
			}
		}
	}
	public static void addList(String s)
	{
		list.append(s+"\r\n");
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
		list = new JTextArea();
		list.setEditable(false);
		this.jscroll = new JScrollPane(list);
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
		this.add("Center", this.jscroll);
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
			//broadcasting();
			
		addList("서버가 종료되었습니다.");
		
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
