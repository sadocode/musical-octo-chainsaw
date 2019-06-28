package server;

import java.net.Socket;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.io.UnsupportedEncodingException;

public class ServerThread extends Thread{
	private Socket socket;
	private ByteArrayOutputStream baos;
	private InputStream is;
	private OutputStream os;
	
	private int packetSize;
	public byte type;
	private byte[] flag;
	private String name;
	private int nameSize;
	private byte[] nameByte;
	private int fileNameSize;
	private byte[] fileNameByte;
	private long dataSize;
	private byte[] data;
	
	
	
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
			
			Server.addClient(this.name, this.os);
			System.out.println("SIZE : " +Server.clients.size());
			Server.addList(this.name+"님이 접속했습니다.");
			
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
			Server.clients.remove(this.name);
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
		this.nameByte = null;
		this.fileNameSize = 0;
		this.fileNameByte = null;
		this.dataSize = 0;
		this.data = null;
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
		
		System.out.println("0");
		///SOP(4)
		if((temp = this.readSOP(is, this.baos)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}
		System.out.println("1");
		///packetSize(4)
		if((temp = this.readPacketSize(is, os)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}
		System.out.println("2");
		///type(1)
		if((temp = this.readType(is, this.baos)) != 0)
			size += temp;
		else
		{
			this.reset();
			return temp;
		}
		System.out.println("3");
		///type == JOIN or FINISH
		if(this.type == JOIN || this.type == FINISH)
		{
			System.out.println("4");
			//flag(2)
			if((temp = this.readFlag(is, this.baos)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
			System.out.println("5");
			///nameSize(4)
			if((temp = this.readNameSize(is, this.baos)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
			System.out.println("6");
			///name(nameSize)
			if((temp = this.readName(is, this.baos)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
			System.out.println("7");
			///fileNameSize(4)
			if((temp = this.readFileNameSize(is, this.baos)) != 0)
				size += temp;
			else 
			{
				this.reset();
				return temp;
			}
			System.out.println("8");
			///dataSize(4)
			if((temp = this.readDataSize(is, this.baos)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
			System.out.println("9");
			///EOP(4)
			if((temp = this.readEOP(is, this.baos)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
		}//JOIN or FINISH
		else
		{
			System.out.println("10");
			if((temp = this.readRemainder(is, os)) != 0)
				size += temp;
			else
			{
				this.reset();
				return temp;
			}
		}
		System.out.println("11");
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
		System.out.println("@readNsize@nameSize :"+this.nameSize);
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
		this.nameByte = new byte[this.nameSize];
		n = is.read(this.nameByte, 0, this.nameSize);

		if(n != this.nameSize)
			return 0;
		
		try
		{
			this.name = new String(this.nameByte, "utf-8");
		}
		catch(UnsupportedEncodingException uee)
		{
			this.name = new String(this.nameByte);
		}
		System.out.println("@readName@name : " + this.name);
		os.write(this.nameByte);
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
		this.fileNameByte = new byte[this.fileNameSize];
		n = is.read(this.fileNameByte, 0, this.fileNameSize);
		
		if(n != this.fileNameSize)
			return 0;
		System.out.println("@readFname@fileName :" + new String(this.fileNameByte));
		os.write(this.fileNameByte);
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
		System.out.println("&&"+this.byteBufferToLong(checkBuffer, 4));
		this.dataSize = this.byteBufferToLong(checkBuffer, 4);
		System.out.println("@readSize@fileSize :" + this.dataSize);
		os.write(checkBuffer);
		return n;
	}
	
	/**
	 * dataSize에 해당하는 만큼의 바이트를 읽고, data를 초기화한다.
	 * @param is
	 * @return 정상 -> dataSize, 오류 -> 0
	 * @throws IOException
	 */
	private int readData(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		this.data = new byte[(int)this.dataSize];
		n = is.read(this.data, 0, (int)this.dataSize);
		
		if(n != this.dataSize)
			return 0;
		
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
	
	/**
	 * SOP(4) + packetSize(4) + type(1)
	 * => 9
	 * @param is
	 * @param os
	 * @return
	 * @throws IOException
	 */
	private int readRemainder(InputStream is, OutputStream os) throws IOException
	{
		int header = 9;
		int n = 0;
		byte[] buffer = new byte[(int)this.packetSize - header];
	
		n = is.read(buffer, 0, buffer.length);
		
		if(n != buffer.length)
			return 0;
		
		os.write(buffer);
		buffer = null;
		return ((int)this.packetSize - header);
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
