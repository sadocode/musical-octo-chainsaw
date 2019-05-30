package reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ByteCmdReader{
	private ByteArrayOutputStream baos;
	private byte[] pathBuffer;
	
	public ByteCmdReader() {
		this.pathBuffer = setBuffer();
		this.baos = new ByteArrayOutputStream();
	}

	private void setPathBuffer(byte[] buffer) {
		this.pathBuffer = new byte[buffer.length];
		System.arraycopy(buffer, 0, this.pathBuffer, 0, buffer.length);
	}
	
	private byte[] getCheckBuffer() {
		return this.checkBuffer;
	}
	
	public int read(InputStream is) throws IOException{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		this.baos.reset();
		return this.read(is, this.baos);
	}
	
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
	 * @return read(is,os)에서 저장한 ByteArrayOutputStream의 값을 byte[]로 반환한다.
	 */
	public byte[] getBytes() {
		return this.baos.toByteArray();
	}
	/**
	 * 
	 * @return read(is,os)에서 저장한 ByteArrayOutputStream의 값을 String으로 반환한다.
	 * @throws UnsupportedEncodingException
	 */
	public String getString() throws UnsupportedEncodingException{
		return this.baos.toString("utf-8");
	}
}
