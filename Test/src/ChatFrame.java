import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.List;

public class ChatFrame extends JFrame{
	private static int clientNumber;
	private List list;
	
	public ChatFrame(String type) 
	{
		if(type == null)
			throw new java.lang.NullPointerException();
		if(!(type.toLowerCase().startsWith("server") || type.toLowerCase().startsWith("client")))
			throw new java.lang.IllegalArgumentException();
		
		this.clientNumber = 0;
				
		if(type.toLowerCase().startsWith("server"))
		{
			JPanel panel = new JPanel();
			this.serverPanel(panel);
			this.add(panel);
		}
		
		if(type.toLowerCase().startsWith("client"))
		{
			
			
		}
		
		
		this.setTitle(type);
		this.setSize(500,1000);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
		
	}
	
	private void serverPanel(JPanel panel)
	{
		this.serverTop(panel);
		this.serverBottom(panel);
	}
	private void serverTop(JPanel panel)
	{
		panel.setLayout(null);
		
		JLabel clientNumberLabel1 = new JLabel("접속한 client 수 :");
		clientNumberLabel1.setBounds(10,10,120,25);
		panel.add(clientNumberLabel1);
		
		JLabel clientNumberLabel2 = new JLabel("" + clientNumber);
		clientNumberLabel2.setBounds(120, 10, 15, 25);
		panel.add(clientNumberLabel2);
		
		JLabel clientIdLabel1 = new JLabel("접속한 client id:");
		clientIdLabel1.setBounds(10, 35, 120, 25);
		panel.add(clientIdLabel1);
		
		
	}
	private void serverBottom(JPanel panel)
	{
		//0111 1111
	}
	public static void main(String args[]) 
	{
		ChatFrame c = new ChatFrame("SERVER");
		boolean a = true;
		boolean b = false;
		
		
	
	}

}