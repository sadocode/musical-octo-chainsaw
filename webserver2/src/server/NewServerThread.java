package server;
import java.net.Socket;

import parsing.ByteProcessing;
import reader.ByteReader;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;

public class NewServerThread extends Thread{
	private String default_path;
	private String home;
	private Socket socket;
	private byte[] requestBuffer;
	
	public NewServerThread(Socket socket, String default_path, String home) {
		if(socket == null)
			throw new java.lang.NullPointerException("socket is null");
		if(default_path == null)
			throw new java.lang.NullPointerException("default_path is null");
		if(home == null)
			throw new java.lang.NullPointerException("home is null");
		this.socket = socket;
		this.default_path = default_path;
		this.home = home;
	}
	/**
	 * IOException 贸府 困秦辑 积己
	 * 
	 * @return
	 * @throws IOException
	 */
	private InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}
	/**
	 * IOException 贸府 困秦辑 积己
	 * 
	 * @return
	 * @throws IOException
	 */
	private OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}
	/**
	 * WebRoute 馆券
	 * @return
	 */
	public String getWebRoute() {
		return this.default_path;
	}
	
	public void run() {
		System.out.println("Thread Created!");
		
		InputStream is = null;
		OutputStream os = null;
		DataOutputStream dos = null;
		try {
			is = getInputStream();
			dos = new DataOutputStream(os = getOutputStream());
			
			System.out.printf("Connected IP : %s, Port : %d\n", socket.getInetAddress(), socket.getPort());
			this.socket.setSoTimeout(5000);
			
			ByteReader br = new ByteReader();
			int size = br.read(is);
			
			
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		
		
	}
}
