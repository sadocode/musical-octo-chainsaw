package server.packet;

import reader.ByteHeaderReader;
import parsing.RequestParser2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class RequestPacket {
	private InputStream is;
	private byte[] data;
	private String webRoot;
	private String home;
	private String method;
	private String url;
	private Map<String, String> requestHeaders;
	private String screen;
	private String cmdResult;
 
	
	public RequestPacket(InputStream is)
	{
		this.is = is;
	}
	
	public Map<String,String> getRequestHeaders()
	{
		return this.requestHeaders;
	}
	
	public String getMethod()
	{
		return this.method;
	}
	
	public String getUrl()
	{
		return this.url;
	}
	
	public void process(Map<String,String> info)
	{
		this.webRoot = info.get("WEBROOT");
		this.home = info.get("HOME");
		this.readPacket();
		this.parse();
	}
	
	/**
	 * Socket의 InputStream으로부터 byte를 읽어서 byte[] data에 저장한다.
	 * ByteHeaderReader 클래스를 이용해 byte를 읽어들인다.
	 */
	private void readPacket()
	{
		try 
		{
			ByteHeaderReader byteHeaderReader = new ByteHeaderReader();
			byteHeaderReader.read(this.is);
			this.data = byteHeaderReader.getBytes();			
		} 
		catch(IOException ioe) 
		{
			ioe.printStackTrace(System.out);
		} 
		finally 
		{
			if(this.is != null) 
			{
				try 
				{
					this.is.close();
				}
				catch(Exception e) 
				{
					e.printStackTrace(System.out);					
				}
			}
		}
	}
	
	/**
	 * 
	 * 
	 */
	private void parse()
	{
		try 
		{
			RequestParser2 requestParser2 = new RequestParser2(this.data, this.webRoot, this.home);
			requestParser2.process();
			requestParser2.parse();
			
			this.requestHeaders = requestParser2.getRequestHeaders();
			this.method = requestParser2.getMethod();
			this.url = requestParser2.getUrl();
			
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	
}
