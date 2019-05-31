package server;
import java.net.Socket;

import parsing.ByteProcessing;
import reader.ByteHeaderReader;
import parsing.RequestParser;
import build.ResponseBuilder;
import file.FileRead;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.File;

public class NewServerThread extends Thread{
	private String default_path;
	private String home;
	private Socket socket;
	private byte[] requestBuffer;
	private String response;
	private File file;
	
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
	 * IOException ó�� ���ؼ� ����
	 * 
	 * @return
	 * @throws IOException
	 */
	private InputStream getInputStream() throws IOException {
		return this.socket.getInputStream();
	}
	/**
	 * IOException ó�� ���ؼ� ����
	 * 
	 * @return
	 * @throws IOException
	 */
	private OutputStream getOutputStream() throws IOException {
		return this.socket.getOutputStream();
	}
	/**
	 * WebRoute ��ȯ
	 * @return
	 */
	public String getWebRoute() {
		return this.default_path;
	}
	/**
	 * thread run()
	 * byte�� �д� �۾� -> ByteHeaderReader
	 * ���� byte�� ó���ϴ� �۾� -> ByteProcessing
	 * ó���� byte���� ��� ������ �̾ƿ��� �۾� -> RequestParser
	 * 		cmd�� �ٷ�� �۾� -> CmdReader
	 * response�� ����� �۾� -> ResponseBuilder
	 * client�� response�� �Բ� ���� ������ �д� �۾� -> FileRead
	 */
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
			
			ByteHeaderReader bhr = new ByteHeaderReader();
			int size = bhr.read(is);
			this.requestBuffer = bhr.getBytes();
			
			ByteProcessing bp = new ByteProcessing(this.requestBuffer);
			bp.processBytes();
			
			RequestParser rp = new RequestParser(bp.getMap());
			rp.setMethodUrl(bp.getMethod(), bp.getUrl());
			rp.setHome(this.home);
			rp.setDefaultPath(this.default_path);
			rp.parsing();
			
			ResponseBuilder rb = new ResponseBuilder(rp.getRequestHeaders());
			rb.setUrl(rp.getUrl());
			rb.setFile(rp.getFile());
			rb.setCode(rp.getCode());
			rb.build();
			this.response = rb.getResponse();
			dos.writeBytes(response);
			
			FileRead fr = new FileRead(rp.getFile());
			fr.fread();
			dos.write(fr.getFileData(), 0, fr.getFileLength());
			dos.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
