import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
/**
 * InputStream 에서 특정 구분자를 기준으로 read 할수 있는 기능을 제공하는 클래스
 * 
 * @author yna
 *
 */
public class ByteReader {
	
	private byte[] delimiter;	// 구분자
	private ByteArrayOutputStream baos;	// 버퍼
	
	/**
	 * 구분자 지정 생성자
	 * 
	 * @param deli	구분자 byte[]
	 */
	public ByteReader(byte[] deli)
	{
		if (deli == null || deli.length == 0)
			throw new IllegalArgumentException();
		// 전달 받은 array 의 원소가 변경되어도 영향 받지 않기 위해 새로 생성하여 복사 
		this.delimiter = new byte[deli.length];
		System.arraycopy(deli, 0, this.delimiter, 0, deli.length);
		
		// 버퍼크기를 전달받아서 고정된 byte[] 를 사용해도 되지만, 그 크기를 가늠할 수 없을 경우 아래처럼 클래스 사용.
		this.baos = new ByteArrayOutputStream();
	}
	/**
	 * InputStream으로부터 read() 한다.
	 * 지정한 구분자를 만날때까지 (또는 스트림의 끝까지) read한다.
	 * 
	 * @param is	InputStream
	 * @return	읽은 크기. 구분자의 길이는 미포함.
	 * @throws IOException	예외
	 */
	public int read(InputStream is) throws IOException
	{
		this.baos.reset();
		return this.read(is, this.baos);
	}
	/**
	 * 다음 구분자까지 read하여 출력 스트림에 write 한다.
	 * 
	 * @param is	read할 InputStream
	 * @param os	write할 OutputStream
	 * @return	읽을 길이. 실제 전달한 길이와 같지 않은 경우에 대한 처리를 고려한다.
	 * @throws IOException	예외
	 */
	public int read(InputStream is, OutputStream os) throws IOException
	{
		int n = 0;
		int offset = 0;
		int size = 0;
		while(true)
		{
			n = is.read();
			if (n < 0)
				break;
			if (this.delimiter[offset] == n)
			{
				offset++;
				if (offset == this.delimiter.length)
				{
					break;
				}
			}
			else
			{
				size++;
				if (offset > 0)
				{
					size+=offset;
					os.write(this.delimiter, 0, offset);
					offset = 0;
				}
				os.write(n);
			}
		}
		return size;
	}
	/**
	 * 현재 읽은 데이터를 byte[] 로 가져온다.
	 * 
	 * @return	데이터 
	 */
	public byte[] getBytes()
	{
		return this.baos.toByteArray();
	}
	/**
	 * 현재 읽은 데이터를 String으로 가져온다.
	 * 
	 * @param encoding	인코딩. null 일 경우 utf-8 적용.
	 * @return	문자열
	 * @throws UnsupportedEncodingException	인코딩 못찾을 경우 예외
	 */
	public String getString(String encoding) throws UnsupportedEncodingException
	{
		byte[] buf = this.getBytes();
		return new String(buf, encoding == null ? "utf-8" : encoding);
	}
	// test
	public static void main(String[] args) throws Exception
	{
		String s = "열심히 해 봅시다.";
		StringBuilder sb = new StringBuilder();
		// 구분자 아무거나!
		String deli = "--90jui;fdsa980-432q8m90-v8n9e0samifodsanus;asadsajklf;dsal\r\n";
		for (int i=0; i<10; i++)
		{
			sb.append(s).append(deli);
		}
		s = sb.toString();
		System.out.println(s);
		ByteReader br = new ByteReader(deli.getBytes());
		InputStream is = new ByteArrayInputStream(s.getBytes());
		int size = 0;
		byte[] buf = null;
		String str = null;
		int num = 1;
		File file = null;
		while(true)
		{
			if (num%2==1)
			{
				size = br.read(is);
				if (size <= 0)
					break;
				buf = br.getBytes();
				str = br.getString("utf-8");
				System.out.println(num + ":" + size);
				System.out.println("\t" + Base64.getEncoder().encodeToString(buf));
				System.out.println("\t" + str);
			}
			else
			{
				file = new File("/temp/file" + num + ".txt");
				try(FileOutputStream fos = new FileOutputStream(file))
				{
					size = br.read(is, fos);
					System.out.println(num + ":" + size);
				}catch(Exception ex)
				{
					ex.printStackTrace();
					continue;
				}
				System.out.println("\t" + file + "(" + file.length() + ")");
			}
			num++;
		}
		
	}
}
