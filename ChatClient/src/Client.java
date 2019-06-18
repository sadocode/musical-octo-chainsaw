import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Properties;

public class Client {
	private ByteArrayOutputStream baos;
	private Socket socket;
	private Properties info;
	private int port;
	private String ip;
	private String id;
	private String type;
	
	public Client()
	{
		this.baos = new ByteArrayOutputStream();
	}
	private void setId()
	{
		//frame에 id입력란 에서 받아옴.
	}
	private void setEnvironment() 
	{
	   	try(FileInputStream fis = new FileInputStream("info.properties")) 
	   	{
	   		this.info = new Properties();
	   		this.info.load(fis);
	   		this.port = Integer.parseInt(info.getProperty("PORT"));
	   		this.ip = info.getProperty("IP");
	   	} 
	   	catch(IOException ioe) 
	   	{
	   		ioe.printStackTrace(System.out);
	  	}
	}
	private void setSocket()
	{
		//id, ip, port를 더해서 소켓 접속.
		
	}
	
	private void setType()
	{
		//chatframe에서 특정 상황에 맞게 type이 변경된다.
	}
	/**
	 * baos로 메시지를 저장하는 메소드.
	 * 패킷구조 : SOP + TYPE + SIZE + DATA + EOP 에 맞춰서 저장한다.
	 * @param os
	 */
	private void write(OutputStream os)
	{
		this.baos.reset();		
		switch(this.type)
		{
		case("JOIN"):
			break;
		case("CHAT"):
			break;
		case("IMAGE"):
			break;
		case("FILE_ASK"):
			break;
		case("FILE_ACCEPT"):
			break;
		case("FILE_DECLINE"):
			break;
		case("FILE_SEND"):
			break;
		case("FINISH"):
			break;
		default:
			break;
		}
	}
	
	/**
	 * server로 baos의 값을 보내는 메소드
	 * @param os
	 * @return baos
	 */
	private byte[] sendMessage(OutputStream os)
	{
		return this.baos.toByteArray();
	}
	private byte[] getBytes()
	{
		
		return this.baos.toByteArray();
	}
	public static void main(String[] args) 
	{
		Client client = new Client();
		client.setId();
		client.setEnvironment();
		
		
		try 
		{
			client.setSocket();
		
			//frame에 뭐 클릭하면? -> setType() 실행.
			// write() 실행.
			//이벤트로 해야하는데 그러면 무한루프 돌려야하나? 모르겠네;
		
		
			//clientThread.start();
			//clientThread => read 계속 하는 놈.
	
			client.sendMessage(client.socket.getOutputStream());
		}
		catch(IOException ioe)
		{
			//예외 처리
		}
		finally
		{
			//소켓 연결 제거.
		}
	}
}