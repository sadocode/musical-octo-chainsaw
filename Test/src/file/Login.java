package test.file;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * properties 파일에 담긴 정보와 입력된 정보를 통해 로그인 성공, 실패 여부를 알 수 있는 클래스
 * 
 * 
 * 
 * filePath
 * 		로그인 정보를 담은 properties 파일의 경로
 * @author hkj
 *
 */
public class Login {
	private static final String filePath = "user.properties";
	
	private Properties user;
	private String id;
	private String password;
	private String workspace;
	private boolean login;
	
	public Login()
	{
		this.init();
	}
	/**
	 * filePath에 담긴 정보를 보고, 인스턴스 변수인 id, password, workspace를 초기화 시킨다.
	 */
	private void init()
	{
		
		try(FileInputStream fis = new FileInputStream(filePath))
		{
			this.user = new Properties();
			this.user.load(fis);
			
			this.id = this.user.getProperty("ID");
			this.password = this.user.getProperty("PASSWORD");
			this.workspace = this.user.getProperty("WORKSPACE");
			
		}
		catch(IOException ioe)
		{
			ioe.printStackTrace(System.out);
		}
	}
	/**
	 * 
	 * @param id
	 * @param password
	 * @return 로그인 성공, 실패 여부
	 */
	public boolean login(String id, String password)
	{
		if(id == null || password == null)
		{
			//처리
			return false;
		}
		
		if(this.id.equals(id) && this.password.equals(password))
		{
			return true;
		}
		else
		{
			// 처리
			return false;
		}
	}
}
