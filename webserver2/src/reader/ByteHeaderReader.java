package reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * <변수>
 * baos
 * 		ByteHeaderReader에서 읽은 바이트를 저장하는 stream
 * 
 * checkBuffer
 * 		request header를 읽기 위해 기준으로 삼은 버퍼
 * 
 * <생성자>
 * ByteHeaderReader()
 * 		checkBuffer 초기화
 * 		baos 선언
 * 
 * <메소드>
 * byte[] initCheckBuffer()
 * 		checkBuffer를 초기화 후, 반환해주는 메소드
 * 
 * int read(InputStream is)
 * 		InputStream에서 들어온 바이트를 읽는 메소드
 * 		baos를 reset해줌.
 * 		read(InputStream is, this.baos)의 값을 반환한다.
 * 			-> 읽은 바이트 수
 * 
 * int read(InputStream is, OutputStream os)
 * 		InputStream에서 들어온 바이트를 읽어 baos로 보내는 메소드
 * 		총 읽은 바이트 수를 반환한다.
 * 
 * byte[] getBytes()
 * 		baos의 값을 그대로 반환한다.
 * 
 * String getString()
 * 		baos 값을 String으로 변환하여 반환한다.
 * 		
 * @author hkj
 *
 */
public class ByteHeaderReader
{
	private ByteArrayOutputStream baos;
	private byte[] checkBuffer;
	
	/**
	 * checkBuffer 초기화
	 * baos 선언
	 */
	public ByteHeaderReader() 
	{
		this.checkBuffer = initCheckBuffer();
		this.baos = new ByteArrayOutputStream();
	}
	
	/**
	 * @return \r\n\r\n 이 저장된 buffer 반환
	 */
	private byte[] initCheckBuffer() 
	{
		byte[] checkBuffer = new byte[4];
		checkBuffer[0] = 13;
		checkBuffer[1] = 10;
		checkBuffer[2] = 13;
		checkBuffer[3] = 10;
		return checkBuffer;
	}
	
	/**
	 * InputStream에서 들어온 바이트를 읽는 메소드
	 * baos를 reset해줌.
	 * read(InputStream is, this.baos)의 값을 반환한다.
	 * 		-> 읽은 바이트 수
	 * 
	 * @param is 소켓에서 들어오는 값
	 * @return 읽은 바이트 수를 반환(size)
	 * @throws IOException
	 */
	public int read(InputStream is) throws IOException
	{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		this.baos.reset();
		return this.read(is, this.baos);
	}
	/**
	 * checkBuffer를 이용해 InputStream에서 들어오는 값과, 읽은 바이트를 비교하면서 받는다.
	 * 
	 * <변수>
	 * offset
	 * 		checkBuffer의 바이트를 나타내기 위한 offset
	 * n
	 * 		InputStream으로부터 읽은 값
	 * size
	 * 		InputStream으로부터 읽은 총 바이트 수
	 * 
	 * @param is 소켓에서 들어오는 값
	 * @param os byteArrayOutputStream으로 저장하기 위한 OutputStream
	 * @return 읽은 바이트 수 반환(size)
	 * @throws IOException
	 */
	private int read(InputStream is, OutputStream os) throws IOException
	{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		int size = 0;
		int offset = 0;
		int n = 0;
		
		while(true) 
		{
			n = is.read();
			
			if(n < 0)
				break;
			
			if(this.checkBuffer[offset] == n) 
			{
				offset++;
				if(offset == this.checkBuffer.length) 
				{
					size++;
					os.write(n);
					break;
				}
			} else 
			{
				offset = 0;
			}
			size++;	
			os.write(n);
		}
		return size;
	}
	/**
	 * 
	 * @return read(is,os)에서 저장한 ByteArrayOutputStream의 값을 byte[]로 반환한다.
	 */
	public byte[] getBytes() 
	{
		return this.baos.toByteArray();
	}
	/**
	 * 
	 * @return read(is,os)에서 저장한 ByteArrayOutputStream의 값을 String으로 반환한다.
	 * @throws UnsupportedEncodingException
	 */
	public String getString() throws UnsupportedEncodingException
	{
		return this.baos.toString("utf-8");
	}
}
