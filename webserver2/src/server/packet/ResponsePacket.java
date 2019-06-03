package server.packet;

import java.io.File;
import java.io.OutputStream;
import java.util.Map;

public class ResponsePacket {
	private OutputStream os;
	public ResponsePacket(OutputStream os)
	{
		this.os = os;
	}
	public void setHeader(String name, String value)
	{
		//
	}
	public void setHeaders(Map<String,String> headers)
	{
		//
	}
	public void println(String message)
	{
		//
	}
	public void write(File file)
	{
		//
	}
	public void write(byte[] buf)
	{
		this.write(buf, 0, buf.length);
	}
	public void write(byte[] buf, int start, int length)
	{
		//
	}
	public void response(RequestPacket rp)
	{
		
	}
	public void response(RequestPacket rp, Exception ex)
	{
		//
	}
}
