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
	 * Socket���� ���� �� byte�� this.request�� ����
	 * requestHeaders �ʱ�ȭ
	 * default_path, home �ʱ�ȭ
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
	 * process()�� �Ϸ�Ǿ�� method�� �ʱ�ȭ�ȴ�.
	 * @return �������� ���̶��, GET, POST �߿��� �������ش�. 
	 */
	public String getMethod() 
	{
		return this.method;
	}
	
	/**
	 * process()�� �Ϸ�Ǿ�� url�� �ʱ�ȭ�ȴ�.
	 * @return ��û�� url�� ���� String���� ���ϵȴ�.
	 */
	public String getUrl() 
	{
		return this.url.toString();
	}
	
	/**
	 * process()�� �Ϸ�Ǿ�� protocol�� �ʱ�ȭ��.
	 * @return HTTP/1.1 �Ǵ� HTTP/1.0, HTTP/2 �� ���ϵȴ�.
	 */
	public String getProtocol() 
	{
		return this.protocol;
	}
	
	/**
	 * process()�� �Ϸ�Ǿ�� boundary�� �ʱ�ȭ��.
	 * @return POST�� ��쿡�� �������� boundary���� ���ϵȴ�.
	 */
	public String getBoundary() 
	{
		return this.boundary;
	}
	
	/**
	 * process()�� �Ϸ�Ǿ�� requestHeaders�� �ʱ�ȭ�ȴ�.
	 * @return request�� ��� ������ �� Map�� �������ش�.
	 */
	public Map<String, String> getRequestHeaders()
	{
		return this.requestHeaders;
	}
	
	/**
	 * parse()�� ���� �ʱ�ȭ�ȴ�.
	 * @return HTTP ���� �ڵ带 ��ȯ�Ѵ�. ex)200, 304, 404..
	 */
	public String getCode(){
		return this.code;
	}
	
	/**
	 * parse()�� ���� �ʱ�ȭ�ȴ�.
	 * @return url�� File�� ��ȯ�Ѵ�.
	 */
	public File getFile(){
		return this.file;
	}
	
	/**
	 * parse()���� �ʱ�ȭ�Ǵ� isError ���� ��ȯ�Ѵ�.
	 * @return isError
	 */
	public boolean getIsError() {
		return this.isError;
	}
	
	/**
	 * <����>
	 * index		���� �ӽ� ����
	 * indexStart	request���� lineBuffer�� ������ ��, �� ������ �����ϴ� index
	 * readLine		lineBuffer�� String���� ��ȯ�� ��. ��� �� ���ο� �ش��Ѵ�
	 * newLine		�� ������ ���۵Ǹ� newLine�� true�� �ǰ�, arraycopy�� �Ѵ�.
	 * firstLine	lineBuffer�� request�� ù ���� ��츸 true üũ
	 * byteCheck	lineBuffer�� ũ��
	 * reqH1		request ����� �����
	 * reqH2		request ����� ��
	 * 
	 * this.request�� ����Ʈ�� ���θ��� String���� �и��ϰ�, header�� ����
	 * �����ؼ� requestHeaders�� �ҹ��ڷ� �����Ѵ�. 
	 * method, url, protocol��  �����Ѵ�.
	 * 
	 * ù ������ ��� firstLineParsing(readLine)�޼ҵ� ����.
	 * ����� boundary�� ������, body�� ������ ����.(���� X)
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
					 *post��û�� ���� ���!
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
	 * request�� ù ������ �а�, method, url, protocol�� �ʱ�ȭ���ش�.
	 * @param readLine process()���� ó�� ������ �����̴�.
	 * @throws IOException StringTokenizer�� ���� �Է��� ��, �������� ���� ������ ���� �ÿ� IOException�� �߻��Ѵ�.
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
