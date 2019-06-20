package server;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.List;

public class ServerFrame extends JFrame {
	private static int clientNumber;
	private JLabel portLabel = new JLabel("서버 port 입력 :");
	private JTextField portField = new JTextField(3);
	private JButton startButton = new JButton("Server Start");
	private JButton endButton = new JButton("Server End");
	private List list = new List();
	private JPanel top = new JPanel();
	private JPanel bottom = new JPanel();
	
	private int port;
	
	public ServerFrame() 
	{
		this.clientNumber = 0;
		
		this.top.setSize(500, 200);
		this.top.setLayout(new FlowLayout());
		this.top.add(portLabel);
		this.top.add(portField);
		this.top.add(startButton);
		this.bottom.setSize(500,200);
		this.bottom.add(endButton);
		
		
		this.setLayout(new BorderLayout());
		this.add("North", top);
		this.add("Center", list);
		this.add("South", bottom);
		
		
		this.setTitle("Server");
		this.setSize(500,1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//this.setVisible(true);		
	}
	public void setList()
	{
		//Server 클래스에서 List를 수정하기위해서 사용하는 함수.
		//	서버가 시작될 때
		//	클라이언트가 접속했을 때
		// 	채팅을 쳤을 떄
		//	이미지를 보냈을 때
		//	파일관련 메시지
		//	클라이언트가 나갔을 때
		//	서버를 껐을 때
		//위의 경우에 setList()를 하도록  Server 클래스에서
		//	각 경우마다 이벤트를 심는다.
	}
	public List getList()
	{
		return this.list;
	}
	public JTextField getPortField()
	{
		return this.portField;
	}
	public JButton getStartButton()
	{
		return this.startButton;
	}
	public JButton getEndButton()
	{
		return this.endButton;
	}
	//Server에서 가져감.
	public int getPort()
	{
		return this.port;
	}
	
}	