package client;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
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
import java.util.Map;
/**
 * Client에서 챗팅이나 기타 행동을 하면, 서버로 write해준다.
 * read는 ClientThread클래스에서만 하고 Client에서는 하지 않는다.
 * 
 * @author hkj
 *
 */
public class Client extends JFrame implements ActionListener, KeyListener{
	private ByteArrayOutputStream baos;
	private Socket socket;
	private OutputStream out;
	private FileP2P filep2p;
	
	private Map<String, String> sendFileMap;
	private Map<String, String> receiveFileMap;
	
	private int port;
	private String ip;
	private String id;

	private int packetSize;
	private byte type;
	private String filePath;
	private String fileName;
	private long dataSize;
	
	/// Frame을 구성하는 변수 ///
	private JPanel top;
	private JPanel bottom;
	private JPanel bottomButton;
	private JPanel bottomChat;
	private JScrollPane jscroll;
	private JTextArea list;
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
	
	private JButton acceptButton;
	private JButton declineButton;
	///
	
	///////// 상수 
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
	////////
	
	public Client()
	{
		this.baos = new ByteArrayOutputStream();
		this.sendFileMap = new HashMap<String, String>();
		this.receiveFileMap = new HashMap<String, String>();
		this.setFrame();
		
	}

	public static void main(String[] args) 
	{
		Client client = new Client();
	}

	public String getIp()
	{
		return this.ip;
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
		this.list = new JTextArea();
		this.list.setEditable(false);
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
		this.setLocation(400, 400);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	
	/**
	 * type == FILE_ASK를 받았을 경우에 열리는 창.
	 * 
	 * @param name
	 * @param fileName
	 * @param fileSize
	 */
	public void viewFileInfo(String id, String name, String fileName, long fileSize)
	{
		JFrame fileInfoFrame = new JFrame("file");
		JPanel top = new JPanel();
		JPanel bottom = new JPanel();
		fileInfoFrame.setLayout(new BorderLayout());
		
		double temp = 0;
		StringBuilder info = new StringBuilder(name).append("님이 ").append(id).append("에게 ").append(fileName).append("(");
		if(fileSize < 1024)
			info.append(fileSize).append("bytes) 파일을 전송하였습니다.");
		if(fileSize >= 1024 && fileSize < 1024 * 1024)
		{
			temp = fileSize/1024;
			info.append(temp).append("KB) 파일을 전송하였습니다.");
		}
		if(fileSize >= 1024 * 1024)
		{
			temp = fileSize / (1024 * 1024);
			info.append(temp).append("MB) 파일을 전송하였습니다.");
		}
		
		JLabel infoLabel = new JLabel(info.toString());
		top.add(infoLabel);
		this.acceptButton = new JButton("수신");
		this.declineButton = new JButton("거부");
		bottom.add(this.acceptButton);
		bottom.add(this.declineButton);
		this.acceptButton.addActionListener(this);
		this.declineButton.addActionListener(this);
		fileInfoFrame.add("North", top);
		fileInfoFrame.add("South", bottom);
		
		fileInfoFrame.setSize(500, 100);
		top.setSize(500, 50);
		bottom.setSize(500, 50);
		fileInfoFrame.setVisible(true);
	}	
	
	//이미지 보여주는 창.
		//상대가 보낼 때마다 이미지가 뜸.
	public void viewImage(String title, byte[] buffer)
	{
		JFrame imageFrame = new JFrame("image");
		imageFrame.setTitle(title);

		String type = this.detectImageType(title);		
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
			imageFrame.setSize(bi.getWidth(), bi.getHeight());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		imageFrame.setLocation(700,300);
		imageFrame.setVisible(true);
	}
	
	public String detectImageType(String title)
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
	
	public void addList(String s)
	{
		if(s == null)
			throw new java.lang.NullPointerException("addList parameter is null");
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
			this.selectFile();
			this.writeFileAsk(this.baos);
			this.saveSendFileInfo();
		}
		if(obj == this.imageSendButton)
		{
			this.selectImage();
			this.writeImage(this.baos);
		}
		if(obj == acceptButton)
		{
			System.out.println("ACCEPT BUTTON CLICKED!");
			this.writeFileAccept(this.baos, this.fileName);
		}
		if(obj == declineButton)
		{
			System.out.println("DECLINE BUTTON CLICKED!");
			this.writeFileDecline(this.baos);
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

	//애매하네 이거.
	private void reset()
	{
		this.baos.reset();
		this.packetSize = 0;
		this.type = 0;
		this.dataSize = 0;		
		this.fileName = null;
		this.filePath = null;
	}
	
	protected void saveSendFileInfo()
	{
		this.sendFileMap.put(this.fileName, this.filePath);
	}
	protected String getSendFilePath(String fileName)
	{
		return this.sendFileMap.get(fileName);
	}
	protected void saveReceiveFileInfo()
	{
		this.receiveFileMap.put(this.)
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
		System.out.println("selectImage@@fileName : " + this.fileName);
	}
	private void selectFile()
	{
		JFileChooser chooser = new JFileChooser();
		if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
		{
			this.filePath = chooser.getSelectedFile().toString();
		}
		int temp = this.filePath.lastIndexOf("\\");
		this.fileName = this.filePath.substring(temp + 1);
		System.out.println("selectFile@@fileName : " + this.fileName);
	}
	
	/**
	 * 27bytes 헤더
	 * SOP(4) + packetSize(4) + type(1) + flag(2)
	 *  + nameSize(4) + fileNameSize(4) + dataSize(4) = 27bytes
	 * @return
	 */
	private int getPacketSize()
	{
		int packetSize = 27;
		
		if(this.id != null)
			packetSize += this.id.getBytes().length;
		if(this.fileName != null)
			packetSize += this.fileName.getBytes().length;
		if(this.dataSize != 0)
			packetSize += this.dataSize;
		
		return packetSize;
	}
	
	private void writeJoin(OutputStream os)
	{
		this.reset();
	
		this.ip = ipField.getText();
		this.port = Integer.parseInt(portField.getText());
		this.id = idField.getText();
		this.packetSize = this.getPacketSize();
		byte[] header = new byte[SOP.length + 4 + 1 + FLAG10.length + 4 + 4 + 4];
		int offset = 0;
		
		// SOP
		System.arraycopy(SOP, 0, header, offset, SOP.length);
		offset += SOP.length;
		
		// 패킷 크기 4bytes
		this.intToByteBuffer(this.packetSize, header, offset);
		offset += 4;
		
		// 타입
		header[offset++] = JOIN;
		
		// 플래그
		System.arraycopy(FLAG10, 0, header, offset, FLAG10.length);
		offset += FLAG10.length;
		
		// 네임 사이즈
		this.intToByteBuffer(this.id.getBytes().length, header, offset);
		offset += 4;
		
		// 파일 네임 사이즈
		System.arraycopy(ZERO, 0, header, offset, 4);
		offset += 4;
		
		//데이터 사이즈
		System.arraycopy(ZERO, 0, header, offset, 4);
		offset += 4;
		
		try
		{	
			os.write(header, 0, header.length);
			os.write(id.getBytes());//NAME
			os.write(EOP);
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
			ClientThread clientThread = new ClientThread(this, this.socket, this.id);
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
		this.reset();
		byte[] temp = chat.getBytes();
		this.dataSize = temp.length;
		this.packetSize = this.getPacketSize();
		byte[] header = new byte[SOP.length + 4 + 1 + FLAG10.length + 4 + 4 + 4];
		int offset = 0;
		
		// SOP
		System.arraycopy(SOP, 0, header, offset, SOP.length);
		offset += SOP.length;
		
		// 패킷 크기 4bytes
		this.intToByteBuffer(this.packetSize, header, offset);
		offset += 4;
		
		// 타입
		header[offset++] = CHAT;
		
		// 플래그
		System.arraycopy(FLAG10, 0, header, offset, FLAG10.length);
		offset += FLAG10.length;
		
		// 네임 사이즈
		this.intToByteBuffer(this.id.getBytes().length, header, offset);
		offset += 4;
		
		// 파일 네임 사이즈
		System.arraycopy(ZERO, 0, header, offset, 4);
		offset += 4;
		
		//데이터 사이즈
		this.intToByteBuffer(temp.length, header, offset);
		offset += temp.length;
		
		try
		{
			os.write(header);
			os.write(this.id.getBytes());//NAME
			os.write(temp);
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.baos.reset();
			ioe.printStackTrace(System.out);
		}
		temp = null;
		header = null;
		this.sendMessage(this.out);
	}
	private void writeImage(OutputStream os)
	{
		
		this.reset();
		File file = new File(this.filePath);
		byte[] data = new byte[(int)file.length()];
		int n = 0;
		this.dataSize = data.length;
		this.packetSize = this.getPacketSize();
		
		byte[] header = new byte[SOP.length + 4 + 1 + FLAG10.length + 4 + 4 + 4];
		int offset = 0;
		
		// SOP
		System.arraycopy(SOP, 0, header, offset, SOP.length);
		offset += SOP.length;
		
		// 패킷 크기 4bytes
		this.intToByteBuffer(this.packetSize, header, offset);
		offset += 4;
		
		// 타입
		header[offset++] = IMAGE;
		
		// 플래그
		System.arraycopy(FLAG10, 0, header, offset, FLAG10.length);
		offset += FLAG10.length;
		
		// 네임 사이즈
		this.intToByteBuffer(this.id.getBytes().length, header, offset);
		offset += 4;
		
		// 파일 네임 사이즈
		this.intToByteBuffer(this.fileName.getBytes().length, header, offset);
		offset += 4;
		
		//데이터 사이즈
		this.intToByteBuffer((int)this.dataSize, header, offset);
		offset += 4;
		
		try
		{
			os.write(header);
			os.write(this.id.getBytes());//NAME
			os.write(this.fileName.getBytes());//FNAME
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		
		try(FileInputStream fis = new FileInputStream(file))
		{
			n = fis.read(data, 0, data.length);
			
			if(n != data.length)
				throw new java.io.IOException();
			
			os.write(data);//DATA
			os.write(EOP);//EOP
			this.sendMessage(this.out);
		}
		catch(IOException ioe)
		{
			this.reset();
			ioe.printStackTrace(System.out);
		}		
		data = null;
		header = null;
	}
	
	private void writeFileAsk(OutputStream os)
	{
		this.reset();
		File file = new File(this.filePath);
		this.dataSize = file.length();
		this.packetSize = this.getPacketSize();
		
		byte[] header = new byte[SOP.length + 4 + 1 + FLAG10.length + 4 + 4 + 4];
		int offset = 0;
		
		// SOP
		System.arraycopy(SOP, 0, header, offset, SOP.length);
		offset += SOP.length;
		
		// 패킷 크기 4bytes
		this.intToByteBuffer(this.packetSize, header, offset);
		offset += 4;
		
		// 타입
		header[offset++] = FILE_ASK;
		
		// 플래그
		System.arraycopy(FLAG10, 0, header, offset, FLAG10.length);
		offset += FLAG10.length;
		
		// 네임 사이즈
		this.intToByteBuffer(this.id.getBytes().length, header, offset);
		offset += 4;
		
		// 파일 네임 사이즈
		this.intToByteBuffer(this.fileName.getBytes().length, header, offset);
		offset += 4;
		
		//데이터 사이즈
		this.intToByteBuffer((int)this.dataSize, header, offset);
		offset += 4;
		
		try
		{
			os.write(header);
			os.write(this.id.getBytes());//NAME
			os.write(this.fileName.getBytes());//FNAME
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.reset();
			ioe.printStackTrace(System.out);
		}
		
		header = null;
		this.sendMessage(this.out);
	}
	private void writeFileAccept(OutputStream os, String fileName)
	{
		this.reset();
		this.fileName = fileName;
		this.packetSize = this.getPacketSize();
		
		byte[] header = new byte[SOP.length + 4 + 1 + FLAG10.length + 4 + 4 + 4];
		int offset = 0;
		
		// SOP
		System.arraycopy(SOP, 0, header, offset, SOP.length);
		offset += SOP.length;
		
		// 패킷 크기 4bytes
		this.intToByteBuffer(this.packetSize, header, offset);
		offset += 4;
		
		// 타입
		header[offset++] = FILE_ACCEPT;
		
		// 플래그
		System.arraycopy(FLAG10, 0, header, offset, FLAG10.length);
		offset += FLAG10.length;
		
		// 네임 사이즈
		this.intToByteBuffer(this.id.getBytes().length, header, offset);
		offset += 4;
		
		// 파일 네임 사이즈
		this.intToByteBuffer(this.fileName.getBytes().length, header, offset);
		offset += 4;
		
		//데이터 사이즈
		this.intToByteBuffer((int)this.dataSize, header, offset);
		offset += 4;
		
		try
		{
			os.write(header);
			os.write(this.id.getBytes());//NAME
			os.write(this.fileName.getBytes());//FNAME
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.reset();
			ioe.printStackTrace(System.out);
		}
		
		header = null;
		this.sendMessage(this.out);
	}
	private void writeFileDecline(OutputStream os)
	{
		this.reset();
		this.fileName = fileName;
		this.packetSize = this.getPacketSize();
		
		byte[] header = new byte[SOP.length + 4 + 1 + FLAG10.length + 4 + 4 + 4];
		int offset = 0;
		
		// SOP
		System.arraycopy(SOP, 0, header, offset, SOP.length);
		offset += SOP.length;
		
		// 패킷 크기 4bytes
		this.intToByteBuffer(this.packetSize, header, offset);
		offset += 4;
		
		// 타입
		header[offset++] = FILE_ACCEPT;
		
		// 플래그
		System.arraycopy(FLAG10, 0, header, offset, FLAG10.length);
		offset += FLAG10.length;
		
		// 네임 사이즈
		this.intToByteBuffer(this.id.getBytes().length, header, offset);
		offset += 4;
		
		// 파일 네임 사이즈
		this.intToByteBuffer(this.fileName.getBytes().length, header, offset);
		offset += 4;
		
		//데이터 사이즈
		this.intToByteBuffer((int)this.dataSize, header, offset);
		offset += 4;
		
		try
		{
			os.write(header);
			os.write(this.id.getBytes());//NAME
			os.write(this.fileName.getBytes());//FNAME
			os.write(EOP);
		}
		catch(IOException ioe)
		{
			this.reset();
			ioe.printStackTrace(System.out);
		}
		
		header = null;
		this.sendMessage(this.out);
	}
	private void writeFileSend(OutputStream os)
	{
		
	}
	private void writeFinish(OutputStream os)
	{
		this.reset();
		this.packetSize = this.getPacketSize();
		try
		{
			os.write(SOP);
			os.write(this.intToByteBuffer(this.packetSize));
			os.write(FINISH);//TYPE
			os.write(FLAG10);//FLAG
			os.write(this.intToByteBuffer(this.id.getBytes().length));//NSIZE
			os.write(ZERO);//FNSIZE
			os.write(ZERO);//DSIZE
			os.write(this.id.getBytes());//NAME
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
			System.out.println("@sendMessage@packetSize : " + this.packetSize);
			byte[] test = this.intToByteBuffer(this.packetSize);
			System.out.println("@this.getBytes()@ : " + this.getBytes().length);
			os.write(this.getBytes());
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	
	protected void sendFile(String fileName)
	{
		
		System.out.println("다른 놈의 Client에서 받은 FileAccept 요청을 Client-Thread에서 읽은 후, Client에서 처리.");
		
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
		this.intToByteBuffer(value, buffer, 0);
		return buffer;
	}
	private byte[] intToByteBuffer(int value, byte[] buffer, int offset)
	{
		buffer[offset++] = (byte)((value & 0xff000000) >> 24); 
		buffer[offset++] = (byte)((value & 0x00ff0000) >> 16);
		buffer[offset++] = (byte)((value & 0x0000ff00) >> 8);
		buffer[offset++] = (byte)((value & 0x000000ff));

		return buffer;
	}

	
}
