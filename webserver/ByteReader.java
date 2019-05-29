package webserver;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.lang.String;
public class ByteReader {
	
	private byte[] delimiter;	
	private ByteArrayOutputStream baos;	
	

	public ByteReader(byte[] deli)
	{
		if (deli == null || deli.length == 0)
			throw new IllegalArgumentException();
		
		this.delimiter = new byte[deli.length];	
		System.arraycopy(deli, 0, this.delimiter, 0, deli.length);	
		this.baos = new ByteArrayOutputStream();
	}

	public int read(InputStream is) throws IOException
	{
		this.baos.reset();
		return this.read(is, this.baos);
	}

	public int read(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		int offset = 0;
		int size = 0;
		byte[] b = new byte[this.delimiter.length + 2];
		System.arraycopy(this.delimiter, 0, b, 0, this.delimiter.length);
		b[b.length - 2] = 45;
		b[b.length - 1] = 45;

		while(true)
		{
			n = is.read();
			if (n < 0)
				break;

			if (b[offset] == n) {
				offset++;
				if(offset == b.length) {
					size++;
					os.write(n);
					break;
				}
			}
			else {
				offset = 0;
			}
			//System.out.print("@offset : "+offset + "@n : " +n);
			size++;
			os.write(n);
		}
		return size;
	}

	public byte[] getBytes()
	{
		return this.baos.toByteArray();
	}
	
	public String getString(String encoding) throws UnsupportedEncodingException
	{
		byte[] buf = this.getBytes();
		return new String(buf, encoding == null ? "utf-8" : encoding);
	}
}
