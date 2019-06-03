package parsing;
import java.io.File;
import java.util.Map;
import reader.CmdReader;
import java.lang.String;

public class RequestParser{
	
	private Map<String, String> requestHeaders;
	private String code;
	private String method;
	private File file;
	private String default_path;
	private String home;
	private StringBuilder url;
	
	private String screen;
	private String wordType;
	private String resultCmd;
	private boolean isError;
	
	public RequestParser(Map<String, String> reqheaders){
		if(reqheaders == null)
			throw new java.lang.NullPointerException("reqheaders is null");
		this.requestHeaders = reqheaders;
		this.isError = false;
	}
	
	/**
	 * @return request 헤더를 Map으로 반환한다
	 */
	public Map<String, String> getRequestHeaders(){
		return this.requestHeaders;
	}
	
	/**
	 * GET 요청으로 commandText의 값이 입력되었을 경우에만 WordType을 얻을 수 있다.
	 * CmdReader 클래스를 통해 얻는 값이다.
	 * @return wordType
	 */
	public String getWordType() {
		return this.wordType;
	}
	
	/**
	 * setMethodUrl(method, url)을 통해 초기화된다.
	 * @return url을 String으로 반환한다.
	 */
	public String getUrl(){
		return this.url.toString();
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
	public File getFile(){
		return this.file;
	}
	
	/**
	 * resultCmd를 반환한다.
	 * url이 dir, notepad 등 cmd를 이용하는 명령어일 경우에만 이 값이 초기화된다.
	 * 다른경우에는 null값이다.
	 * @return resultCmd
	 */
	public String getResultCmd() {
		return this.resultCmd;
	}
	
	/**
	 * url로 screen이 왔을 경우에만 이 값이 초기화된다.
	 * screenshot의 파일경로이다.
	 * @return screen
	 */
	public String getScreen() {
		return this.screen;
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
		this.url = new StringBuilder(url);
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
	 * parsing()에서 초기화되는 isError 값을 반환한다.
	 * @return isError
	 */
	public boolean getIsError() {
		return this.isError;
	}

	/**
	 * setMethodUrl() 이후에 사용이 가능하다.
	 * setMethodUrl()에서 this.method, this.url, this.filePath가 초기화 되기 때문이다.
	 * setDefaultPath() 이후에 사용가능. 마찬가지 이유로.
	 * @return isError  error -> true, 아니면 false 반환
	 */
	public void parsing(){
		this.isError = false;
		try{
			
			if(!("GET".equals(this.getMethod()) || "POST".equals(this.getMethod()))) {
				this.code = "405";
				this.isError = true;
				return;
			}

			if(this.url.indexOf(" ") != -1){
				this.code = "400";
				this.isError = true;	
				return;
			}

			if(this.getUrl().startsWith("/")){
				if(this.getUrl().length() > 1){
					if(this.getUrl().charAt(1) != '?') {
						this.url.insert(0, this.getDefaultPath());
						this.file = new File(this.getUrl());
					}
				} else {
					this.url = new StringBuilder(this.getDefaultPath()).append("/").append(this.getHome());
					this.file = new File(this.getUrl());
				}
			} else {
				this.code = "400";
				this.isError = true;
				return;
			}
						
			if(this.getUrl().startsWith("/?commandText=")) {
				CmdReader cr = new CmdReader(this.getUrl(), this.default_path);
				cr.execute();
				if(cr.getWordType().equals("screen")) {
					this.screen = cr.getScreen();
				} else if(cr.getWordType().equals("cmd")) {
					this.resultCmd = cr.getString();
				}
				this.wordType = cr.getWordType();
				this.code = "200";
				cr = null;
				return;
			}
		
			if(this.file.exists()){
				if((this.requestHeaders.get("if-none-match") != null) && this.requestHeaders.get("if-none-match").equals("\""+Long.toString(this.file.lastModified()) + "\"")){
					this.code = "304";
				} else {
					this.code = "200";
				}
			} else {
				this.url = new StringBuilder("fileNotFound");
				this.file = new File(this.getUrl());
				this.code = "404";
				this.isError = true;
			}

		}catch(Exception e){
			e.printStackTrace(System.out);
		}
	}
	
}