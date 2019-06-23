package client;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

public class Client extends JFrame implements ActionListener{
	private ByteArrayOutputStream baos;
	private Socket socket;
	private int port;
	private String ip;
	private String id;
	private String type;
	
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
	
	public Client()
	{
		this.baos = new ByteArrayOutputStream();
		this.setFrame();
	}
	
	public static void main(String[] args) 
	{
		Client client = new Client();
		byte[] a = new byte[4];
		
		
	}
	private int byteBufferToInt(byte[] buffer, int size)
	{
		int value = 0;
		int index = size - 1;
		int x = 0;
		while(index >= 0)
		{
			value += (int)((buffer[index] & 0xFF) << 8 * (size - index-- - 1));
			
			x++;
		}
		
		System.out.println(value + " @@" + x);
		return value;
	}
	private long byteBufferToLong(byte[]buffer, int size)
	{
		long value = 0;
		int index = size - 1;
		while(index >= 0)
		{
			value += (long)((buffer[index] & 0xFFL) << 8 * (size - index-- - 1));
			System.out.println(value);
		}
		return value;
	}
	private void setFrame()
	{
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
		this.list = new java.awt.List();
		this.top = new JPanel();
		this.bottom = new JPanel();
		this.bottomButton = new JPanel();
		this.bottomChat = new JPanel();
		
		this.setSize(500, 1000);
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
		
		//key 액션 이벤트도 추가해야해..
		//엔터 누르면 채팅 전송되도록.
		
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
			
		}
		if(obj == this.endButton)
		{
			
		}
		if(obj == this.fileSendButton)
		{
			
		}
		if(obj == this.imageSendButton)
		{
			
		}
		
		//각 버튼 별 이벤트 발생.
		// 키 이벤트도 등록해야해.
	}
	
	//이거 안 쓸 거 같은데?
	//그냥 각 처리에서 writeXXX할 거 같은디
	private void write(OutputStream os)
	{
		this.baos.reset();		
		switch(this.type)
		{
		case("JOIN"):
			this.writeJoin(this.baos);
			break;
		case("CHAT"):
			break;
		case("IMAGE"):
			break;
		case("FILE_ASK"):
			break;
		case("FILE_ACCEPT"):
			break;
		case("FILE_DECLINE"):
			break;
		case("FILE_SEND"):
			break;
		case("FINISH"):
			break;
		default:
			break;
		}
	}
	
	
	private void writeJoin(OutputStream os)
	{
		
		//packet을 만들어줌.
	}
	private void writeChat(OutputStream os)
	{
		
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
		
	}
	/**
	 * server로 baos의 값을 보내는 메소드
	 * @param os
	 * @return baos
	 */
	private byte[] sendMessage(OutputStream os)
	{
		return this.baos.toByteArray();
	}
	private byte[] getBytes()
	{
		
		return this.baos.toByteArray();
	}
	
}
