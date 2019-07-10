package client;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;

public class ClientThread extends Thread{
	private Socket socket;
	private InputStream is;
	private ByteArrayOutputStream baos;
	private Client parent;
	
	private String id; // 자기 자신의 id
	private int packetSize;
	private byte type;
	private byte[] flag;
	private int nameSize;
	private byte[] name; // 다른 사람 혹은 자신의 id가 될 수도 있음.
	private int fileNameSize;
	private byte[] fileName;
	private long dataSize;
	private byte[] data;
	
	private String nameString;
	private String fileNameString;
	
	
	
	public static final byte JOIN = 0;
	public static final byte CHAT = 1;
	public static final byte IMAGE =2 ;
	public static final byte FILE_ASK = 3;
	public static final byte FILE_ACCEPT = 4;
	public static final byte FILE_DECLINE = 5;
	public static final byte FILE_SEND = 6;
	public static final byte FINISH = 127;
	
	public ClientThread(Client parent, Socket socket, String id)
	{
	
		this.parent = parent;
		this.id = id;
		this.socket = socket;
		this.baos = new ByteArrayOutputStream();
		try
		{
			is = this.socket.getInputStream();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		this.flag = new byte[2];
	}
	
	@Override
	public void run()
	{
		int size = 0;
		while(is != null)
		{			
			try
			{
				size = this.readMessage(this.is, this.baos);
				System.out.println("ReadSize : " + size + " /");
				this.addList();
				
				if(size == 0) 
					//break;
					continue;
				
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace(System.out);
			}
			finally
			{
				
			}
		}
	}
	public void addList()
	{
		StringBuilder sb;
		if(this.type == JOIN)
		{
			sb = new StringBuilder("[").append(this.nameString).append("]님이 입장하였습니다.");
			this.parent.addList(sb.toString());
			System.out.println(this.nameString + " JOIN");
		}
			
		if (this.type == CHAT)
		{
			sb = new StringBuilder("[").append(this.nameString).append("]").append(new String(this.data));
			this.parent.addList(sb.toString());
	
		}
		
		if(this.type == IMAGE)
		{
			if(this.id.equals(this.nameString))
				return;
			sb = new StringBuilder("[").append(this.nameString).append("]님이 ").append(new String(this.fileName)).append("를 보내셨습니다.");
			this.parent.addList(sb.toString());
			this.parent.viewImage(new String(this.fileName), this.data);
		}
		if(this.type == FILE_ASK)
		{
			if(this.id.equals(this.nameString))
				return;
			this.parent.viewFileInfo(this.id, this.nameString, this.fileNameString, this.dataSize);
			//fileInfo 저장
			this.parent.save
		}
		if(this.type == FILE_ACCEPT)
		{
			this.parent.sendFile(this.fileNameString);
		}
		if(this.type == FINISH)
		{
			sb = new StringBuilder("[채팅 종료]");
			this.parent.addList(sb.toString());
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
		
		if((temp = this.readSOP(is)) != 0)
			size += temp;
		else
			return temp;
		
		if((temp = this.readPacketSize(is)) != 0)
			size += temp;
		else
			return temp;
		
		if((temp = this.readType(is)) != 0)
			size += temp;
		else
			return temp;
		
		if((temp = this.readFlag(is)) != 0)
			size += temp;
		else
			return temp;
		
		
		if((temp = this.readNameSize(is)) != 0)
			size += temp;
		else
			return temp;
		
		if((temp = this.readFileNameSize(is)) != 0)
			size += temp;
		else 
			return temp;
		
		if((temp = this.readDataSize(is)) != 0)
			size += temp;
		else
			return temp;
		
		if((temp = this.readName(is)) != 0)
			size += temp;
		else
			return temp;
		
		//FILE 쪽 수정해야함.
		if(this.fileNameSize != 0)
		{
			if((temp = this.readFileName(is)) != 0)
				size += temp;
			else
				return temp;
		}

		if(this.type != FILE_ASK && this.dataSize != 0)
		{
			if((temp = this.readData(is)) != 0)
				size += temp;
			else 
				return temp;
		}

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
	private int readSOP(InputStream is) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		System.out.println("@readSOP@this.id : " + this.id);
		System.out.println("@readSOP@SOP : " + this.byteBufferToInt(checkBuffer, 4));
		if(this.byteBufferToInt(checkBuffer, 4) != 0)
			return 0;
				
		return n;
	}
	
	private int readPacketSize(InputStream is) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		this.packetSize = this.byteBufferToInt(checkBuffer, 4);
		System.out.println("@readPacketSize@packetSize : " + this.packetSize);
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
		case 127:
			this.type = FINISH;
			break;
		default:
			this.type = -1;
			break;
		}
		
		System.out.println("@readType@type : "+this.type);
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
		
		System.out.println("@readFlag@flag : "+flagCheck[0] + flagCheck[1]);
		System.arraycopy(flagCheck, 0, this.flag, 0, 2);
		return 2;
	}
	
	/**
	 * name에 해당하는 4바이트만큼 읽고, nameSize를 초기화한다. 
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readNameSize(InputStream is)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		
		n = is.read(checkBuffer, 0 , 4);
		
		if(n != 4)
			return 0;
		
		
		this.nameSize = this.byteBufferToInt(checkBuffer, 4);
		System.out.println("@readNameSize@nameSize : "+this.nameSize);
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
		return n;
	}
	private int readFileNameSize(InputStream is) throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		
		this.fileNameSize = this.byteBufferToInt(checkBuffer, 4);
		System.out.println("@readFileNameSize@fileNameSize : " + this.fileNameSize);
		return n;
	}
	
	private int readFileName(InputStream is) throws IOException
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
		System.out.println("@readFileName@fileName : " + this.fileNameString);
		return n;
	}
	/**
	 * Size에 해당하는 4바이트를 읽고 fileSize를 초기화한다.
	 * @param is
	 * @return 정상 -> 4, 오류 -> 0
	 * @throws IOException
	 */
	private int readDataSize(InputStream is)throws IOException
	{
		int n = 0;
		byte[] checkBuffer = new byte[4];
		n = is.read(checkBuffer, 0, 4);
		
		if(n != 4)
			return 0;
		System.out.println("@readDataSize@dataSize : "+this.byteBufferToLong(checkBuffer, 4));
		
		this.dataSize = this.byteBufferToLong(checkBuffer, 4);
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
		this.data = new byte[(int)this.dataSize];
		
		n = is.read(this.data, 0, (int)this.dataSize);
		
		if(n != (int)this.dataSize)
			return 0;
		System.out.println("@readData@data : " + new String(this.data));
		return (int)this.dataSize;
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
		System.out.println("@readEOP@EOP");
		return n;
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
	
}
