
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
	
	public byte type;
	private String name;
	private byte[] nameByte;
	private int nameSize;
	private long fileSize;
	private byte[] fileData;
	
	private byte[] flag = new byte[2];
	
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
			Server.addList(this.name+"님이 접속했습니다.");
			
			while(this.is != null)
			{
				size = this.readMessage(this.is, this.baos);
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
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
		
		System.out.println("1");
		if((temp = this.readSop(is)) != 0)
			size += temp;
		else
			return temp;
		
		System.out.println("2");
		if((temp = this.readType(is)) != 0)
			size += temp;
		else
			return temp;
		System.out.println("3");
		if((temp = this.readFlag(is)) != 0)
			size += temp;
		else
			return temp;
		System.out.println("4");
		if((temp = this.readNsize(is)) != 0)
			size += temp;
		else
			return temp;
		System.out.println("5");
		if((temp = this.readName(is)) != 0)
			size += temp;
		else
			return temp;
		
		System.out.println("6");
		if((temp = this.readSize(is)) != 0)
			size += temp;
		else
			return temp;
		
		if((temp = this.readData(is)) != 0)
			size += temp;
		else 
			return temp;
		
		if((temp = this.readEOP(is)) != 0)
			size += temp;
		else
			return temp;
		
		
		return size;
	}
	
	/**
	 * SOP에 해당하는 4바이트를 읽고 정상이면 4, 오류면 0을 반환한다.
	 * 정상 : 0x00000000. 오류 : 그외의 값.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readSop(InputStream is) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		System.out.println("@" + this.byteBufferToInt(checkBuffer, 4));
		if(this.byteBufferToInt(checkBuffer, 4) != 0)
			return 0;
				
		return n;
	}
	
	/**
	 * 1바이트에 해당하는 TYPE을 읽고 정상이면 1, 오류면 0을 반환.
	 * this.type 값을 초기화해준다.
	 * @param is
	 * @return 1 or 0 반환
	 * @throws IOException
	 */
	private int readType(InputStream is) throws IOException
	{
		int n = 0;
		n = is.read();
		
		switch(n)
		{
		case 0:
			this.type = this.JOIN;
			break;
		case 1:
			this.type = this.CHAT;
			break;
		case 2:
			this.type = this.IMAGE;
			break;
		case 3:
			this.type = this.FILE_ASK;
			break;
		case 4:
			this.type = this.FILE_ACCEPT;
			break;
		case 5:
			this.type = this.FILE_DECLINE;
			break;
		case 6:
			this.type = this.FILE_SEND;
			break;
		case 7:
			this.type = this.FINISH;
			break;
		default:
			this.type = -1;
			break;
		}
		
		System.out.println("T@"+this.type);
		if(this.type != -1)
			return 1;
		else
			return 0;
	}
	
	private int readFlag(InputStream is) throws IOException
	{
		int n = 0;
		byte[] flagCheck = new byte[2];
		
		n = is.read(flagCheck, 0 , 2);
		
		if(n != 2)
			return 0;
		
		System.out.println("F@"+flagCheck[0] +"@" + flagCheck[1]);
		System.arraycopy(flagCheck, 0, this.flag, 0, 2);
		return 2;
	}
	
	/**
	 * name에 해당하는 4바이트만큼 읽고, nameSize를 초기화한다. 
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readNsize(InputStream is)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0 , 4);
		
		if(n != 4)
			return 0;
		
		
		this.nameSize = this.byteBufferToInt(checkBuffer, 4);
		System.out.println("NS@"+this.nameSize);
		return n;
	}
	
	/**
	 * nameSize에 해당하는 만큼의 바이트를 읽고 nameSize를 반환한다.
	 * name, nameByte를 초기화한다. 
	 * @param is
	 * @return 정상 -> this.nameSize, 오류 -> 0
	 * @throws IOException
	 */
	private int readName(InputStream is)throws IOException
	{
		int n = 0;
		byte[] name = new byte[this.nameSize];
		n = is.read(name, 0, this.nameSize);
		
		if(n != this.nameSize)
			return 0;
		
		try
		{
			this.name = new String(name, "utf-8");
		}
		catch(UnsupportedEncodingException uee)
		{
			this.name = new String(name);
		}
		System.out.println("!!");
		
		System.arraycopy(name, 0, this.nameByte, 0, this.nameSize);
		System.out.println("N@" + this.name);
		name = null;
		return n;
	}
	
	/**
	 * Size에 해당하는 4바이트를 읽고 fileSize를 초기화한다.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readSize(InputStream is)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		this.fileSize = this.byteBufferToLong(checkBuffer, 4);
		return n;
	}
	
	/**
	 * fileSize에 해당하는 만큼의 바이트를 읽고, fileData를 초기화한다.
	 * @param is
	 * @return 정상 -> fileSize, 오류 -> 0
	 * @throws IOException
	 */
	private int readData(InputStream is) throws IOException
	{
		int n = 0;
		int temp = (int)this.fileSize;
		byte[] dataBuffer;
		if(temp > 0)
		{
			dataBuffer = new byte[temp];
			n = is.read(dataBuffer, 0, temp);
			
			if(n != temp)
				return 0;
			System.arraycopy(dataBuffer, 0, this.fileData, 0, temp);
			dataBuffer = null;
			return temp;
		}
		else 
		{
			temp = (temp & 0x7fffffff);
		
			dataBuffer = new byte[temp];
			n = is.read(dataBuffer, 0, temp);
			
			if(n != temp)
				return 0;
			
			int temp2 = 0x40000000;
			//temp2 만큼 그냥 두 번 더 받아서
			//temp + temp2 + temp2 이렇게 둬야하나 싶은데.
			
			
			//ADD MORE!!!#!@#!#
			this.fileSize += temp;
			
				
		}
		
		return temp;
	}
	
	/**
	 * EOP에 해당하는 4바이트를 읽고, 정상이면 4 오류면 0을 반환한다.
	 * 정상 : 0xffffffff, 오류 : 그외의 값.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readEOP(InputStream is)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		if(this.byteBufferToInt(checkBuffer, 4) != (int)0xffffffff)
			return 0;
		
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
		int x = 0;
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
