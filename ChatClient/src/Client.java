import java.io.ByteArrayOutputStream;

public class Client {
	private ByteArrayOutputStream baos;
	
	public Client()
	{
		this.baos = new ByteArrayOutputStream();
	}
	
	private void readMessage()
	{
		
	}
	private byte[] writeMessage()
	{
		
		return this.getBytes();
	}
	private byte[] getBytes()
	{
		
		return this.baos.toByteArray();
	}
	public static void main(String[] args) 
	{
		
	}
}