package server;

import java.net.Socket;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;

/**
 * client에게서 오는 packet을 read하고 그대로 broadcasting 해주는 클래스.
 * write하는 것은 Server 클래스의 broadcasting 말고는 없음.
 * @author hkj
 *
 */
public class ServerThread extends Thread{
	private Socket socket;
	private ByteArrayOutputStream baos;
	private InputStream is;
	private OutputStream os;
	
	private int packetSize;
	private byte type;
	private byte[] flag;
	private int nameSize;
	private byte[] name;
	private int fileNameSize;
	private byte[] fileName;
	private long dataSize;
	private byte[] data;
	
	private String nameString;
	private String fileNameString;
	
	
	public static final int MAX_READ = 65536;
	
	public static final byte JOIN = 0;
	public static final byte CHAT = 1;
	public static final byte IMAGE = 2;
	public static final byte FILE_ASK = 3;
	public static final byte FILE_ACCEPT = 4;
	public static final byte FILE_DECLINE = 5;
	public static final byte FILE_SEND = 6;
	public static final byte FINISH = 127;
	
	public ServerThread(Socket socket)
	{
		if(socket == null)
			throw new java.lang.NullPointerException("ServerThread socket is null.");
		
		this.socket = socket;
		this.baos = new ByteArrayOutputStream();
		try
		{
			this.is = this.socket.getInputStream();
			this.os = this.socket.getOutputStream();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace();
		}
	}
	
	@Override
	public void run()
	{
		int size = 0;
		try
		{
			size = this.readMessage(this.is, this.baos);
			
			Server.addClient(this.nameString, this.os);
			Server.addList(this.nameString + "님이 접속했습니다. 현재 접속자 수 : " + Server.clients.size());
			
			while(this.is != null)
			{
				size = this.readMessage(this.is, this.baos);
				
				if(size > 0)
				{
					System.out.println("++++"+ size +"++++" + this.getBytes());
					Server.broadcasting(this.getBytes());
					this.reset();
				}
				else
					break;
				
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		finally
		{
			Server.broadcasting(this.getBytes());
			Server.clients.remove(this.nameString);
			StringBuilder sb = new StringBuilder("[").append(this.socket.getInetAddress()).append(":").append(socket.getPort()).append("]에서 접속을 종료하였습니다.");
			Server.addList(sb.toString());
		}
	}
	
	public synchronized void reset()
	{
		this.packetSize = 0;
		this.type = 0;
		this.flag = null;
		this.nameSize = 0;
		this.name = null;
		this.fileNameSize = 0;
		this.fileName = null;
		this.dataSize = 0;
		this.data = null;
		this.nameString = null;
		this.fileNameString = null;
		this.baos.reset();
	}
	
	/**
	 * 읽은 패킷에 오류가 있을 때 0을 반환한다. 정상적인 패킷일 경우 읽은 총 size반환.
	 * 
	 * @param is
	 * @param os
	 * @return size  socket의 InputStream에서 들어온 데이터의 총 바이트를 반환한다.
	 * @throws IOException
	 */
	private int readMessage(InputStream is, OutputStream os) throws IOException
	{
		if(is == null)
			throw new java.lang.NullPointerException("readMessage() is is null.");
		
		int size = 0;
		int temp = 0;
		this.baos.reset();
		
		if((temp = this.readSOP(is, os)) != 0)
			size += temp;
		else
		{
			System.out.println("SOP fail");
			this.reset();
			return temp;
		}
		
		if((temp = this.readPacketSize(is, os)) != 0)
			size += temp;
		else
		{
			System.out.println("packetSize fail");
			this.reset();
			return temp;
		}
		
		if((temp = this.readType(is, os)) != 0)
			size += temp;
		else
		{
			System.out.println("type fail");
			this.reset();
			return temp;
		}

		if((temp = this.readFlag(is, os)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}
		
		if((temp = this.readNameSize(is, os)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}
		
		if((temp = this.readFileNameSize(is, os)) != 0)
			size += temp;
		else 
		{
			this.reset();
			return temp;
		}
		
		if((temp = this.readDataSize(is, os)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}
		
		if((temp = this.readName(is, os)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}

		if(this.fileNameSize != 0)
		{
			if((temp = this.readFileName(is, os)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
			
		}
		
		if(this.dataSize != 0 && this.type != FILE_ASK)
		{
			System.out.println("??");
			if((temp = this.readData(is, os)) != 0)
				size += temp;
			else 
			{
				this.reset();
				return temp;
			}
		}
		
		if((temp = this.readEOP(is, os)) != 0)
			size += temp;
		else
		{
			System.out.println("what?");
			this.reset();
			return temp;
		}
		System.out.println("+@#!@?");
		return size;
	}
	
	/**
	 * SOP에 해당하는 4바이트를 읽고 정상이면 4, 오류면 0을 반환한다.
	 * 정상 : 0x00000000. 오류 : 그외의 값.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readSOP(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		System.out.println("@readSop@Sop : " + this.byteBufferToInt(checkBuffer, 4));
		if(this.byteBufferToInt(checkBuffer, 4) != 0)
			return 0;
		
		os.write(checkBuffer);
		return n;
	}
	
	private int readPacketSize(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		this.packetSize = this.byteBufferToInt(checkBuffer, 4);
		
		if(this.packetSize < 0)
			return 0;
		
		System.out.println("@readPacketSize@packetSize : " + this.packetSize);
		os.write(checkBuffer);
		return n;
	}	
	/**
	 * 1바이트에 해당하는 TYPE을 읽고 정상이면 1, 오류면 0을 반환.
	 * this.type 값을 초기화해준다.
	 * @param is
	 * @return 1 or 0 반환
	 * @throws IOException
	 */
	private int readType(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		n = is.read();
		
		switch(n)
		{
		case 0:
			this.type = JOIN;
			break;
		case 1:
			this.type = CHAT;
			break;
		case 2:
			this.type = IMAGE;
			break;
		case 3:
			this.type = FILE_ASK;
			break;
		case 4:
			this.type = FILE_ACCEPT;
			break;
		case 5:
			this.type = FILE_DECLINE;
			break;
		case 6:
			this.type = FILE_SEND;
			break;
		case 7:
			this.type = FINISH;
			break;
		default:
			this.type = -1;
			break;
		}
		
		System.out.println("@readType@Type : "+this.type);
		if(this.type != -1)
		{
			os.write(this.type);
			return 1;
		}
		else
			return 0;
	}
	
	private int readFlag(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		this.flag = new byte[2];
		
		n = is.read(this.flag, 0 , 2);
		
		if(n != 2)
			return 0;
		
		System.out.println("@readFlag@Flag : " + this.flag[0] + this.flag[1]);
		os.write(this.flag);
		return 2;
	}
	
	/**
	 * name에 해당하는 4바이트만큼 읽고, nameSize를 초기화한다. 
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readNameSize(InputStream is, OutputStream os)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0 , 4);
		
		if(n != 4)
			return 0;
		
		
		this.nameSize = this.byteBufferToInt(checkBuffer, 4);
		System.out.println("@readNameSize@nameSize :"+this.nameSize);
		os.write(checkBuffer);
		return n;
	}
	
	/**
	 * nameSize에 해당하는 만큼의 바이트를 읽고 nameSize를 반환한다.
	 * name, nameByte를 초기화한다. 
	 * @param is
	 * @return 정상 -> this.nameSize, 오류 -> 0
	 * @throws IOException
	 */
	private int readName(InputStream is, OutputStream os)throws IOException
	{
		int n = 0;
		this.name = new byte[this.nameSize];
		n = is.read(this.name, 0, this.nameSize);

		if(n != this.nameSize)
			return 0;
		
		try
		{
			this.nameString = new String(this.name, "utf-8");
		}
		catch(UnsupportedEncodingException uee)
		{
			this.nameString = new String(this.name);
		}
		System.out.println("@readName@name : " + this.nameString);
		os.write(this.name);
		return n;
	}
	
	private int readFileNameSize(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		this.fileNameSize = this.byteBufferToInt(checkBuffer, 4);
		System.out.println("@readFnsize@fileNameSize :"+this.fileNameSize);
		os.write(checkBuffer);
		return n;
	}
	
	private int readFileName(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		this.fileName = new byte[this.fileNameSize];
		n = is.read(this.fileName, 0, this.fileNameSize);
		
		if(n != this.fileNameSize)
			return 0;
		try
		{
			this.fileNameString = new String(this.fileName, "utf-8");
		}
		catch(UnsupportedEncodingException uee)
		{
			this.fileNameString = new String(this.fileName);
		}
		System.out.println("@readFileName@fileName :" + this.fileNameString);
		os.write(this.fileName);
		return n;
	}
	/**
	 * Size에 해당하는 4바이트를 읽고 fileSize를 초기화한다.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readDataSize(InputStream is, OutputStream os)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;

		this.dataSize = this.byteBufferToLong(checkBuffer, 4);
		System.out.println("@readDataSize@dataSize :" + this.dataSize);
		os.write(checkBuffer);
		return n;
	}
	
	/**
	 * fileSize에 해당하는 만큼의 바이트를 읽고, fileData를 초기화한다.
	 * @param is
	 * @return 정상 -> fileSize, 오류 -> 0
	 * @throws IOException
	 */
	private int readData(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		
		this.data = new byte[(int)this.dataSize];
		
		n = is.read(this.data, 0, (int)this.dataSize);
		
		if(n != (int)this.dataSize)
			return 0;
		System.out.println("@readData@data : " +new String(this.data));
		os.write(this.data);
		return (int)this.dataSize;
	}
	
	/**
	 * EOP에 해당하는 4바이트를 읽고, 정상이면 4 오류면 0을 반환한다.
	 * 정상 : 0xffffffff, 오류 : 그외의 값.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readEOP(InputStream is, OutputStream os)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		if(this.byteBufferToInt(checkBuffer, 4) != (int)0xffffffff)
			return 0;
		os.write(checkBuffer);
		System.out.println("@readEOP@EOP");
		return n;
	}

	public String getString()
	{
		try
		{
			return this.baos.toString("utf-8");
		}
		catch(UnsupportedEncodingException uee)
		{
			return this.baos.toString();
		}
	}
	public byte[] getBytes()
	{
		return this.baos.toByteArray();
	}
	private int byteBufferToInt(byte[] buffer, int size)
	{
		int value = 0;
		int index = size - 1;
		while(index >= 0)
		{
			value += (int)((buffer[index] & 0xFF) << 8 * (size - index-- - 1));
		}
		return value;
	}
	private long byteBufferToLong(byte[]buffer, int size)
	{
		long value = 0;
		int index = size - 1;
		while(index >= 0)
		{
			value += (long)((buffer[index] & 0xFFL) << 8 * (size - index-- - 1));
		}
		return value;
	}
}
