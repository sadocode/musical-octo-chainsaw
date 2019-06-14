package parsing;

import reader.CmdReader;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.io.IOException;
import java.io.File;
import java.lang.String;

public class RequestParser2 {
	private byte[] request;
	private String webRoot;
	private String home;
	private String method;
	private StringBuilder url;
	private String protocol;
	private String boundary;
	private Map<String, String> requestHeaders;
	private String code;
	private File file;
	private boolean isError;
	
	private String screen;
	private String resultCmd;
	private String wordType;
	
	/**
	 * Socket으로 부터 온 byte를 this.request에 복사
	 * requestHeaders 초기화
	 * default_path, home 초기화
	 * @param request
	 */
	public RequestParser2(byte[] request, String webRoot, String home) 
	{
		if(request == null || request.length == 0)
			throw new java.lang.NullPointerException("request parameter of ByteProcessing constructor is null");
		this.request = new byte[request.length];
		System.arraycopy(request, 0, this.request, 0, request.length);
		this.requestHeaders = new HashMap<>();
		this.isError = false;
		this.webRoot = webRoot;
		this.home = home;
	}
	
	/**
	 * process()가 완료되어야 method가 초기화된다.
	 * @return 정상적인 값이라면, GET, POST 중에서 리턴해준다. 
	 */
	public String getMethod() 
	{
		return this.method;
	}
	
	/**
	 * process()가 완료되어야 url이 초기화된다.
	 * @return 요청된 url의 값이 String으로 리턴된다.
	 */
	public String getUrl() 
	{
		return this.url.toString();
	}
	
	/**
	 * process()가 완료되어야 protocol이 초기화됨.
	 * @return HTTP/1.1 또는 HTTP/1.0, HTTP/2 가 리턴된다.
	 */
	public String getProtocol() 
	{
		return this.protocol;
	}
	
	/**
	 * process()가 완료되어야 boundary가 초기화됨.
	 * @return POST의 경우에만 정상적인 boundary값이 리턴된다.
	 */
	public String getBoundary() 
	{
		return this.boundary;
	}
	
	/**
	 * process()가 완료되어야 requestHeaders가 초기화된다.
	 * @return request의 헤더 정보가 들어간 Map을 리턴해준다.
	 */
	public Map<String, String> getRequestHeaders()
	{
		return this.requestHeaders;
	}
	
	/**
	 * parse()을 통해 초기화된다.
	 * @return HTTP 상태 코드를 반환한다. ex)200, 304, 404..
	 */
	public String getCode(){
		return this.code;
	}
	
	/**
	 * parse()를 통해 초기화된다.
	 * @return url을 File로 반환한다.
	 */
	public File getFile(){
		return this.file;
	}
	
	/**
	 * parse()에서 초기화되는 isError 값을 반환한다.
	 * @return isError
	 */
	public boolean getIsError() {
		return this.isError;
	}
	
	/**
	 * <변수>
	 * index		계산용 임시 변수
	 * indexStart	request에서 lineBuffer로 복사할 때, 새 라인이 시작하는 index
	 * readLine		lineBuffer를 String으로 변환한 값. 헤더 한 라인에 해당한다
	 * newLine		새 라인이 시작되면 newLine이 true가 되고, arraycopy를 한다.
	 * firstLine	lineBuffer가 request의 첫 줄일 경우만 true 체크
	 * byteCheck	lineBuffer의 크기
	 * reqH1		request 헤더의 헤더명
	 * reqH2		request 헤더의 값
	 * 
	 * this.request의 바이트를 라인마다 String으로 분리하고, header값 별로
	 * 정리해서 requestHeaders에 소문자로 저장한다. 
	 * method, url, protocol도  저장한다.
	 * 
	 * 첫 라인일 경우 firstLineParsing(readLine)메소드 실행.
	 * 헤더에 boundary가 있으면, body를 읽으러 간다.(구현 X)
	 * 
	 */
	public void process() 
	{
		try 
		{
			int index = 0;
			int indexStart = 0;
			String readLine = "";
			boolean newLine = false;
			boolean firstLine = true;
			int byteCheck = 0;
			byte[] lineBuffer = null;
			String reqH1 = null;
			String reqH2 = null;
			for(int i = 0; i < this.request.length; i++) 
			{
				if(newLine == true) 
				{
					lineBuffer = new byte[byteCheck];
					System.arraycopy(this.request, indexStart, lineBuffer, 0, byteCheck);
					readLine = new String(lineBuffer, "utf-8");
					
					if(firstLine == true) 
					{
						this.firstLineProcess(readLine);
						firstLine = false;
					}
					
					if(-1 != (index = readLine.indexOf(":")))
					{
                        reqH1 = readLine.substring(0, index).toLowerCase();
                        reqH2 = readLine.substring(index+2).toLowerCase();
                        this.requestHeaders.put(reqH1, reqH2);
                        reqH1 = null;
                        reqH2 = null;
                    }
					
					if(-1 != (index = readLine.indexOf("boundary="))) 
					{
                        index += 9;
						this.boundary = "--" + readLine.substring(index);
                        this.requestHeaders.put("boundary", this.getBoundary());
					}
					
					/*
					 *post요청이 왔을 경우!
					if(readLine.equals(this.boundary)){
						byte[] bodyBuffer = new byte[this.request.length - indexStart];
						System.arraycopy(this.request, indexStart, bodyBuffer, 0, bodyBuffer.length);
						rb = new ReadBody(lineByte, this.boundary);
						break;
	                }
					*/
					
					indexStart += byteCheck;
					byteCheck = 0;
					lineBuffer = null;
					readLine = null;
				}
				
				newLine = false;
				byteCheck++;
				
				if(this.request[i] == 13) 
				{
					if(i+1 == request.length) 
					{
						throw new java.lang.IndexOutOfBoundsException();
					}
					if(this.request[i+1] == 10) 
					{
						newLine = true;
						byteCheck++;
						i++;
					}	
				}
			}
			
		} 
		catch(Exception e) 
		{
			e.printStackTrace(System.out);
		}
	}
	
	/**
	 * 
	 * 
	 */
	public void parse() 
	{
		try 
		{
			if(!("GET".equals(this.getMethod()) || "POST".equals(this.getMethod()))) 
			{
				this.code = "405";
				this.isError = true;
				return;
			}
			
			if(this.url.indexOf(" ") != -1)
			{
				this.code = "400";
				this.isError = true;	
				return;
			}
			
			if(this.getUrl().startsWith("/"))
			{
				if(this.getUrl().length() > 1)
				{
					if(this.getUrl().charAt(1) != '?') 
					{
						this.url.insert(0, this.webRoot);
						this.file = new File(this.getUrl());
					}
				} 
				else 
				{
					this.url = new StringBuilder(this.webRoot).append("/").append(this.home);
					this.file = new File(this.getUrl());
				}
			} 
			else 
			{
				this.code = "400";
				this.isError = true;
				return;
			}
			
			if(this.getUrl().startsWith("/?commandText=")) 
			{
				CmdReader cr = new CmdReader(this.getUrl(), this.webRoot);
				cr.execute();
				
				if(cr.getWordType().equals("screen")) 
				{
					this.screen = cr.getScreen();
				} 
				else if(cr.getWordType().equals("cmd")) 
				{
					this.resultCmd = cr.getString();
				}
				
				this.wordType = cr.getWordType();
				this.code = "200";
				cr = null;
				return;
			}
			
			if(this.file.exists())
			{
				if((this.requestHeaders.get("if-none-match") != null) && this.requestHeaders.get("if-none-match").equals("\""+Long.toString(this.file.lastModified()) + "\""))
				{
					this.code = "304";
				} 
				else 
				{
					this.code = "200";
				}
			} 
			else 
			{
				this.url = new StringBuilder("fileNotFound");
				this.file = new File(this.getUrl());
				this.code = "404";
				this.isError = true;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);			
		}
	}
	
	/**
	 * request의 첫 라인을 읽고, method, url, protocol을 초기화해준다.
	 * @param readLine process()에서 처음 생성한 문장이다.
	 * @throws IOException StringTokenizer에 값을 입력할 때, 정상적인 값이 들어오지 않을 시에 IOException이 발생한다.
	 */
	private void firstLineProcess(String readLine) throws IOException
	{
		if(readLine == null)
			throw new java.lang.NullPointerException();
		StringTokenizer st = new StringTokenizer(readLine);
		this.method = st.nextToken();
		this.url = new StringBuilder(st.nextToken());
		this.protocol = st.nextToken();
		st = null;
	}
}
