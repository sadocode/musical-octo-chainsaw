package file;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MakeHTML {
	private String html;
	private File file;
	private String url;
	private String screen;
	private String resultCmd;
	private StringBuilder htmlStringBuilder;
	
	public MakeHTML(String html) {
		if(html == null)
			throw new java.lang.NullPointerException("MakeHTML html is null");
		this.html = html;
		this.htmlStringBuilder = new StringBuilder("<!DOCHTML><html><head></head><body></body></html>");
	}
	public MakeHTML(String html, String resultCmd) {
		if(html == null)
			throw new java.lang.NullPointerException("MakeHTML html is null");
		else if(resultCmd == null)
			throw new java.lang.NullPointerException("MakeHTML resultCmd is null");
		this.html = html;
		this.resultCmd =resultCmd;
		this.htmlStringBuilder = new StringBuilder("<!DOCHTML><html><head><link rel='stylesheet' href='./css/styles.css' type='text/css'></head><body></body></html>");
	}
	
	/**
	 * html파일의 경로를 반환한다.
	 * @return html
	 */
	public String getHtml() {
		return this.html;
	}
	
	/**
	 * File file = new file(String html)
	 * @return file
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * url을 설정한다.
	 * @param url
	 */
	public void setUrl(String url) {
		if(url == null)
			throw new java.lang.NullPointerException("MakeHTML setUrl() url is null");
		this.url = url;
	}
	
	/**
	 * 스크린샷의 경로를 설정한다.
	 * @param screen
	 */
	public void setScreen(String screen) {
		if(screen == null)
			throw new java.lang.NullPointerException("MakeHTML setScreen() screen is null");
		StringBuilder temp = new StringBuilder(screen);
		int index = temp.indexOf("/temp/");
		temp.delete(0, index);
		temp.insert(0, ".");
		this.screen = temp.toString();
	}
	
	/**
	 * make()를 해야 html파일이 만들어지고, 
	 * file이 초기화된다.
	 * @throws IOException
	 */
	public void make() throws IOException{
		this.file = new File(this.html);
		int index = this.htmlStringBuilder.indexOf("</body>");
		if(this.url.endsWith("screen")) {
			this.htmlStringBuilder.insert(index, "<img class='screenShot' src='" + this.screen + "'>");
		} else if(this.url.endsWith("dir")) {
			this.htmlStringBuilder.insert(index, "<pre>"+this.resultCmd.replaceAll("<DIR>", "[DIR]")+"</pre>");
		} else {
			System.out.println(this.url);
			this.htmlStringBuilder.insert(index, "<p>hello</p>");
		}
		
		FileWriter fw = new FileWriter(file, false);
		fw.write(this.htmlStringBuilder.toString());
		fw.flush();
		fw.close();
	}
	
}
