package reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * <����>
 * baos
 * 		ByteHeaderReader���� ���� ����Ʈ�� �����ϴ� stream
 * 
 * checkBuffer
 * 		request header�� �б� ���� �������� ���� ����
 * 
 * <������>
 * ByteHeaderReader()
 * 		checkBuffer �ʱ�ȭ
 * 		baos ����
 * 
 * <�޼ҵ�>
 * byte[] initCheckBuffer()
 * 		checkBuffer�� �ʱ�ȭ ��, ��ȯ���ִ� �޼ҵ�
 * 
 * int read(InputStream is)
 * 		InputStream���� ���� ����Ʈ�� �д� �޼ҵ�
 * 		baos�� reset����.
 * 		read(InputStream is, this.baos)�� ���� ��ȯ�Ѵ�.
 * 			-> ���� ����Ʈ ��
 * 
 * int read(InputStream is, OutputStream os)
 * 		InputStream���� ���� ����Ʈ�� �о� baos�� ������ �޼ҵ�
 * 		�� ���� ����Ʈ ���� ��ȯ�Ѵ�.
 * 
 * byte[] getBytes()
 * 		baos�� ���� �״�� ��ȯ�Ѵ�.
 * 
 * String getString()
 * 		baos ���� String���� ��ȯ�Ͽ� ��ȯ�Ѵ�.
 * 		
 * @author hkj
 *
 */
public class ByteHeaderReader
{
	private ByteArrayOutputStream baos;
	private byte[] checkBuffer;
	
	/**
	 * checkBuffer �ʱ�ȭ
	 * baos ����
	 */
	public ByteHeaderReader() 
	{
		this.checkBuffer = initCheckBuffer();
		this.baos = new ByteArrayOutputStream();
	}
	
	/**
	 * @return \r\n\r\n �� ����� buffer ��ȯ
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
	 * InputStream���� ���� ����Ʈ�� �д� �޼ҵ�
	 * baos�� reset����.
	 * read(InputStream is, this.baos)�� ���� ��ȯ�Ѵ�.
	 * 		-> ���� ����Ʈ ��
	 * 
	 * @param is ���Ͽ��� ������ ��
	 * @return ���� ����Ʈ ���� ��ȯ(size)
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
	 * checkBuffer�� �̿��� InputStream���� ������ ����, ���� ����Ʈ�� ���ϸ鼭 �޴´�.
	 * 
	 * <����>
	 * offset
	 * 		checkBuffer�� ����Ʈ�� ��Ÿ���� ���� offset
	 * n
	 * 		InputStream���κ��� ���� ��
	 * size
	 * 		InputStream���κ��� ���� �� ����Ʈ ��
	 * 
	 * @param is ���Ͽ��� ������ ��
	 * @param os byteArrayOutputStream���� �����ϱ� ���� OutputStream
	 * @return ���� ����Ʈ �� ��ȯ(size)
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
	 * @return read(is,os)���� ������ ByteArrayOutputStream�� ���� byte[]�� ��ȯ�Ѵ�.
	 */
	public byte[] getBytes() 
	{
		return this.baos.toByteArray();
	}
	/**
	 * 
	 * @return read(is,os)���� ������ ByteArrayOutputStream�� ���� String���� ��ȯ�Ѵ�.
	 * @throws UnsupportedEncodingException
	 */
	public String getString() throws UnsupportedEncodingException
	{
		return this.baos.toString("utf-8");
	}
}
