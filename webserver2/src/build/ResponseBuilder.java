package build;
import java.io.File;
import java.util.Date;
import java.lang.String;
import java.lang.StringBuilder;
import java.util.Map;
import java.util.HashMap;

public class ResponseBuilder{
	private Date today;
	private StringBuilder response;
	private boolean isError = false;
	private File file;
	private String url;
	private Map<String, String> requestHeaders;
	private String code;
	
	public ResponseBuilder(Map<String, String> requestHeaders){
		if(requestHeaders == null)
			throw new java.lang.NullPointerException("ResponseBuilder : requestHeader is null!");
		this.requestHeaders = requestHeaders;;
	}

	
	/**
	 * url을 설정
	 * @param url
	 */
	public void setUrl(String url) {
		if(url == null)
			throw new java.lang.NullPointerException("url is null");
		this.url = url;
	}
	/**
	 * code를 설정
	 * @param code
	 */
	public void setCode(String code) {
		if(code == null)
			throw new java.lang.NullPointerException();
		if(code.length() != 3)
			throw new java.lang.IllegalArgumentException();
		this.code = code;
	}
	/**
	 * file을 설정
	 * @param file
	 */
	public void setFile(File file){
		if(file == null)
			throw new java.lang.IllegalArgumentException();
		this.file = file;
	}
	/**
	 * request의 에러여부를 설정해 준다.
	 * 
	 * @param value
	 */
	private void setError(boolean value) {
		if (value == true)
			this.isError = true;
		else
			this.isError = false;
	}
	/**
	 * this.response의 값을 설정해준다.
	 * 
	 * @param response
	 */
	private void setResponse(Object response) {
		if(this.response == null)
			this.response = new StringBuilder();
		this.response.append(response);
	}
	/**
	 * String
	 * @return response 반환
	 */
	public String getResponse(){
		return this.response.toString();
	}
	/**
	 * String
	 * @return url
	 */
	public String getUrl() {
		return this.url;
	}
	/**
	 * String
	 * @return code
	 */
	public String getCode() {
		return this.code;
	}
	
	/**
	 * build()에 앞서, setUrl(), setFile(), setCode()를 해야한다.
	 * ResponseBuilder의 값들은 build()과정을 거쳐야 값이 초기화된다.
	 * 
	 */
	public void build(){
		if(this.getCode().equals("405")){
			System.out.println("405 Method Not Allowed");
            this.setResponse("HTTP/1.1 405 Method Not Allowed \r\nConnection: close\r\n");
            this.setError(true);
		} else if(this.getCode().equals("400")){
            System.out.println("400 BAD REQUEST");
            this.setResponse("HTTP/1.1 400 Bad Request Message \r\nConnection: close\r\n");
            this.setError(true);
        } else if(this.getCode().equals("404")){
            System.out.println("404 Requested File Not Found");
            this.setResponse("HTTP/1.1 404 Not Found \r\nConnection: close\r\n");
            this.setError(true);
        } else if(this.getCode().equals("200")){
        	this.setResponse("HTTP/1.1 200 OK \r\n");
        	this.setError(false);
        } else if(this.getCode().equals("304")){
        	this.setResponse("HTTP/1.1 304 NOT MODIFIED \r\n");
        	this.setError(false);
        } else {
        	this.setResponse("error");
        	this.setError(true);
        }
        if(!this.isError){
        	today = new Date();
        	this.mimetypeResponse(this.getUrl());
        	this.setResponse("Content-Length: ");
        	this.setResponse(file.length());
        	this.setResponse("\r\nDate: ");
        	this.setResponse(today);
        	this.setResponse("\r\nLast-Modified: ");
        	this.setResponse(new Date(file.lastModified()));
        	this.setResponse("\r\netag: \"");
        	this.setResponse(file.lastModified());
        	this.setResponse("\r\n\r\n");  	
        }

	}
	/**
	 * setUrl()을 한 후에 사용가능하다.
	 * filename을 보고 mime type을 구분한다.
	 * mime type에 따라서 StringBuilder response에 Content-Type, Cache-Control을 설정해 준다.
	 * 
	 * @param filename
	 */
	public void mimetypeResponse(String filename){
        if(filename.endsWith(".jpg")){//image
                this.setResponse("Content-Type: image/jpeg;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".jpeg")){//image
            	this.setResponse("Content-Type: image/jpeg;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".bmp")){//image
            	this.setResponse("Content-Type: image/bmp;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".gif")){//image
            	this.setResponse("Content-Type: image/gif;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".ico")){//image
            	this.setResponse("Content-Type: image/x-ico;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".webp")){//image
            	this.setResponse("Content-Type: image/webm;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".svg")){//image
            	this.setResponse("Content-Type: image/svg+xml;\r\nCache-Control: max-age=20;\r\n");
            } else if(filename.endsWith(".pdf")){//pdf
            	this.setResponse("Content-Type: application/pdf;\r\nCache-Control: max-age=5;\r\n");
            } else if(filename.endsWith(".ppt")){//ppt
            	this.setResponse("Content-Type: application/ppt;\r\nCache-Control: max-age=5;\r\n");
            } else if(filename.endsWith(".xml")){//xml
            	this.setResponse("Content-Type: application/xml;\r\nCache-Control: max-age=5;\r\n");
            } else if(filename.endsWith(".json")){//xml
            	this.setResponse("Content-Type: application/json;\r\nCache-Control: max-age=5;\r\n");
            } else if(filename.endsWith(".wav")){//audio
            	this.setResponse("Content-Type: audio/wav;\r\nCache-Control: max-age=5;\r\n");
            } else if(filename.endsWith(".mpeg")){
            	this.setResponse("Content-Type: video/mpeg;\r\nCache-Control: max-age=5;\r\n");
            } else if(filename.endsWith(".css")){
            	this.setResponse("Content-Type: text/css;charset=utf-8\r\nCache-Control: no-cache, no-store;\r\n");
            } else if(filename.endsWith(".htm")){
            	this.setResponse("Content-Type: text/html;charset=utf-8\r\nCache-Control: no-cache, no-store\r\n");
            } else if(filename.endsWith(".html")){
            	this.setResponse("Content-Type: text/html;charset=utf-8\r\nCache-Control: no-cache, no-store\r\n");
            } else if(filename.endsWith(".txt")){
            	this.setResponse("Content-Type: text/plain;charset=utf-8\r\nCache-Control: max-age=10;\r\n");
            } else if(filename.endsWith(".js")){
            	this.setResponse("Content-Type: application/js;charset=utf-8\r\nCache-Control: private, max-age=86400\r\n");
            } else if(filename.endsWith(".woff")){
            	this.setResponse("Content-Type: application/x-font-woff;charset=utf-8\r\nCache-Control: max-age=86400\r\n");
            }
    }
}