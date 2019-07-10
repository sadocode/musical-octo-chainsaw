package client;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.String;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

public class FileP2P extends Thread{
	ServerSocket serverSocket;
	Socket socket;
	Client parent;
	InputStream is;
	OutputStream os;
	ByteArrayOutputStream baos; // RECEIVER -> dataType == IMAGE 인 경우만
	
	private String savePath; // RECEIVER
	private String filePath; // SENDER
	private String fileName; // SENDER, RECEIVER
	private byte roleType;   // RECEIVER, SENDER
	private byte dataType;   // RECEIVER
	private byte[] buffer;	 // RECEIVER, SENDER
	private int port;        // RECEIVER, SENDER
	private int bufferSize;   // RECEIVER, SENDER			
	
	public static final byte SENDER = 0;
	public static final byte RECEIVER = 1;
	
	public static final byte IMAGE = 0;
	public static final byte FILE = 0;
	
	public static final int LAST_PORT_NUMBER = 49151;
	
	/**
	 * sender의 생성자
	 * @param client	이 클래스를 호출한 자기 자신
	 * @param filePath 	보내는 파일의 path
	 * @param fileName	보내는 파일의 이름
	 * @param port		보내는 port번호
	 * @param bufferSize	보내기 전에 buffersize로 설정할 크기
	 */
	public FileP2P(Client client, String filePath, String fileName, int port, int bufferSize)
	{
		this.roleType = SENDER;
		this.parent = client;
		this.filePath = filePath;
		this.fileName = fileName;
		this.port = port;
		this.bufferSize = bufferSize;
	}
	
	/**
	 * receiver의 생성자
	 * @param client	이 클래스를 호출한 자기 자신
	 * @param savePath	저장할 위치
	 * @param dataType	image or file. image면 화면으로 뿌려주고, 파일이면 저장시킴.
	 * @param bufferSize	받기 전에 bufferSize로 설정할 크기
	 */
	public FileP2P(Client client, String savePath, String dataType, int bufferSize)
	{
		if("image".equals(dataType.toLowerCase()))
		{
			this.dataType = IMAGE;
		}
		if("file".equals(dataType.toLowerCase()))
		{
			this.dataType = FILE;
		}
		else
		{
			throw new java.lang.IllegalArgumentException();
		}
		
		this.roleType = RECEIVER;
		this.parent = client;
		this.savePath = savePath;
		this.bufferSize = bufferSize;
	}
	
	/**
	 * receiver의 서버 소켓이 설정되고난 후에, 서버 소켓의 port를 가져가기 위한 메소드
	 * @return port
	 */
	public int getPort()
	{
		return this.port;
	}

	@Override
	public void run()
	{
		//SENDER인 경우
		if(this.roleType == SENDER)
		{
			this.sender();
		}
		
		//RECEIVER인 경우
		if(this.roleType == RECEIVER)
		{
			this.receiver();
		}
	}
	
	/**
	 * sender일 경우에만 수행되는 메소드.
	 * socket을 할당.
	 * fileNameSize(4bytes) + fileName 을 보낸다. : sendFileName()
	 * 데이터를 보낸다. : sendPacket()
	 */
	private void sender()
	{
		try 
		{
			this.socket = new Socket(this.parent.getIp(), this.port);
			this.os = this.socket.getOutputStream();
			this.sendFileName();
			this.sendPacket();
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
		finally
		{
			try
			{
				this.socket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace(System.out);
			}
		}
	}
	
	/**
	 * fileName의 size와 fileName을 보낸다.
	 * fileNameSize는 4바이트 고정이다.
	 * @throws IOException
	 */
	private void sendFileName() throws IOException
	{
		byte[] fileName = this.fileName.getBytes();
		int size = fileName.length;
		byte[] fileNameSize = this.intToByteBuffer(size);
		
		//arraycopy하려고 new하는데 드는 자원이 더 들 것 같아서 그냥 write 두번으로 함.
		this.os.write(fileNameSize);
		this.os.write(fileName);
	}
	
	
	/**
	 * fileInputStream을 열고,
	 *  데이터를 bufferSize만큼 읽고 outputStream으로 write 하는 것을 반복한다. 
	 */
	private void sendPacket()
	{
		
		int n = 0;
		this.buffer = new byte[this.bufferSize];
		try(FileInputStream fis = new FileInputStream(this.filePath))
		{
			while(true)
			{
				n = fis.read(this.buffer, 0 , this.bufferSize);
				
				if(n == -1)
					break;
				
				if(n != this.bufferSize)
				{
					this.os.write(buffer, 0, n);
					break;
				}
				
				this.os.write(buffer);
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	
	/**
	 * receiver일 때만 실행되는 메소드이다.
	 * 서버소켓을 할당해준다. : initServerSocket()
	 * 소켓의 inputStream을 열어준다. : isSetInputStream()
	 * 파일 이름을 읽는다. : readFileName()
	 * 데이터를 받는다. : receivePacket() 
	 */
	private void receiver()
	{
		boolean setInputStream = false;

		this.serverSocket = this.initServerSocket();
		
		try
		{
			while((this.socket = this.serverSocket.accept()) != null)
			{
				//처음에 socket.getInputStream과 fileName만 읽어온다.
				if(!setInputStream)
				{
					setInputStream = this.isSetInputStream();
					this.fileName = this.readFileName();
				}
				this.receivePacket();
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}

	}
	
	/**
	 * 먼저 임의로 포트 번호를 10000번을 할당해준다.
	 * 그런데 만약에 해당 포트가 사용되고 있다면, 포트 번호를 1씩 더해가면서
	 * 이용가능한 포트번호를 찾고, serverSocket을 할당해준다.
	 * @return ServerSocket
	 */
	private ServerSocket initServerSocket()
	{
		this.port = 10000;
		ServerSocket serverSocket;
		while(true)
		{
			try
			{
				serverSocket = new ServerSocket(this.port);
				
				if(this.port > LAST_PORT_NUMBER)
				{
					break;
				}
				
				if(serverSocket != null)
					break;
			}
			catch(IOException ioe)
			{
				ioe.printStackTrace(System.out);
				this.port++;
			}
		}
		//예외 처리를 이용해서 사용중인 포트라고 할 때마다 새로운 포트를 골라줌..
		return serverSocket;
	}
	
	/**
	 * socket의 inputStream을 얻고 true를 반환한다.
	 * @return true
	 * @throws IOException
	 */
	private boolean isSetInputStream() throws IOException
	{
		// socket의 inputStream을 얻어오지 못 할 경우에 대한 예외처리가 필요함.
		this.is = this.socket.getInputStream();
		return true;
	}
	
	/**
	 * fileNameSize(4bytes) + fileName 을 읽고
	 * fileName을 반환해준다.
	 * readPacket()에 앞서서 실행되어야 한다.
	 * 
	 * @return fileName
	 * @throws IOException
	 */
	private String readFileName() throws IOException
	{
		int n = 0;
		int size = 0;
		byte[] fileNameSizeBuffer = new byte[4];
		byte[] fileName;
		
		n = this.is.read(fileNameSizeBuffer, 0 , 4);
		
		if(n != 4)
			throw new java.io.IOException();
		
		size = this.byteBufferToInt(fileNameSizeBuffer, 4);
		fileName = new byte[size];
		
		n = this.is.read(fileName, 0 , size);
		
		if(n != size)
			throw new java.io.IOException();
		
		return fileName.toString();
	}
	
	/**
	 * 데이터를 읽어온다.
	 * 이 메소드에 앞서서 readFileName() 메소드가 실행된다.
	 * dataType에 따라서 분기된다.
	 * IMAGE, FILE외의 dataType은 이 메소드에 도달하기 전에 이미 걸러지므로 따로 처리하지 않는다.
	 * 
	 * @throws IOException
	 */
	private void receivePacket() throws IOException
	{
	
		if(this.dataType == IMAGE)
		{
			this.receiveImage();
			this.viewImage();
		}
		if(this.dataType == FILE)
			this.receiveFile();
	}
	
	/**
	 * dataType이 IMAGE일 경우 실행되는 메소드이다.
	 * 이미지를 bufferSize 만큼 받아서 ByteArrayOutputStream에 저장한다.
	 */
	private void receiveImage()
	{		
		// 아직은 구현 전부 X임.
		
		this.baos = new ByteArrayOutputStream();
		this.baos.reset();
		int n = 0;
		
		try
		{
			while(true)
			{
				n = this.is.read(this.buffer, 0, this.bufferSize);
				
				if(n == -1)
					break;
				
				if(n != this.bufferSize)
				{
					this.baos.write(this.buffer, 0, n);
					break;
				}
				
				this.baos.write(this.buffer);
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	
	/**
	 * FileP2P를 초기화한 Client 자기 자신의 메소드인 viewImage를 이용해서,
	 * 이미지 창을 띄워준다.
	 */
	private void viewImage()
	{
		this.parent.viewImage(this.fileName, this.getBytes());
	}
	
	/**
	 * dataType이 FILE일 경우에 실행되는 메소드.
	 * FileOutputStream을 열고, bufferSize만큼씩 InputStream에서 받아다가
	 * file에다가 저장한다.
	 * 
	 * filePath는 getSavePath()에서 얻어온다.
	 */
	private void receiveFile()
	{
		int n = 0;
		String filePath = this.getSavePath();
		
		try(FileOutputStream fos = new FileOutputStream(filePath))
		{
			while(true)
			{
				n = this.is.read(this.buffer, 0 , this.bufferSize);
			
				if(n == -1)
					break;
				
				if(n != this.bufferSize)
				{
					fos.write(this.buffer, 0, n);
					break;
				}
				
				fos.write(this.buffer);
			}
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	
	/**
	 * receiver가 파일 저장을 원한 곳에 fileName을 더해서 savePath를 만들어 반환한다.
	 * @return savePath
	 */
	private String getSavePath()
	{
		StringBuilder sb = new StringBuilder(this.savePath).append("\\").append(this.fileName);
		return sb.toString();
	}
	
	/**
	 * int형 정수를 4바이트 배열로 변환시켜 반환한다.
	 * @param value
	 * @return byte[4]
	 */
	private byte[] intToByteBuffer(int value)
	{
		byte[] buffer = new byte[4];
		this.intToByteBuffer(value, buffer, 0);
		return buffer;
	}
	/**
	 * int형 정수를 배열의 offset으로 부터 4바이트에 복사한다.
	 * @param value
	 * @param buffer
	 * @param offset
	 * @return buffer
	 */
	private byte[] intToByteBuffer(int value, byte[] buffer, int offset)
	{
		buffer[offset++] = (byte)((value & 0xff000000) >> 24); 
		buffer[offset++] = (byte)((value & 0x00ff0000) >> 16);
		buffer[offset++] = (byte)((value & 0x0000ff00) >> 8);
		buffer[offset++] = (byte)((value & 0x000000ff));

		return buffer;
	}
	
	/**
	 * 바이트 배열을 int형 정수로 변환시켜 반환한다.
	 * @param buffer
	 * @param size
	 * @return int value
	 */
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
	
	/**
	 * this.baos를 바이트 배열로 반환한다.
	 * @return this.baos
	 */
	private byte[] getBytes()
	{
		return this.baos.toByteArray();
	}
}