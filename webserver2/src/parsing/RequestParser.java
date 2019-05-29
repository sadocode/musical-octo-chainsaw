package parsing;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import java.lang.String;
import java.util.Map;

public class RequestParser{
	
	private Map<String, String> requestHeaders;
	private String code;
	private String method;
	private File filePath;
	private String url;
	private String default_path;
	private String home;
	
	public RequestParser(Map<String, String> reqheaders){
		if(reqheaders == null)
			throw new java.lang.NullPointerException("reqheaders is null");
		this.requestHeaders = reqheaders;
	}
	/**
	 * @return request 헤더를 Map으로 반환한다
	 */
	public Map<String, String> getRequestHeaders(){
		return this.requestHeaders;
	}
	/**
	 * setMethodUrl(method, url)을 통해 초기화된다.
	 * @return url을 String으로 반환한다.
	 */
	public String getUrl(){
		return this.url;
	}
	/**
	 * parsing()을 통해 초기화된다.
	 * @return HTTP 상태 코드를 반환한다. ex)200, 304, 404..
	 */
	public String getCode(){
		return this.code;
	}
	/**
	 * setMethodUrl(method, url)을 통해 초기화된다.
	 * @return 받은 request의 method를 반환한다.
	 */
	public String getMethod(){
		return this.method;
	}
	/**
	 * setMethodUrl(method, url)을 통해 초기화된다.
	 * @return url을 File로 반환한다.
	 */
	public File getFilePath(){
		return this.filePath;
	}
	/**
	 * method, url을 입력받아 this.method, this.url을 초기화한다.
	 * @param method
	 * @param url
	 */
	public void setMethodUrl(String method, String url){
		if(method == null)
			throw new java.lang.NullPointerException("method is null");
		if(url == null)
			throw new java.lang.NullPointerException("url is null");
		this.method = method;
		this.url = url;
	}
	/**
	 * this.default_path를 초기화한다.
	 * @param default_path
	 */
	public void setDefaultPath(String default_path) {
		if(default_path == null)
			throw new java.lang.NullPointerException("default_path is null");
		this.default_path = default_path;
	}
	/**
	 * setDefaultPath로 초기화된 이후에 사용 가능.
	 * @return default_path 반환
	 */
	public String getDefaultPath(){
		return this.default_path;
	}
	/**
	 * url이 / 인 경우, 화면에 띄워줄 html파일의 이름을 설정한다.
	 * @param home
	 */
	public void setHome(String home) {
		if(home == null)
			throw new java.lang.NullPointerException("home is null");
		this.home = home;
	}
	/**
	 * setHome(home)으로 초기화해야 사용가능
	 * @return this.home 리턴
	 */
	public String getHome() {
		return this.home;
	}
	/**
	 * setMethodUrl() 이후에 사용이 가능하다.
	 * setMethodUrl()에서 this.method, this.url, this.filePath가 초기화 되기 때문이다.
	 * setDefaultPath() 이후에 사용가능. 마찬가지 이유로.
	 */
	public void parsing(){
		boolean isError = false;
		try{
			if(!"GET".equals(this.getMethod()) || !"POST".equals(this.getMethod())) {
				this.code = "405";
				isError = true;
			}

			if(!isError && this.url.contains(" ")){
				this.code = "400";
				isError = true;						
			}

			
			if(!isError && this.url.startsWith("/")){
				if(this.url.length() > 1){
					this.url = this.getDefaultPath() + this.url.substring(1);
					this.filePath = new File(this.getUrl());
				} else {
					this.url = this.getDefaultPath() + this.getHome();
					this.filePath = new File(this.getUrl());
				}
			} else if(!isError && !this.url.startsWith("/")){
					this.code = "400";
					isError = true;
			}
			
			
			if(!isError && this.filePath.exists()){
				if((this.requestHeaders.get("if-none-match") != null) && this.requestHeaders.get("if-none-match").equals("\""+Long.toString(this.filePath.lastModified()) + "\"")){
					this.code = "304";
				} else {
					this.code = "200";
				}
			} else if(!isError && !this.filePath.exists()){
				this.url = "fileNotFound";
				this.filePath = new File(this.getUrl());
				this.code = "404";
			}

		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
}