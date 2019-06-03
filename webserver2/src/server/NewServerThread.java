package server;
import parsing.ByteProcessing;
import reader.ByteHeaderReader;
import parsing.RequestParser;
import build.ResponseBuilder;
import file.FileRead;
import file.MakeHTML;

import java.net.Socket;
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
	 * WebRoute ��ȯ
	 * @return
	 */
	public String getWebRoute() {
		return this.default_path;
	}
	
	/**
	 * <����>
	 * url
	 * 		request ù ���ο� �־����� url�� �ǹ�. 
	 * default_path
	 * 		info.properties�� ������ �⺻ ���
	 * home
	 * 		info.properties�� ������ �⺻ html����
	 * response
	 * 		socket�� ����� OutputStream���� ���� String.
	 * 		response ����� �����.
	 * file
	 * 		socket�� ����� OutputStream���� ���� file
	 * requestBuffer
	 * 		request�� ������ ����
	 * html
	 * 		cmd, screenshot ��û�� �� ��쿡 �������ִ� html������ path. String��
	 */
	/**
	 * thread run()
	 * 	|-	byte�� �д� �۾� -> ByteHeaderReader
	 * 	|-	���� byte�� ó���ϴ� �۾� -> ByteProcessing
	 * 	|-	ó���� byte���� ��� ������ �̾ƿ��� �۾� -> RequestParser
	 * 	|		cmd�� �ٷ�� �۾� -> CmdReader
	 * 	|-	response�� ����� �۾� -> ResponseBuilder
	 * 	|-	socket OutputStream�� response�� �Բ� ���� ������ �д� �۾� -> FileRead
	 * 	�� 	Ŭ���̾�Ʈ �������� response, file ����
	 */
	public void run() {
		System.out.println("Thread Created!");
		
		InputStream is = null;
		OutputStream os = null;
		DataOutputStream dos = null;
		try {
			is = socket.getInputStream();
			dos = new DataOutputStream(socket.getOutputStream());
			System.out.printf("Connected IP : %s, Port : %d\n", socket.getInetAddress(), socket.getPort());
			//this.socket.setSoTimeout(5000);
			
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
			FileRead fr = null;		
			StringBuilder html = null;
			
			if(rp.getUrl().indexOf("?") != -1) {
				html = new StringBuilder(this.default_path);
				html.append("/temp.html");
				this.file = new File(html.toString());
				rb.setFile(this.file);
				MakeHTML mh = null;
				
				if(rp.getWordType().equals("cmd")) {
					mh = new MakeHTML(html.toString(), rp.getResultCmd());
				} else if(rp.getWordType().startsWith("screen")) {
					mh = new MakeHTML(html.toString());
					mh.setScreen(rp.getScreen());
				} else {
					mh = new MakeHTML(html.toString());
				}
				
				rb.setUrl(html.toString());
				mh.setUrl(rp.getUrl());
				mh.make();
				fr = new FileRead(mh.getFile());
				mh = null;
			} else {
				rb.setFile(rp.getFile());
				rb.setUrl(rp.getUrl());
				fr = new FileRead(rp.getFile());
			}
			
			rb.setCode(rp.getCode());
			rb.build();
			this.response = rb.getResponse();
			dos.writeBytes(this.response);
			
		
			fr.fread();
			
			dos.write(fr.getFileData(), 0, fr.getFileLength());
			dos.flush();
			dos.close();
			is.close();
			html = null;
			is = null;
			dos = null;
			bp = null;
			rp = null;
			rb = null;
			fr = null;
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}
}