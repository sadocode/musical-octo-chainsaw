package client;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.File;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Graphics;

public class Client extends JFrame implements ActionListener, KeyListener{
	private ByteArrayOutputStream baos;
	private Socket socket;
	private int port;
	private String ip;
	private String id;
	private int type;
	private OutputStream out;
	private String filePath;
	private String fileName;
	
	private JPanel top;
	private JPanel bottom;
	private JPanel bottomButton;
	private JPanel bottomChat;
	private JScrollPane jscroll;
	public static JTextArea list;
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
		this.setTitle("Client");
		this.ipLabel = new JLabel("ip ->");
		this.ipField = new JTextField(12);
		this.portLabel = new JLabel("port ->");
		this.portField = new JTextField(3);
		this.idLabel = new JLabel("id -> ");
		this.idField = new JTextField(5);
		this.startButton = new JButton("채팅 시작");
		this.fileSendButton = new JButton("파일 전송");
		this.imageSendButton = new JButton("사진 전송");
		this.endButton = new JButton("채팅 종료");
		this.chatField = new JTextField(25);
		list = new JTextArea();
		list.setEditable(false);
		this.jscroll = new JScrollPane(list);
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
		//key 이벤트 처리
		// enter -> 채팅 입력
		
		this.add("North", this.top);
		this.add("Center", this.jscroll);
		this.add("South", this.bottom);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	

	public static String detectImageType(String title)
	{
		if(title.endsWith("jpg"))
			return "jpg";
		else if(title.endsWith("png"))
			return "png";
		else if(title.endsWith("bmp"))
			return "bmp";
		else if(title.endsWith("ico"))
			return "ico";
		else
			return "error";
	}

	//이미지 보여주는 창.
	//상대가 보낼 때마다 이미지가 뜸.
	public static void viewImage(String title, byte[] buffer)
	{
		JFrame imageFrame = new JFrame("image");
		imageFrame.setTitle(title);
		
		
		String type = detectImageType(title);		
		if(type == "error")
			return;
		
		ByteArrayInputStream image;
		BufferedImage bi;
		JPanel panel;
		try
		{
			image = new ByteArrayInputStream(buffer);
			bi = ImageIO.read(image);
			panel = new JPanel() {
				public void paintComponent(Graphics g) {
					super.paintComponent(g);
					g.drawImage(bi, 0, 0, null);
				}
			};
			imageFrame.add(panel);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		imageFrame.setLocation(700,300);
		imageFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
		imageFrame.setVisible(true);
	}
	public static void addList(String s)
	{
		list.append(s + "\r\n");
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
			this.writeFileAsk(this.baos);
		}
		if(obj == this.imageSendButton)
		{
			this.selectImage();
			this.writeImage(this.baos);
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
	
	/**
	 * 이미지 파일을 선택하는 메소드
	 * 이미지 파일이 아닌 경우 선택이 불가능하다.
	 * 선택한 이미지 파일의 경로는 this.imageFile에 저장된다.
	 */
	private void selectImage()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("image","jpg","bmp","png","ico"));
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			this.filePath = chooser.getSelectedFile().toString();
		}
		int temp = this.filePath.lastIndexOf("\\");
		this.fileName = this.filePath.substring(temp + 1);
		System.out.println(this.fileName);
	}
	private void selectFile()
	{
		JFileChooser chooser = new JFileChooser();
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			this.filePath = chooser.getSelectedFile().toString();
		}
		int temp = this.filePath.lastIndexOf("\\");
		System.out.println("### temp : " + temp);
		this.fileName = this.filePath.substring(temp);
		System.out.println(this.fileName);
	}
	private void writeJoin(OutputStream os)
	{
		this.baos.reset();
	
		this.ip = ipField.getText();
		this.port = Integer.parseInt(portField.getText());
		this.id = idField.getText();
		
		try
		{	
			os.write(SOP);
			os.write(JOIN);
			os.write(FLAG10);
			os.write(this.intToByteBuffer(id.getBytes().length));
			os.write(id.getBytes());
			os.write(ZERO);
			os.write(ZERO);
			os.write(EOP);
			// baos에 데이터 저장.	
		}
		catch(IOException ioe)
		{
			this.baos.reset();
			ioe.printStackTrace(System.out);
		}
		
		try
		{
			this.socket = new Socket(this.ip, this.port);
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
			os.write(this.intToByteBuffer(this.id.getBytes().length));
			os.write(this.id.getBytes());
			os.write(ZERO);
			os.write(this.intToByteBuffer(temp.length));
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
		this.baos.reset();
		File file = new File(this.filePath);
		byte[] data = new byte[(int)file.length()];
		int n = 0;
		
		try
		{
			os.write(SOP);
			os.write(IMAGE);
			os.write(FLAG10);
			os.write(this.intToByteBuffer(this.id.getBytes().length));
			os.write(this.id.getBytes());
			os.write(this.intToByteBuffer(this.fileName.getBytes().length));
			os.write(this.fileName.getBytes());
			os.write(this.intToByteBuffer((int)file.length()));
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		
		try(FileInputStream fis = new FileInputStream(file))
		{
			while(true)
			{
				n = fis.read();
	
				if(n < 0)
					break;
				os.write(n);
			}
			System.out.println("why it doesnt work? ");
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		
		this.sendMessage(this.out);
		
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
		
		try
		{
			os.write(SOP);
			os.write(FINISH);
			os.write(FLAG10);
			os.write(this.intToByteBuffer(this.id.getBytes().length));
			os.write(this.id.getBytes());
			os.write(ZERO);
			os.write(ZERO);
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.baos.reset();
			ioe.printStackTrace(System.out);
		}
		
		this.sendMessage(this.out);
		this.closeChat();
	}
	private void closeChat()
	{
		try
		{
			this.socket.close();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
			if(this.socket != null)
				this.socket = null;
		}
	}
	private void sendMessage(OutputStream os)
	{
		try
		{
			System.out.println("@sendMessage@" +this.getBytes());
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
}
