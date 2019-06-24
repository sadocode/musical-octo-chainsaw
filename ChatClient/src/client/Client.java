
package client;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class Client extends JFrame implements ActionListener, KeyListener{
	private ByteArrayOutputStream baos;
	private Socket socket;
	private int port;
	private String ip;
	private String id;
	private int type;
	private OutputStream out;
	
	
	private JPanel top;
	private JPanel bottom;
	private JPanel bottomButton;
	private JPanel bottomChat;
	private java.awt.List list;
	private JLabel ipLabel;
	private JLabel portLabel;
	private JLabel idLabel;
	private JTextField ipField;
	private JTextField portField;
	private JTextField idField;
	private JButton startButton;
	private JButton endButton;
	private JButton fileSendButton;
	private JButton imageSendButton;
	private JTextField chatField;
	
	private static final byte[] SOP = {0x00, 0x00, 0x00, 0x00};
	private static final byte[] EOP = {(byte)0xff, (byte)0xff, (byte)0xff, (byte)0xff};
	
	public static final byte JOIN = 0;
	public static final byte CHAT = 1;
	public static final byte IMAGE = 2;
	public static final byte FILE_ASK = 3;
	public static final byte FILE_ACCEPT = 4;
	public static final byte FILE_DECLINE = 5;
	public static final byte FILE_SEND = 6;
	public static final byte FINISH = 127;
	
	public static final byte[] ZERO = {0x00, 0x00, 0x00, 0x00};
	
	public static final byte[] FLAG10 = {0x01, 0x00};
	public static final byte[] FLAG00 = {0x00, 0x00};
	public static final byte[] FLAG01 = {0x00, 0x01};
	
	public Client()
	{
		this.baos = new ByteArrayOutputStream();
		this.setFrame();
	}
	
	public static void main(String[] args) 
	{
		Client client = new Client();
		
	}

	private void setFrame()
	{
		this.ipLabel = new JLabel("ip ->");
		this.ipField = new JTextField(12);
		this.portLabel = new JLabel("port ->");
		this.portField = new JTextField(3);
		this.idLabel = new JLabel("id -> ");
		this.idField = new JTextField(5);
		this.startButton = new JButton("¿¿ ¿¿");
		this.fileSendButton = new JButton("¿¿ ¿¿");
		this.imageSendButton = new JButton("¿¿ ¿¿");
		this.endButton = new JButton("¿¿ ¿¿");
		this.chatField = new JTextField(25);
		this.list = new java.awt.List();
		this.top = new JPanel();
		this.bottom = new JPanel();
		this.bottomButton = new JPanel();
		this.bottomChat = new JPanel();
		
		this.setSize(500, 600);
		this.setLayout(new BorderLayout());
		this.top.setLayout(new FlowLayout());
		this.bottom.setLayout(new BorderLayout());
		this.bottomChat.setLayout(new FlowLayout());
		
		this.top.setSize(500, 200);
		this.bottom.setSize(500, 200);
		this.bottomButton.setSize(500,100);
		this.bottomChat.setSize(500, 100);
		this.top.add(ipLabel);
		this.top.add(ipField);
		this.top.add(portLabel);
		this.top.add(portField);
		this.top.add(idLabel);
		this.top.add(idField);
		this.top.add(startButton);
		this.bottomButton.add(fileSendButton);
		this.bottomButton.add(imageSendButton);
		this.bottomButton.add(endButton);
		this.bottomChat.add(chatField);
		this.bottom.add("North", this.bottomButton);
		this.bottom.add("South", this.bottomChat);
		
		this.startButton.addActionListener(this);
		this.endButton.addActionListener(this);
		this.fileSendButton.addActionListener(this);
		this.imageSendButton.addActionListener(this);
		this.chatField.addKeyListener(this);
		//key ¿¿¿ ¿¿
		// enter -> ¿¿ ¿¿
		
		this.add("North", this.top);
		this.add("Center", this.list);
		this.add("South", this.bottom);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		if(obj == this.startButton)
		{
			this.writeJoin(this.baos);
		}
		if(obj == this.endButton)
		{
			this.writeFinish(this.baos);
		}
		if(obj == this.fileSendButton)
		{
			
		}
		if(obj == this.imageSendButton)
		{
			
		}

	}
	@Override
	public void keyPressed(KeyEvent e) 
	{
		if (e.getKeyCode() == KeyEvent.VK_ENTER)
		{
			this.writeChat(this.baos, this.chatField.getText());
			this.chatField.setText("");
		}
	}
	public void keyTyped(KeyEvent e)
	{
	
	}
	public void keyReleased(KeyEvent e) 
	{
	
	}
	private void writeJoin(OutputStream os)
	{
		this.baos.reset();
	
		this.ip = ipField.getText();
		this.port = Integer.parseInt(portField.getText());
		this.id = idField.getText();
		byte[] name = id.getBytes();
		
		try
		{	
		
			os.write(SOP);
			os.write(JOIN);
			os.write(FLAG10);
			os.write(this.intToByteBuffer(name.length));
			os.write(name);
			os.write(ZERO);
			os.write(EOP);
			// baos¿ ¿¿¿ ¿¿.	
		}
		catch(IOException ioe)
		{
			this.baos.reset();
			ioe.printStackTrace(System.out);
		}
		
		try
		{
			this.socket = new Socket(ip, this.port);
			this.out = this.socket.getOutputStream();
			ClientThread clientThread = new ClientThread(this.socket);
			clientThread.start();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	
		this.sendMessage(this.out);
	}
	private void writeChat(OutputStream os, String chat)
	{
		this.baos.reset();
		byte[] temp = chat.getBytes();
		
		try
		{
			os.write(SOP);
			os.write(CHAT);
			os.write(FLAG10);
			os.write(ZERO);
			os.write(temp.length);
			os.write(temp);
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.baos.reset();
			ioe.printStackTrace(System.out);
		}
		
		this.sendMessage(this.out);
	}
	private void writeImage(OutputStream os)
	{
		
	}
	private void writeFileAsk(OutputStream os)
	{
		
	}
	private void writeFileAccept(OutputStream os)
	{
		
	}
	private void writeFileDecline(OutputStream os)
	{
		
	}
	private void writeFileSend(OutputStream os)
	{
		
	}
	private void writeFinish(OutputStream os)
	{
		this.baos.reset();
		byte[] name = this.id.getBytes();
		
		try
		{
			os.write(SOP);
			os.write(FINISH);
			os.write(FLAG10);
			os.write(name.length);
			os.write(name);
			os.write(ZERO);
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.baos.reset();
			ioe.printStackTrace(System.out);
		}
		
		this.sendMessage(this.out);
	}
	
	private void sendMessage(OutputStream os)
	{
		try
		{
			System.out.println(this.getBytes());
			os.write(this.getBytes());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	private byte[] getBytes()
	{
		
		return this.baos.toByteArray();
	}
	private int byteBufferToInt(byte[] buffer, int size)
	{
		int value = 0;
		int index = size - 1;
		while(index >= 0)
		{
			value += (int)((buffer[index] & 0xFF) << 8 * (size - index-- - 1));
		}
		return value;
	}
	private long byteBufferToLong(byte[]buffer, int size)
	{
		long value = 0;
		int index = size - 1;
		while(index >= 0)
		{
			value += (long)((buffer[index] & 0xFFL) << 8 * (size - index-- - 1));
		}
		return value;
	}
	private byte[] intToByteBuffer(int value)
	{
		byte[] buffer = new byte[4];
		int temp = value & 0xff000000;
		buffer[0] = (byte)((value & 0xff000000) >> 24); 
		buffer[1] = (byte)((value & 0x00ff0000) >> 16);
		buffer[2] = (byte)((value & 0x0000ff00) >> 8);
		buffer[3] = (byte)((value & 0x000000ff));
		return buffer;
	}

