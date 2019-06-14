package get;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * webRoot
 * 		getSettings()에서 초기화된다.
 * defaultScreen
 * 		getSettings()에서 초기화된다. AWTException이 발생할 경우, default로 설정되는 스샷이다.
 * screen
 * 		filePath아님. screen샷을 찍을 경우 파일명이다.
 * process
 * 		cmd 실행을 위한 객체
 * processBuilder
 * 		cmd 실행을 위한 객체
 * baos
 * 		cmd에서 읽은 byte를 담는 stream
 * instructorType
 * 		입력된 명령어로 초기화됨.
 * response
 * 				
 * String detectWords(String words) 
 * boolean isWords(String words)
 * String notDefinedInsturctor(String words)
 * String getResponse(String words)
 * String buildCmd(String words)
 * void writeCmd(String words)
 * int readCmd(InputStream is, OutputStream os)
 * String getString()
 * byte[] getBytes()
 * String buildScreen(String words)
 * void getSettings()
 * String getWebRoot()
 * String getDefaultScreen()
 * String makeHtml()
 * void doGet(HttpServletRequest request, HttpServletResponse response)
 * 
 * @author hkj
 *
 */
//@WebServlet("/Server")
public class GetWords extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String webRoot;
	private String defaultScreen;
	
	private String screen;
	private Process process;
	private ProcessBuilder processBuilder;
	private ByteArrayOutputStream baos;
	private String instructorType;
	private String response;
	
    public GetWords() {
        super();
      
    }
    
    /**
     * index.jsp에서 GET 요청으로 온 string값을 받아서, 그 값에 따른 결과를 String으로 리턴해준다.
     * 정해진 명령어가 아닌 경우에는 그 자신을 리턴한다.
     * 정해진 명령어가 입력된 경우에는 명령어에 따라 다르게 동작하고, 그에 따른 String을 리턴해준다.
     * @param words
     * @return 입력값에 따라 입력값을 혹은 다른 값을 리턴해준다.
     */
    private String detectWords(String words) 
    {
    	if(words == null)
    		throw new java.lang.NullPointerException("detectWords() string is null");
    	
    	if(isWords(words)) 
    	{
    		return getResponse(words);
    	}
    	else
    	{	
    		return this.notDefinedInsturctor(words);
    	}
    }
    
    /**
     * 정의되지 않은 명령어를 처리하는 메서드이다.
     * 들어온 words를 instructorType, response에 넣어주고
     * 그 값을 그대로 반환한다.
     * @param words
     * @return words
     */
    private String notDefinedInsturctor(String words) {
    	if(words == null)
    		throw new java.lang.NullPointerException("notDefindedInstructor() words is null");
    	this.instructorType = words;
    	this.response = words;
    	return words;
    }
    
    /**
     * 입력받은 String이 정해진 명령어에 해당되면 true, 아니면 false 반환하는 메소드
     * @param words
     * @return 입력 값이 dir, notepad, screen에 해당되면 true. 나머지는 false 반환.
     */
    private boolean isWords(String words)
    {
    	if(words == null)
    		throw new java.lang.NullPointerException("detectWords() string is null");
    	
    	words = words.toLowerCase();
    	String[] instructors = {"dir", "notepad", "screen"};
    	
    	for(int i = 0; i < instructors.length; i++) 
    	{
    		if(instructors[i].equals(words))
    			return true;
    	}
    	
    	return false;
    }
    
    /**
     * 입력된 String(명령어)에 따른 각각의 메소드를 실행하고, 이 결과를 리턴해준다.
     * @param words
     * @return response
     */
    private String getResponse(String words){
    	if(words == null) 
    		throw new java.lang.NullPointerException("getResponse() s is null");
    	
    	switch(words) 
    	{
    		case "dir":
    			this.response = buildCmd(words);
    			break;
    		case "notepad":
    			this.response = buildCmd(words);
    			break;
    		case "screen":
    			response = buildScreen(words);
    			makeScreen(words); 
    			break;
    		default:
    			throw new java.lang.IllegalArgumentException();
    	}
    	return this.response;
    }
    
    /**
     * readCmd()를 통해 읽은 byte를 String으로 바꿔 반환한다.
     * @param words
     * @return baos에 저장된 값을 String으로 반환
     */
    private String buildCmd(String words) {
    	if(words == null)
    		throw new java.lang.NullPointerException("buildCmd() words is null");
    	
    	try 
    	{
    		this.processBuilder = new ProcessBuilder("cmd");
    		this.process = this.processBuilder.start();
    		this.processBuilder.redirectErrorStream(true);
    		this.writeCmd(words);
    		
    		this.baos = new ByteArrayOutputStream();
    		this.readCmd(this.process.getInputStream(), this.baos);
    	}
    	catch(IOException ioe)
    	{	
    		//예외 처리 필요
    		ioe.printStackTrace(System.out);
    	}
    	finally
    	{
    		processBuilder = null;
    		process = null;
    	}
    	
    	this.instructorType = words;
    	return this.getString();
    }
    
    /**
     * cmd에 먼저 dir, notepad등 명령어를 쏴주기 위해 DataOutputStream을 이용한다.
     * 
     * @throws IOException
     */
    private void writeCmd(String words) throws IOException{
		DataOutputStream dos = new DataOutputStream(this.process.getOutputStream());
		dos.writeBytes(words + "\r\n");
		dos.flush();
		dos.close();
	}
    
    /**
     * cmd 창의 byte를 읽어서 this.baos에 저장.
     * 
     * @param is process의 InputStream
     * @param os byteArrayOutputStream
     * @return 읽은 size 반환
     * @throws IOException
     */
    private int readCmd(InputStream is, OutputStream os) throws IOException {
    	if(is == null || os == null)
    		throw new java.lang.IllegalArgumentException();
    	
    	this.baos.reset();
    	int n = 0;
		int offset = 0;
		int size = 0;
		byte[] checkBuffer = {13, 10, 13, 10};
		
		while(true) {
			n = is.read();
			if(n < 0)
				break;
			if(checkBuffer[offset] == n) {
				offset++;
				if(offset == checkBuffer.length) {
					size++;
					os.write(n);
					offset = 0;
					break;
				}
			} else {
				offset = 0;
			}
			size++;
			os.write(n);
		}
		
		checkBuffer = null;
		int index = 0;
		boolean finishStorePath = false;
		byte[] temp = new byte[100];
		byte[] path = null;
		
		while(true) {
			n = is.read();
			if(n < 0)
				break;
			if(!finishStorePath)
				temp[index] = (byte)n;
			if(!finishStorePath && n == 62) {
				finishStorePath = true;
				path = new byte[index + 1];
				System.arraycopy(temp, 0, path, 0, index + 1);
				size++;
				os.write(n);
				temp = null;
				offset = 0;
				continue;
			}
			if(finishStorePath) {
				if(path[offset] == n) {
					offset++;
					if(offset == path.length) {
						size++;
						os.write(n);
						break;
					}
				} else {
					offset = 0;
				}
			}
			
			index++;
			size++;
			os.write(n);
		}
		
		path = null;
		return size;
    }
    
    /**
     * readCmd를 한 후에 baos가 초기화됨.
     * 즉, readCmd 후에 사용가능.
     * <DIR> 을 [DIR]로 변경해주는 작업을 해주는데, 여기에서 리소스 많이 써먹을 거 같음.
     * @return this.baos
     */
    public String getString() 
    {
    	String tempString;
    	byte[] tempByte;
    	try
    	{
    		//System.out.println("x-windows-949");
    		tempString = this.baos.toString("MS949");
    		//System.out.println(tempString);
    		//tempByte = tempString.getBytes("utf-8");
    		//System.out.println(new String(tempByte, "utf-8").replaceAll("<DIR>", "[DIR]"));
    		//return  new String(tempByte,"utf-8").replaceAll("<DIR>", "[DIR]");
    		return  tempString.replace("<", "&lt;").replace(">", "&gt;");
    	}
    	catch(Exception use) 
    	{
    		tempString = this.baos.toString();
    		return tempString.replaceAll("<DIR>", "[DIR]");
    	}
    }
    
    /**
     * readCmd를 한 후에 baos가 초기화됨.
     * 즉, readCmd 후에 사용가능.
     * @return this.baos
     */
    public byte[] getBytes()
    {
    	return this.baos.toByteArray();
    }
    
    /**
     * screen 명령어가 입력된 경우에 실행되는 메서드.
     * screenshot의 filePath는  webRoot/temp/yyyy_MM_dd_kkmmss.bmp 이다. 
     * AWTException이 발생한다면, 기본 파일의 path를 반환한다.
     * @return this.screen 	찍은 screenshot의 filePath를 반환한다.
     */
    private String buildScreen(String words){
    	if(words == null)
    		throw new java.lang.NullPointerException("buildScreen() words is null");
    	Date captureTime = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_kkmmss");
    	StringBuilder filePath;
    	
    	try 
    	{
    		filePath = new StringBuilder(this.getWebRoot());
    		filePath.append("/temp/").append(sdf.format(captureTime)).append(".bmp");
    		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    		Robot robot = new Robot();
    		BufferedImage capture = robot.createScreenCapture(screenRect);
    		this.screen = sdf.format(captureTime) + ".bmp";
    		ImageIO.write(capture, "bmp", new File(filePath.toString()));
    	}
    	catch(IOException ioe) 
    	{
    		// 예외 처리 필요
    		ioe.printStackTrace(System.out);
    	}
    	catch (AWTException awte)
    	{
    		filePath = new StringBuilder(this.getWebRoot());
    		filePath.append("/temp/").append(this.getDefaultScreen());
    		this.screen = this.getDefaultScreen();
    	}    	
    	
    	this.instructorType = words;
    	return this.screen;    	
    }
    
    /**
     * screen 명령어가 입력된 상황에서 실행됨.
     * 스크린샷을 파일로 생성하지 않고 바로 outputstream으로 전송한다.
     * @param words
     * @return
     */
    private void makeScreen(String words){
    	try 
    	{
    		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    		Robot robot = new Robot();
    		BufferedImage capture = robot.createScreenCapture(screenRect);
    		this.baos = new ByteArrayOutputStream();
    		this.baos.reset();
    		ImageIO.write(capture, "bmp", this.baos);
    	}
    	catch(IOException ioe) 
    	{
    		// 예외 처리 필요
    		ioe.printStackTrace(System.out);
    	}
    	catch (AWTException awte)
    	{
    		//
    	}    	
    	
    	this.instructorType = words; 	
    }
    
    /**
     * info.properties에서 설정값을 가져오는 메서드
     * doGet()에서 다른 메서드에 앞서 실행된다.
     * 현재는 webRoot, defaultScreen의 값만 가져온다.
     */
    private void getSettings() {	
    	StringBuilder webPath = new StringBuilder(getServletContext().getRealPath("/"));
        
    	try(FileInputStream fis = new FileInputStream(webPath.append("/info.properties").toString()))
    	{
    		Properties info = new Properties();
    		info.load(fis);
    		this.webRoot = info.getProperty("WEBROOT");
    		//this.webRoot = webPath.toString();
    		this.defaultScreen = info.getProperty("DEFAULTSCREEN");
    	} 
    	catch(IOException ioe)
    	{
    		//예외 처리 필요
    		ioe.printStackTrace(System.out);
    	}
    }
    
    /**
     * getSetting()에서 설정한 webRoot 반환
     * @return webRoot
     */
    public String getWebRoot() 
    {
    	return this.webRoot;
    }
    
    /**
     * getSetting()에서 설정한 defaultScreen 반환
     * @return defaultScreen
     */
    public String getDefaultScreen() 
    {
    	return this.defaultScreen;
    }
    
    /**
     * screen 명령어가 입력될 경우 / 나머지 모든 명령어가 입력될 경우
     * 출력값이 두 개로 나뉜다.
     * 
     * @return html. html을 String 형식으로 보내준다.
     */
    private String makeHtml() {
    
    	StringBuilder html = new StringBuilder("<!DOCTYPE html>\r\n");
    	html.append("<html>\r\n");
    	html.append("<head>\r\n");
    	html.append("<title>GetWords</title>\r\n");
    	html.append("<meta charset=\"utf-8\">\r\n");
    	html.append("<link rel=\"stylesheet\" href=\"");
    	html.append(this.webRoot + "/css/styles.css\" type=\"text/css\">\r\n");
    	html.append("<script src=\"https://code.jquery.com/jquery-1.11.1.js\"></script>\r\n");
    	html.append("</head>\r\n");
    	html.append("<body>\r\n");
    	
    	if("screen".equals(this.instructorType))
    	{
    		StringBuilder webPath = new StringBuilder(getServletContext().getRealPath("/")).append("temp\\");
    		
    		html.append("<img src=\"").append(webPath).append(this.screen).append("\" class=\"playerPic\">\r\n");
    	}
    	else
    	{
    		html.append("<pre>").append(this.response).append("</pre>\r\n");
    	}

    	html.append("</body>\r\n");
    	html.append("</html>\r\n");
    	
    	return html.toString();
    }
    
    /**
     * doGet()에서 브라우저로 response를 보내주기 위해서 사용하는 메소드
     * 바이트로 이미지를 보내거나 String을 보낸다.
     * @param response
     */
    private void sendResponse(HttpServletResponse response) {
    	if(response == null)
    		throw new java.lang.NullPointerException("sendResponse() response is null");
    	    	
    	try 
    	{
    		if("screen".equals(this.instructorType)) 
    		{
    			response.setContentType("image/bmp");
    			byte[] temp = this.getBytes();
    			response.getOutputStream().write(temp, 0, temp.length);
    		}
    		else
    		{
    			response.setContentType("text/html; charset=UTF-8");
    			response.setCharacterEncoding("UTF-8");
    			response.getWriter().print(this.makeHtml());
    			//dos.writeBytes(this.makeHtml());
    			//DataOutputStream 함부로 쓰면 안 됨. response를 이용해서 응답하게.
    		}
    	}
    	catch(IOException ioe)
    	{
    		// 예외처리 필요
    	}
    	
    	
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		this.getSettings();
		this.detectWords(request.getParameter("words"));
		
		//PrintWriter out = response.getWriter();
		//out.println(this.makeHtml());
		//out.close();
		this.sendResponse(response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//
	}

}
