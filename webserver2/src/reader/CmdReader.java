package reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Process;
import java.lang.ProcessBuilder;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Date;
import java.text.SimpleDateFormat;

public class CmdReader{
	private ByteArrayOutputStream baos;
	private ProcessBuilder pb;
	private Process p;
	private StringBuilder url;
	private String default_path;
	private String wordType;
	private String screen;
	public static final String exceptionType = "hi";
	public static final String cmdType = "cmd";
	public static final String screenType = "screen";
	
	public CmdReader() {
		this.baos = new ByteArrayOutputStream();
	}
	/**
	 * url을 받아 cmd에 입력할 값을 초기화.
	 * url은 /?commandText=dir처럼 /?commandText= 으로 시작된다.
	 * @param url
	 * @param default_path
	 * @throws IOException
	 */
	public CmdReader(String url, String default_path) throws IOException{
		if (url == null)
			throw new java.lang.NullPointerException("CmdReader url is null");
		if (default_path == null)
			throw new java.lang.NullPointerException("CmdReader default_path is null");
		this.baos = new ByteArrayOutputStream();
		this.url = new StringBuilder(url.substring(14)).append("\r\n");
		this.default_path = default_path;
	}
	
	/**
	 * 생성자에서 parameter로 받은 url이 반환된다. 
	 * @return url
	 */
	public String getUrl() {
		return this.url.toString();
	}	
	/**
	 * 입력된 type을 반환한다.
	 * dir, notepad, screen 이 있다.
	 * @return wordType
	 */
	public String getWordType() {
		return this.wordType;
	}
	
	/**
	 * url로 screen이 왔을 경우에만 초기화된다.
	 * execute()를 통해 초기화 된다.
	 * @return screen
	 */
	public String getScreen() {
		return this.screen;
	}
	
	/**
	 * 무조건 execute()를 해야지 wordType, baos, screen의 값이 초기화된다.
	 * @throws IOException
	 * @throws AWTException
	 */
	public void execute() throws IOException, AWTException{
		if(this.getUrl().startsWith("dir") || this.getUrl().startsWith("notepad")){
			pb = new ProcessBuilder("cmd");
			p = pb.start();
			pb.redirectErrorStream(true);
			this.wordType = cmdType;
			this.executeCmd();
		} else if(this.getUrl().startsWith("screen")) {
			this.screenMethod();
			this.wordType = screenType;
		} else {
			this.wordType = exceptionType;
		}
	}
	/**
	 * url로 screen이 왔을 경우, 스크린샷을 찍는 메소드.
	 * 
	 * 
	 * @return screen
	 * @throws AWTException
	 * @throws IOException
	 */
	private String screenMethod() throws AWTException, IOException{
		Date captureTime = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_kkmmss");
		StringBuilder filepath = new StringBuilder(this.default_path);
		filepath.append("/temp/").append(sdf.format(captureTime)).append(".bmp");
		Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
		Robot robot = new Robot();
		BufferedImage capture = robot.createScreenCapture(screenRect);
		this.screen = filepath.toString();
		ImageIO.write(capture, "bmp", new File(this.screen));
		
		return this.screen;
	}
	/**
	 * execute() -> executeCmd() -> cmdUrlWrite() -> read() -> cmdRead() : cmd를 이용하는 경우
	 * 			 -> screenMethod()										 : screenshot을 이용하는 경우
	 * @return
	 * @throws IOException
	 */
	public int executeCmd() throws IOException{
		
		this.cmdUrlWrite();
		return this.read(this.p.getInputStream());
	}
	
	/**
	 * executeCmd()에서 사용된다.
	 * @throws IOException
	 */
	private void cmdUrlWrite() throws IOException{
		DataOutputStream dos = new DataOutputStream(p.getOutputStream());
		dos.writeBytes(this.url.toString());
		dos.flush();
		dos.close();
	}
	
	/**
	 * 
	 * @param is
	 * @return this.cmdRead(is, this.baos)
	 * @throws IOException
	 */
	public int read(InputStream is) throws IOException{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		this.baos.reset();
		return this.cmdRead(is, this.baos);
	}
	/**
	 * cmd에서 읽은 바이트 수를 반환한다.
	 * 
	 * @param is
	 * @param os
	 * @return size
	 * @throws IOException
	 */
	public int cmdRead(InputStream is, OutputStream os) throws IOException{
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
	 * 
	 * @return read(is,os)에서 저장한 ByteArrayOutputStream의 값을 byte[]로 반환한다.
	 */
	public byte[] getBytes() {
		return this.baos.toByteArray();
	}
	/**
	 * 
	 * @return read(is,os)에서 저장한 ByteArrayOutputStream의 값을 String으로 반환한다.
	 * @throws UnsupportedEncodingException
	 */
	public String getString() throws UnsupportedEncodingException{
		return this.baos.toString("utf-8");
	}
}
