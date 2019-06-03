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
	 * WebRoute 반환
	 * @return
	 */
	public String getWebRoute() {
		return this.default_path;
	}
	
	/**
	 * <변수>
	 * url
	 * 		request 첫 라인에 주어지는 url을 의미. 
	 * default_path
	 * 		info.properties에 설정한 기본 경로
	 * home
	 * 		info.properties에 설정한 기본 html파일
	 * response
	 * 		socket과 연결된 OutputStream으로 보낼 String.
	 * 		response 헤더가 저장됨.
	 * file
	 * 		socket과 연결된 OutputStream으로 보낼 file
	 * requestBuffer
	 * 		request를 저장한 버퍼
	 * html
	 * 		cmd, screenshot 요청이 올 경우에 생성해주는 html파일의 path. String임
	 */
	/**
	 * thread run()
	 * 	|-	byte를 읽는 작업 -> ByteHeaderReader
	 * 	|-	읽은 byte를 처리하는 작업 -> ByteProcessing
	 * 	|-	처리된 byte에서 헤더 정보를 뽑아오는 작업 -> RequestParser
	 * 	|		cmd를 다루는 작업 -> CmdReader
	 * 	|-	response를 만드는 작업 -> ResponseBuilder
	 * 	|-	socket OutputStream에 response와 함께 보낼 파일을 읽는 작업 -> FileRead
	 * 	ㅗ 	클라이언트 소켓으로 response, file 전송
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