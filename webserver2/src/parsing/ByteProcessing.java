package parsing;

import java.util.Map;
import java.util.HashMap;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import java.io.IOException;

/**
 * 
 * @author yna
 *
 */
public class ByteProcessing {
	private byte[] request;
	private Map<String, String> requestHeaders;
	private String method;
	private String url;
	private String protocol;
	private String boundary;
	/**
	 * 
	 * @param request
	 */
	public ByteProcessing(byte[] request) {
		if(request == null || request.length == 0)
			throw new java.lang.NullPointerException("request parameter of ByteProcessing constructor is null");
		this.request = new byte[request.length];
		System.arraycopy(request, 0, this.request, 0, request.length);
		this.requestHeaders = new HashMap<>();
	}
	/**
	 * processBytes()�� �Ϸ�Ǿ�� requestHeaders�� �ʱ�ȭ�ȴ�.
	 * @return request�� ��� ������ �� Map�� �������ش�.
	 */
	public Map<String, String> getMap(){
		return this.requestHeaders;
	}
	/**
	 * processBytes()�� �Ϸ�Ǿ�� method�� �ʱ�ȭ�ȴ�.
	 * @return �������� ���̶��, GET, POST �߿��� �������ش�. 
	 */
	public String getMethod() {
		return this.method;
	}
	/**
	 * processBytes()�� �Ϸ�Ǿ�� url�� �ʱ�ȭ�ȴ�.
	 * @return ��û�� url�� ���� String���� ���ϵȴ�.
	 */
	public String getUrl() {
		return this.url;
	}
	/**
	 * processBytes()�� �Ϸ�Ǿ�� protocol�� �ʱ�ȭ��.
	 * @return HTTP/1.1 �Ǵ� HTTP/1.0, HTTP/2 �� ���ϵȴ�.
	 */
	public String getProtocol() {
		return this.protocol;
	}
	/**
	 * processBytes()�� �Ϸ�Ǿ�� boundary�� �ʱ�ȭ��.
	 * @return POST�� ��쿡�� �������� boundary���� ���ϵȴ�.
	 */
	public String getBoundary() {
		return this.boundary;
	}
	/**
	 * ��� ����
	 * @param readLine processBytes()���� ó�� ������ �����̴�.
	 * @throws IOException StringTokenizer�� ���� �Է��� ��, �������� ���� ������ ���� �ÿ� IOException�� �߻��Ѵ�.
	 */
	private void firstLineParsing(String readLine) throws IOException{
		if(readLine == null)
			throw new java.lang.NullPointerException();
		
		StringTokenizer st = new StringTokenizer(readLine);
		this.method = st.nextToken();
		this.url = st.nextToken();
		this.protocol = st.nextToken();
		st = null;
	}
	/**
	 * index		���� �ӽ� ����
	 * indexStart	request���� lineBuffer�� ������ ��, �� ������ �����ϴ� index
	 * readLine		lineBuffer�� String���� ��ȯ�� ��. ��� �� ���ο� �ش��Ѵ�
	 * newLine		�� ������ ���۵Ǹ� newLine�� true�� �ǰ�, arraycopy�� �Ѵ�.
	 * firstLine	lineBuffer�� request�� ù ���� ��츸 true üũ
	 * byteCheck	lineBuffer�� ũ��
	 * reqH1		request ����� �����
	 * reqH2		request ����� ��
	 * 
	 * ù ������ ��� firstLineParsing(readLine)�޼ҵ� ����.
	 * ����� boundary�� ������, 
	 */
	public void processBytes() {
		
		try {
			int index = 0;
			int indexStart = 0;
			String readLine = "";
			boolean newLine = false;
			boolean firstLine = true;
			int byteCheck = 0;
			byte[] lineBuffer = null;
			String reqH1 = null;
			String reqH2 = null;
			for(int i = 0; i < this.request.length; i++) {
				
				if(newLine == true) {
					lineBuffer = new byte[byteCheck];
					System.arraycopy(this.request, indexStart, lineBuffer, 0, byteCheck);
					readLine = new String(lineBuffer, "utf-8");
					
					if(firstLine == true) {
						this.firstLineParsing(readLine);
						firstLine = false;
					}
					
					if(-1 != (index = readLine.indexOf(":"))){
                        reqH1 = readLine.substring(0, index).toLowerCase();
                        reqH2 = readLine.substring(index+2).toLowerCase();
                        this.requestHeaders.put(reqH1, reqH2);
                        reqH1 = null;
                        reqH2 = null;
                    }
					
					if(-1 != (index = readLine.indexOf("boundary="))) {
                        index += 9;
						this.boundary = "--" + readLine.substring(index);
                        this.requestHeaders.put("boundary", this.getBoundary());
					}
					
					/*
					if(readLine.equals(this.boundary)){
						byte[] bodyBuffer = new byte[this.request.length - indexStart];
						System.arraycopy(this.request, indexStart, bodyBuffer, 0, bodyBuffer.length);
						rb = new ReadBody(lineByte, this.boundary);
						break;
	                }
					*/
					
					
					indexStart += byteCheck;
					byteCheck = 0;
					lineBuffer = null;
					readLine = null;
				}
				
				
				newLine = false;
				byteCheck++;
				
				if(this.request[i] == 13) {
					if(i+1 == request.length) {
						throw new java.lang.IndexOutOfBoundsException();
					}
					if(this.request[i+1] == 10) {
						newLine = true;
						byteCheck++;
						i++;
					}	
				}
			}
			
		} catch(Exception e) {
			e.printStackTrace(System.out);
		}
	}
}
