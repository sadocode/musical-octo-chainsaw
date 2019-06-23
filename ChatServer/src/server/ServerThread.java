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
	
	private String type;
	private String name;
	private byte[] nameByte;
	private int nameSize;
	private long fileSize;
	private byte[] fileData;
	
	public static final String JOIN = "JOIN";
	public static final String CHAT = "CHAT";
	public static final String IMAGE = "IMAGE";
	public static final String FILE_ASK = "FILE_ASK";
	public static final String FILE_ACCEPT = "FILE_ACCEPT";
	public static final String FILE_DECLINE = "FILE_DECLINE";
	public static final String FILE_SEND = "FILE_SEND";
	public static final String FINISH = "FINISH";
	
	public ServerThread(Socket socket)
	{
		if(socket == null)
			throw new java.lang.NullPointerException("ServerThread socket is null.");
		
		this.socket = socket;
		this.baos = new ByteArrayOutputStream();
	}
	
	@Override
	public void run()
	{
		int size = 0;
		try
		{
			InputStream is = this.socket.getInputStream();
			while(true)
			{
				size = readMessage(is, this.baos);
				if(size != 0)
				{
					
					//유효한 메시지면 Server로 넘김. 어떻게 넘기냐?
					//server-t에서
					//
				}
				size = 0;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
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
		
		this.baos.reset();
		this.readSop(is);
		
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
		
		if(this.byteBufferToInt(checkBuffer, 4) != 0)
			return 0;
				
		return n;
	}
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
			this.type = null;
			break;
		}
		
		if(this.type != null)
			return 1;
		else
			return 0;
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
		//
		///
		//byteBufferToInt 수정 필요.
		this.nameSize = this.byteBufferToInt(checkBuffer, 4);
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
		
		System.arraycopy(name, 0, this.nameByte, 0, this.nameSize);
		
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
		
		//byteBufferToInt 수정필요
		this.fileSize = this.byteBufferToInt(checkBuffer, 4);
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
			temp = 0x7fffffff;
			dataBuffer = new byte[temp];
			n = is.read(dataBuffer, 0, temp);
			
			if(n != temp)
				return 0;
			
			this.fileSize += temp;
			
			// have to add more..
			return 0;
			
		}
		
		
		
		
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
	/**
	 * readJoin, readChat, readImage, readFinish에서 사용하는 메소드.
	 * SIZE(8바이트)를 읽고, DATA(id or chat message or image)을 읽는 메소드
	 * baos에 저장되는 값으로 
	 * 		readJoin -> id
	 * 		readChat -> message
	 * 		readImage -> image data
	 * 		readFinish -> id
	 * 읽은 총 바이트 수를 반환한다.
	 * @param is
	 * @param os
	 * @return size
 	 * @throws IOException
	 */
	private int read(InputStream is, OutputStream os) throws IOException
	{
		int size = 0;
		long dataSize = 0;
		byte[] sizeBuffer = new byte[8];
		int n = 0;
		ByteBuffer bb;
		
		while(true)
		{
			if(size == 0)
			{
				n = is.read(sizeBuffer, 0, 8);
				
				if(n < 8)
					break;
				
				bb = ByteBuffer.wrap(sizeBuffer);
				dataSize = bb.getLong();	
				size += 8;
				
				continue;
			}
			
			if(size < 8 + dataSize) 
			{
				n = is.read();
			
				if(n < 0)
					break;
			
				os.write(n);
				size++;
			}
			if(size == 8 + dataSize)
				break;
		}
		
		return size;
	}
	private int readJoin(InputStream is, OutputStream os) throws IOException
	{
		this.type = "JOIN";
		return this.read(is, os);
	}
	private int readChat(InputStream is, OutputStream os) throws IOException
	{
		this.type = "CHAT";
		return this.read(is, os);
	}
	private int readImage(InputStream is, OutputStream os) throws IOException
	{
		this.type = "IMAGE";
		return this.read(is, os);
	}
	
	
	//code error!!!!!!!!!!!!!!!!!!!!!!
	// 고쳐야해..
	private int readFileAsk(InputStream is, OutputStream os) throws IOException
	{
		long size = 0;
		long dataSize = 0;
		byte[] sizeBuffer = new byte[8];
		int n = 0;
		ByteBuffer bb;
		
		while(true)
		{
			if(size == 0)
			{
				n = is.read(sizeBuffer, 0, 8);
				
				if(n < 8)
					break;
				
				bb = ByteBuffer.wrap(sizeBuffer);
				dataSize = bb.getLong();	
				size += 8;
				
				continue;
			}
			if(size == 8)
			{
				n = is.read(sizeBuffer, 0, 8);
				
				if(n < 8)
					break;
				
				bb = ByteBuffer.wrap(sizeBuffer);
				dataSize = bb.getLong();	
				size += 8;
				this.fileSize = dataSize;
				continue;
			}
			if(size >= 16)
			{
				n = is.read();
				
				if(n < 0)
					break;
				
				os.write(n);
				size++;
				
				if(dataSize == size)
					break;
			}
		}
		
		this.type = "FILE_ASK";
		return (int)size;
	}
	
	/**
	 * type에 FILE_ACCEPT를 저장한다. 
	 * 반환하는 size는 8바이트이다.
	 * @param is
	 * @param os
	 * @return size
	 * @throws IOException
	 */
	private int readFileAccept(InputStream is, OutputStream os) throws IOException
	{
		int size = 8;
		byte[] sizeBuffer = new byte[8];
		int n = 0;
		
		n = is.read(sizeBuffer, 0, 8);
		if(n < 8)
			throw new java.io.IOException();
		
		this.type = "FILE_ACCEPT";
		return size;
	}
	
	/**
	 * type에 FILE_DECLINE을 저장한다. 
	 * 반환하는 size는 8바이트이다.
	 * @param is
	 * @param os
	 * @return size
	 * @throws IOException
	 */
	private int readFileDecline(InputStream is, OutputStream os) throws IOException
	{
		int size = 8;
		byte[] sizeBuffer = new byte[8];
		int n = 0;
		
		n = is.read(sizeBuffer, 0, 8);
		if(n < 8)
			throw new java.io.IOException();
		
		this.type = "FILE_DECLINE";
		return size;
	}
	
	private int readFileSend(InputStream is, OutputStream os) throws IOException
	{
		this.type = "FILE_SEND";
		return this.read(is, os);
	}
	private int readFinish(InputStream is, OutputStream os) throws IOException
	{
		this.type = "FINISH";
		return this.read(is, os);
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
