package test.file;

/**
 * Response 헤더 중 쿠키를 설정해주는 클래스이다.
 * 
 * @author hkj
 *
 */
public class Cookie implements ResponseHeader, {
	private int maxAge;
	private final static int DEFAULT_AGE = 3600;
	
	public Cookie()
	{
		this.setMaxAge();	
	}
	
	
	/**
	 * 쿠키의 maxAge를 설정해주는 메소드.
	 * 받은 second에 문제가 있을 시에 그냥 maxAge를 DEFAULT_AGE로 설정해준다.
	 * @param second
	 */
	public void setMaxAge(int second)
	{
		try
		{
			this.maxAge = second;
		}
		catch(Exception e)
		{
			this.maxAge = this.setMaxAge();
		}
	}
	/**
	 * 쿠키의 maxAge를 DEFAULT_AGE로 설정해주는 메소드.
	 * @return DEFAULT_AGE
	 */
	public int setMaxAge() 
	{
		this.maxAge = DEFAULT_AGE;
		return DEFAULT_AGE;
	}
	
	@Override
	public String getHeader()
	{	
		StringBuilder sb = new StringBuilder("set-cookie:")
				.append("sad");
		
		return sb.toString();
	}
	
	public String isLogin(boolean login)
	{
		if(login)
			return this.getHeader();
		else
			return "";
	}
	
}
