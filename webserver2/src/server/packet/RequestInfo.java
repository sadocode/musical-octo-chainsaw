package server.packet;

import java.io.IOException;
import java.net.Socket;
import java.util.Map;

public class RequestInfo {
	private Socket socket;
	private RequestPacket request;
	private ResponsePacket response;
	
	public RequestInfo(Socket socket) throws IOException
	{	
		this.socket = socket;
		this.init();
	}
	private void init() throws IOException
	{
		this.request = new RequestPacket(socket.getInputStream());
		this.response = new ResponsePacket(socket.getOutputStream());
	}
	public void process(Map<String,String> environments, Map<String,String> responseHeaders)
	{
		this.request.process(environments);
		this.response.response(this.request);
	}
}
