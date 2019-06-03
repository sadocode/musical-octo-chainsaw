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
	 * @return request ����� Map���� ��ȯ�Ѵ�
	 */
	public Map<String, String> getRequestHeaders(){
		return this.requestHeaders;
	}
	
	/**
	 * GET ��û���� commandText�� ���� �ԷµǾ��� ��쿡�� WordType�� ���� �� �ִ�.
	 * CmdReader Ŭ������ ���� ��� ���̴�.
	 * @return wordType
	 */
	public String getWordType() {
		return this.wordType;
	}
	
	/**
	 * setMethodUrl(method, url)�� ���� �ʱ�ȭ�ȴ�.
	 * @return url�� String���� ��ȯ�Ѵ�.
	 */
	public String getUrl(){
		return this.url.toString();
	}
	
	/**
	 * parsing()�� ���� �ʱ�ȭ�ȴ�.
	 * @return HTTP ���� �ڵ带 ��ȯ�Ѵ�. ex)200, 304, 404..
	 */
	public String getCode(){
		return this.code;
	}
	
	/**
	 * setMethodUrl(method, url)�� ���� �ʱ�ȭ�ȴ�.
	 * @return ���� request�� method�� ��ȯ�Ѵ�.
	 */
	public String getMethod(){
		return this.method;
	}
	
	/**
	 * setMethodUrl(method, url)�� ���� �ʱ�ȭ�ȴ�.
	 * @return url�� File�� ��ȯ�Ѵ�.
	 */
	public File getFile(){
		return this.file;
	}
	
	/**
	 * resultCmd�� ��ȯ�Ѵ�.
	 * url�� dir, notepad �� cmd�� �̿��ϴ� ��ɾ��� ��쿡�� �� ���� �ʱ�ȭ�ȴ�.
	 * �ٸ���쿡�� null���̴�.
	 * @return resultCmd
	 */
	public String getResultCmd() {
		return this.resultCmd;
	}
	
	/**
	 * url�� screen�� ���� ��쿡�� �� ���� �ʱ�ȭ�ȴ�.
	 * screenshot�� ���ϰ���̴�.
	 * @return screen
	 */
	public String getScreen() {
		return this.screen;
	}
	
	/**
	 * method, url�� �Է¹޾� this.method, this.url�� �ʱ�ȭ�Ѵ�.
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
	 * this.default_path�� �ʱ�ȭ�Ѵ�.
	 * @param default_path
	 */
	public void setDefaultPath(String default_path) {
		if(default_path == null)
			throw new java.lang.NullPointerException("default_path is null");
		this.default_path = default_path;
	}
	
	/**
	 * setDefaultPath�� �ʱ�ȭ�� ���Ŀ� ��� ����.
	 * @return default_path ��ȯ
	 */
	public String getDefaultPath(){
		return this.default_path;
	}
	
	/**
	 * url�� / �� ���, ȭ�鿡 ����� html������ �̸��� �����Ѵ�.
	 * @param home
	 */
	public void setHome(String home) {
		if(home == null)
			throw new java.lang.NullPointerException("home is null");
		this.home = home;
	}
	
	/**
	 * setHome(home)���� �ʱ�ȭ�ؾ� ��밡��
	 * @return this.home ����
	 */
	public String getHome() {
		return this.home;
	}
	
	/**
	 * parsing()���� �ʱ�ȭ�Ǵ� isError ���� ��ȯ�Ѵ�.
	 * @return isError
	 */
	public boolean getIsError() {
		return this.isError;
	}

	/**
	 * setMethodUrl() ���Ŀ� ����� �����ϴ�.
	 * setMethodUrl()���� this.method, this.url, this.filePath�� �ʱ�ȭ �Ǳ� �����̴�.
	 * setDefaultPath() ���Ŀ� ��밡��. �������� ������.
	 * @return isError  error -> true, �ƴϸ� false ��ȯ
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