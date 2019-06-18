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
	private String type;
	private String fileName;
	private long fileSize;
	
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
					//broadcasting
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace(System.out);
		}
	}
	/**
	 * socket의 InputStream에서 들어온 데이터의 총 바이트를 반환한다.
	 * 
	 * @param is
	 * @param os
	 * @return size
	 * @throws IOException
	 */
	private int readMessage(InputStream is, OutputStream os) throws IOException
	{
		if(is == null)
			throw new java.lang.NullPointerException("readMessage() is is null.");
		
		this.baos.reset();
		int size = 0;
		int n = 0;
		byte[] checkPacket = new byte[4];
		ByteBuffer bb;
		try {
			Loop1: while (true) {
				if (size == 0) {
					n = is.read(checkPacket, 0, 4);

					if(n < 4)
						break;
					
					bb = ByteBuffer.wrap(checkPacket);
					if(bb.getInt() != 0x00000000)
						break;
					
					size += 4;
					continue;
				}
				if (size == 4) {
					n = is.read();
					size++;

					switch (n) {
					case 0:
						size += readJoin(is, os);
						break;
					case 1:
						size += readChat(is, os);
						break;
					case 2:
						size += readImage(is, os);
						break;
					case 3:
						size += readFileAsk(is, os);
						break;
					case 4:
						size += readFileAccept(is, os);
						break;
					case 5:
						size += readFileDecline(is, os);
						break;
					case 6:
						size += readFileSend(is, os);
						break;
					case 127:
						size += readFinish(is, os);
						break;
					default:
						size = 0;
						break Loop1;
					}
					continue;
				}
				if (size > 4) {
					n = is.read(checkPacket, 0, 4);
					
					if(n < 4)
						break;
					
					bb = ByteBuffer.wrap(checkPacket);
					if(bb.getInt() != 0xffffffff)
						break;
					size += 4;
				}

			}
		}
		catch(IOException ioe)
		{
			System.out.println("wrong packet received");
			size = 0;
		}
		
		return size;
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
}