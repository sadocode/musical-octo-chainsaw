import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ClientPro {
	public static void main(String[] args) {
		JFrame jf = new ClientFrame();
		jf.setSize(550, 500);
		jf.setVisible(true);
	}

}

class ClientFrame extends JFrame implements ActionListener, KeyListener {
	JLabel ip = new JLabel("Ip");
	JTextField ipField = new JTextField(13);
	JLabel port = new JLabel("Port");
	JTextField portField = new JTextField(5);
	JLabel id = new JLabel("ID");
	JTextField idField = new JTextField(5);
	JButton con = new JButton("접속요청");
	JButton disCon = new JButton("접속종료");
	List list = new List();
	JTextField uMsg = new JTextField(20);
	JPanel top = new JPanel();
	JPanel bottom = new JPanel();
	String uid = "";

	// 소켓의 생성
	Socket socket;
	DataOutputStream out;

	{
		top.setSize(550, 200);
		top.setLayout(new FlowLayout());
		top.add(ip);
		top.add(ipField);
		top.add(port);
		top.add(portField);
		top.add(id);
		top.add(idField);
		top.add(con);
		bottom.add(disCon);
		bottom.add(uMsg);
		bottom.setSize(550, 200);
		con.addActionListener(this);
		disCon.addActionListener(this);
		uMsg.addKeyListener(this);
	}

	public ClientFrame() {
		this.setLayout(new BorderLayout());
		this.add("North", top);
		this.add("Center", list);
		this.add("South", bottom);

	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			try {
				out.writeUTF("[" + uid + "]" + uMsg.getText());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			uMsg.setText("");
		}
	}

	public void keyReleased(KeyEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == con) {
			String uip = ipField.getText();
			int uport = Integer.parseInt(portField.getText());
			uid = idField.getText();
			init(uid, uip, uport);
		} else if (obj == disCon) {
			System.exit(0);
		}

	}

	public void init(String uid, String uip, int uport) {
		try {
			String serverIp = uip;
			// 소켓을 생성하여 연결을 요청한다.
			socket = new Socket(serverIp, uport);
			out = new DataOutputStream(socket.getOutputStream());
			System.out.println("서버에 연결되었습니다.");
			// 접속자 이름전송
			out.writeUTF(uid);

			// Thread sender = new Thread(new ClientSender(socket, uid));
			Thread receiver = new Thread(new ClientReceiver(socket));

			// sender.start();
			receiver.start();
		} catch (ConnectException ce) {
			ce.printStackTrace();
		} catch (Exception e) {
		}
	}

	class ClientReceiver extends Thread {
		Socket socket;
		DataInputStream in;

		ClientReceiver(Socket socket) {
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
			} catch (IOException e) {
			}
		}

		public void run() {
			while (in != null) {
				try {
					String re = in.readUTF();
					System.out.println(re);
					list.add(re);
				} catch (IOException e) {
				}
			}
		} // run
	}// end class ClientReceiver

}
