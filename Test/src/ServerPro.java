import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerPro {
	public static void main(String[] args) {
		// new ServerFrame().start();
		JFrame jf = new ServerFrame();
		jf.setSize(450, 500);
		jf.setVisible(true);
	}

}// end ServerPro class

class ServerFrame extends JFrame implements ActionListener {
	ServerSocket serverSocket = null;
	Socket socket = null;

	HashMap clients;

	JLabel info = new JLabel("가동하실 서버의 PORT를 입력하세요.");
	JLabel port = new JLabel("PORT");
	JTextField portField = new JTextField(3);
	JButton con = new JButton("서버시작 하기");
	JButton disCon = new JButton("서버종료 하기");
	List list = new List();
	JPanel top = new JPanel();
	JPanel bottom = new JPanel();

	{
		top.setSize(450, 200);
		top.setLayout(new FlowLayout());
		top.add(info);
		top.add(port);
		top.add(portField);
		top.add(con);
		bottom.add(disCon);
		bottom.setSize(450, 200);
		con.addActionListener(this);
		disCon.addActionListener(this);
	}

	public ServerFrame() {
		clients = new HashMap();
		Collections.synchronizedMap(clients);// 동기화 처리
		this.setLayout(new BorderLayout());
		this.add("North", top);
		this.add("Center", list);
		this.add("South", bottom);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == con) {
			int portNum = Integer.parseInt(portField.getText());
			start(portNum);
		} else if (obj == disCon) {
			System.exit(0);
		}

	}

	public void start(int port) {

		try {
			serverSocket = new ServerSocket(port);
			list.add("서버가 시작되었습니다.");
			Thread t1 = new StartThread();
			t1.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	} // start()

	// 클라이언트가 데이터를 입력하면 모든 클라이언트에게 데이터 전달
	void sendToAll(String msg) {
		Iterator it = clients.keySet().iterator();

		while (it.hasNext()) {
			try {
				DataOutputStream out = (DataOutputStream) clients.get(it.next());
				out.writeUTF(msg);
			} catch (IOException e) {
			}
		} // while
	} // sendToAll

	// ServerReceiver thread 호출하는 클래스
	class StartThread extends Thread {
		public void run() {
			while (true) {
				try {
					socket = serverSocket.accept();
				} catch (IOException e) {
					e.printStackTrace();
				}
				list.add("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서접속하였습니다.");
				ServerReceiver thread = new ServerReceiver(socket);
				thread.start();
			}
		}
	}

	class ServerReceiver extends Thread {
		Socket socket;
		DataInputStream in;
		DataOutputStream out;

		ServerReceiver(Socket socket) {
			this.socket = socket;
			try {
				in = new DataInputStream(socket.getInputStream());
				out = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
			}
		}

		// 쓰레드는 클라이언트가 추가될 때 마다 생긴다
		public void run() {
			System.out.println(Thread.currentThread());
			String name = "";
			try {
				name = in.readUTF();
				sendToAll("#" + name + "접속");

				clients.put(name, out);
				list.add("현재 서버접속자 수는 " + clients.size() + "입니다.");
				while (in != null) {
					sendToAll(in.readUTF());
				}
			} catch (IOException e) {
				// ignore
			} finally {
				sendToAll("#" + name + "님이 나가셨습니다.");
				clients.remove(name);
				list.add("[" + socket.getInetAddress() + ":" + socket.getPort() + "]" + "에서 접속을 종료하였습니다.");
				list.add("현재 서버접속자 수는 " + clients.size() + "입니다.");
			} // try
		} // run
	} // ReceiverThread
}
