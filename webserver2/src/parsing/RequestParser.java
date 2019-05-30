package parsing;
import java.io.File;
import java.util.Map;

import javax.imageio.ImageIO;

import java.util.HashMap;
import java.lang.String;
import java.util.Map;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import java.util.Date;
import java.text.SimpleDateFormat;

public class RequestParser{
	
	private Map<String, String> requestHeaders;
	private String code;
	private String method;
	private File filePath;
	private String url;
	private String default_path;
	private String home;
	
	public RequestParser(Map<String, String> reqheaders){
		if(reqheaders == null)
			throw new java.lang.NullPointerException("reqheaders is null");
		this.requestHeaders = reqheaders;
	}
	/**
	 * @return request ����� Map���� ��ȯ�Ѵ�
	 */
	public Map<String, String> getRequestHeaders(){
		return this.requestHeaders;
	}
	/**
	 * setMethodUrl(method, url)�� ���� �ʱ�ȭ�ȴ�.
	 * @return url�� String���� ��ȯ�Ѵ�.
	 */
	public String getUrl(){
		return this.url;
	}
	/**
	 * parsing()�� ���� �ʱ�ȭ�ȴ�.
	 * @return HTTP ���� �ڵ带 ��ȯ�Ѵ�. ex)200, 304, 404..
	 */
	public String getCode(){
		return this.code;
	}
	/**
	 * setMethodUrl(method, url)�� ���� �ʱ�ȭ�ȴ�.
	 * @return ���� request�� method�� ��ȯ�Ѵ�.
	 */
	public String getMethod(){
		return this.method;
	}
	/**
	 * setMethodUrl(method, url)�� ���� �ʱ�ȭ�ȴ�.
	 * @return url�� File�� ��ȯ�Ѵ�.
	 */
	public File getFilePath(){
		return this.filePath;
	}
	/**
	 * method, url�� �Է¹޾� this.method, this.url�� �ʱ�ȭ�Ѵ�.
	 * @param method
	 * @param url
	 */
	public void setMethodUrl(String method, String url){
		if(method == null)
			throw new java.lang.NullPointerException("method is null");
		if(url == null)
			throw new java.lang.NullPointerException("url is null");
		this.method = method;
		this.url = url;
	}
	/**
	 * this.default_path�� �ʱ�ȭ�Ѵ�.
	 * @param default_path
	 */
	public void setDefaultPath(String default_path) {
		if(default_path == null)
			throw new java.lang.NullPointerException("default_path is null");
		this.default_path = default_path;
	}
	/**
	 * setDefaultPath�� �ʱ�ȭ�� ���Ŀ� ��� ����.
	 * @return default_path ��ȯ
	 */
	public String getDefaultPath(){
		return this.default_path;
	}
	/**
	 * url�� / �� ���, ȭ�鿡 ����� html������ �̸��� �����Ѵ�.
	 * @param home
	 */
	public void setHome(String home) {
		if(home == null)
			throw new java.lang.NullPointerException("home is null");
		this.home = home;
	}
	/**
	 * setHome(home)���� �ʱ�ȭ�ؾ� ��밡��
	 * @return this.home ����
	 */
	public String getHome() {
		return this.home;
	}
	private String dirMethod() throws IOException{
		ProcessBuilder pb = new ProcessBuilder("cmd");
		pb.redirectErrorStream(true);
		Process p = pb.start();
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		bw.write("dir\n");
		bw.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String outputLine = "";
		StringBuilder outputMessage = new StringBuilder();
		while((outputLine = br.readLine()) != null) {
			outputMessage.append(outputLine).append("\r\n");
			System.out.print(outputLine);
		}
		System.out.print(outputMessage);
		return outputMessage.toString();
	}
	private void screenMethod(String filePath) throws AWTException, IOException{
		if(filePath == null)
			throw new java.lang.NullPointerException("screenMethod. filePath is null");
		
		Date captureTime = new Date();
		StringBuilder filepath = new StringBuilder(filePath);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_kkmmss");
		filepath.append(sdf.format(captureTime)).append(".bmp");

		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Robot robot = new Robot();
		BufferedImage capture = robot.createScreenCapture(screenRect);
		ImageIO.write(capture, "bmp", new File(filepath.toString()));
	}
	/**
	 * setMethodUrl() ���Ŀ� ����� �����ϴ�.
	 * setMethodUrl()���� this.method, this.url, this.filePath�� �ʱ�ȭ �Ǳ� �����̴�.
	 * setDefaultPath() ���Ŀ� ��밡��. �������� ������.
	 * @return isError  error -> true, �ƴϸ� false ��ȯ
	 */
	public void parsing(){
		boolean isError = false;
		try{
			if(!"GET".equals(this.getMethod()) || !"POST".equals(this.getMethod())) {
				this.code = "405";
				isError = true;
			}

			if(!isError && this.url.contains(" ")){
				this.code = "400";
				isError = true;						
			}

			
			if(!isError && this.url.startsWith("/")){
				if(this.url.length() > 1){
					this.url = this.getDefaultPath() + this.url.substring(1);
					this.filePath = new File(this.getUrl());
				} else {
					this.url = this.getDefaultPath() + this.getHome();
					this.filePath = new File(this.getUrl());
				}
			} else if(!isError && !this.url.startsWith("/")){
					this.code = "400";
					isError = true;
			}
			
			if(!isError && "dir".equals(this.url)) {
				
			} else if(!isError && "screen".equals(this.url)) {
				
			} else if(!isError && "notepad".equals(this.url)) {
				
			}
		
			
			if(!isError && this.filePath.exists()){
				if((this.requestHeaders.get("if-none-match") != null) && this.requestHeaders.get("if-none-match").equals("\""+Long.toString(this.filePath.lastModified()) + "\"")){
					this.code = "304";
				} else {
					this.code = "200";
				}
			} else if(!isError && !this.filePath.exists()){
				this.url = "fileNotFound";
				this.filePath = new File(this.getUrl());
				this.code = "404";
			}

		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
}