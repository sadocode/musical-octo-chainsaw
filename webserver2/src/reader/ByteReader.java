package reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ByteReader{
	private ByteArrayOutputStream baos;
	private byte[] checkBuffer;
	
	public ByteReader() {
		this.checkBuffer = setBuffer();
		this.baos = new ByteArrayOutputStream();
	}
	/**
	 * 
	 * @return \r\n �� ����� buffer ��ȯ
	 */
	private byte[] setBuffer() {
		byte[] checkBuffer = new byte[4];
		checkBuffer[0] = 13;
		checkBuffer[1] = 10;
		checkBuffer[2] = 13;
		checkBuffer[3] = 10;
		return checkBuffer;
	}
	/**
	 * 
	 * @return this.checkBuffer ��ȯ
	 */
	private byte[] getCheckBuffer() {
		return this.checkBuffer;
	}
	/**
	 * 
	 * @param is ���Ͽ��� ������ ��
	 * @return ���� ����Ʈ ���� ��ȯ(size)
	 * @throws IOException
	 */
	public int read(InputStream is) throws IOException{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		this.baos.reset();
		return this.read(is, this.baos);
	}
	/**
	 * 
	 * @param is ���Ͽ��� ������ ��
	 * @param os byteArrayOutputStream���� �����ϱ� ���� OutputStream
	 * @return ���� ����Ʈ �� ��ȯ(size)
	 * @throws IOException
	 */
	private int read(InputStream is, OutputStream os) throws IOException{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		int size = 0;
		int offset = 0;
		int n = 0;
		byte[] buffer = getCheckBuffer();
		
		while(true) {
			n = is.read();
			
			if(n < 0)
				break;
			
			if(buffer[offset] == n) {
				offset++;
				if(offset == buffer.length) {
					size++;
					os.write(n);
					break;
				}
			} else {
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
	public byte[] getBytes() {
		return this.baos.toByteArray();
	}
	/**
	 * 
	 * @return read(is,os)���� ������ ByteArrayOutputStream�� ���� String���� ��ȯ�Ѵ�.
	 * @throws UnsupportedEncodingException
	 */
	public String getString() throws UnsupportedEncodingException{
		return this.baos.toString("utf-8");
	}
}
