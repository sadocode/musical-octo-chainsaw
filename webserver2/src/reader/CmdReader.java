package reader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.Process;
import java.lang.ProcessBuilder;


public class CmdReader{
	private ByteArrayOutputStream baos;
	private ProcessBuilder pb;
	private Process p;
	private String url;
	
	public CmdReader() {
		this.baos = new ByteArrayOutputStream();
	}
	public CmdReader(String url) throws IOException{
		if (url == null)
			throw new java.lang.NullPointerException("CmdReader url is null");
		else if ("dir".equals(url) || "notepad".equals(url))
			throw new java.lang.IllegalArgumentException();
		
		this.baos = new ByteArrayOutputStream();
		this.url = url.substring(1) + "\r\n";
		pb = new ProcessBuilder("cmd");
		p = pb.start();
		pb.redirectErrorStream(true);
	}
	public int executeCmd() throws IOException{
		this.cmdUrlWrite();
		return this.read(this.p.getInputStream());
	}
	private void cmdUrlWrite() throws IOException{
		DataOutputStream dos = new DataOutputStream(p.getOutputStream());
		dos.writeBytes(this.url);
		dos.flush();
		dos.close();
	}
	public int read(InputStream is) throws IOException{
		if(is == null)
			throw new java.lang.NullPointerException("InputStream from Socket is null");
		this.baos.reset();
		return this.cmdRead(is, this.baos);
	}
	public int cmdRead(InputStream is, OutputStream os) throws IOException{
		int n = 0;
		int offset = 0;
		int size = 0;
		byte[] checkBuffer = {13, 10, 13, 10};
		while(true) {
			n = is.read();
			if(n < 0)
				break;
			if(checkBuffer[offset] == n) {
				offset++;
				if(offset == checkBuffer.length) {
					size++;
					os.write(n);
					offset = 0;
					break;
				}
			} else {
				offset = 0;
			}
			size++;
			os.write(n);
		}
		
		checkBuffer = null;
		int index = 0;
		boolean finishStorePath = false;
		byte[] temp = new byte[100];
		byte[] path = null;
		
		while(true) {
			n = is.read();
			if(n < 0)
				break;
			if(!finishStorePath)
				temp[index] = (byte)n;
			if(!finishStorePath && n == 62) {
				finishStorePath = true;
				path = new byte[index + 1];
				System.arraycopy(temp, 0, path, 0, index + 1);
				size++;
				os.write(n);
				temp = null;
				offset = 0;
				continue;
			}
			if(finishStorePath) {
				if(path[offset] == n) {
					offset++;
					if(offset == path.length) {
						size++;
						os.write(n);
						break;
					}
				} else {
					offset = 0;
				}
			}
			index++;
			size++;
			os.write(n);
		}
		path = null;
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
