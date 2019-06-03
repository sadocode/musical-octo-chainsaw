package server.packet;

import java.io.InputStream;
import java.util.Map;

public class RequestPacket {
	private InputStream is;
	private byte[] data;
	public RequestPacket(InputStream is)
	{
		this.is = is;
	}
	
	public Map<String,String> getHeaders()
	{
		return null;
	}
	public String getMethod()
	{
		return null;
	}
	public String getURL()
	{
		return null;
	}
	public void process(Map<String,String> info)
	{
		String webroot = info.get("base");
		///... 
		
		this.readPacket();
		this.parse();
	}
	private void readPacket()
	{
		//
	}
	private void parse()
	{
		//
	}
	
}
