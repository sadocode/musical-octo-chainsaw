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

public class Client extends JFrame implements ActionListener{
	private ByteArrayOutputStream baos;
	private Socket socket;
	private int port;
	private String ip;
	private String id;
	private String type;
	
	
	
	
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
		JPanel top = new JPanel();
		//set Frame
	}

	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		Object obj = e.getSource();
		
		//각 버튼 별 이벤트 발생.
		
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