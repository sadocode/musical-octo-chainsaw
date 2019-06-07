package get;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.lang.Process;
import java.lang.ProcessBuilder;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.FileInputStream;

@WebServlet("/Server")
public class GetWords extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public GetWords() {
        super();
      
    }
    
    /**
     * index.jsp에서 GET 요청으로 온 string값을 받아서, 그 값에 따른 결과를 String으로 리턴해준다.
     * 정해진 명령어가 아닌 경우에는 그 자신을 리턴한다.
     * 정해진 명령어가 입력된 경우에는 명령어에 따라 다르게 동작하고, 그에 따른 String을 리턴해준다.
     * @param words
     * @return 입력값에 따라 입력값을 혹은 다른 값을 리턴해준다.
     */
    private String detectWords(String words) 
    {
    	if(words == null)
    		throw new java.lang.NullPointerException("detectWords() string is null");
    	
    	if(!isWords(words)) 
    	{
    		return words;
    	}
    	else
    	{
    		return getResponse(words);
    	}
    }
    
    /**
     * 입력받은 String이 정해진 명령어에 해당되면 true, 아니면 false 반환하는 메소드
     * @param words
     * @return 입력 값이 dir, notepad, screen에 해당되면 true. 나머지는 false 반환.
     */
    private boolean isWords(String words)
    {
    	if(words == null)
    		throw new java.lang.NullPointerException("detectWords() string is null");
    	
    	words = words.toLowerCase();
    	String[] instructors = {"dir", "notepad", "screen"};
    	for(int i = 0; i < instructors.length; i++) 
    	{
    		if(instructors[i].equals(words))
    			return true;
    	}
    	return false;
    }
    
    
    
    /**
     * 입력된 String(명령어)에 따른 각각의 메소드를 실행하고, 이 결과를 리턴해준다.
     * @param words
     * @return response
     */
    private String getResponse(String words) {
    	if(words == null) 
    		throw new java.lang.NullPointerException("getResponse() s is null");
    	String response = null;
    	switch(words) 
    	{
    	case "dir":
    		response = cmdCase(words);
    		break;
    	case "notepad":
    		response = cmdCase(words);
    		break;
    	case "screen":
    		response = screenCase();
    		break;
    	default:
    		throw new java.lang.IllegalArgumentException();
    	}
    	return response;
    }
    
    private String cmdCase(String words) {
    	
    	
    }
    
    private String screenCase() throws AWTException, IOException{
    	Date captureTime = new Date();
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_kkmmss");
    	
    	StringBuilder filepath = new StringBuilder(this.default_path);
    	filepath.append("/temp/").append(sdf.format(captureTime)).append(".bmp");
    	Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
    	Robot robot = new Robot();
    	BufferedImage capture = robot.createScreenCapture(screenRect);
    	this.screen = filepath.toString();
    	ImageIO.write(capture, "bmp", new File(this.screen));
    		
    	return this.screen;
    	
    	
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		this.detectWords(request.getParameter("words"));
		PrintWriter out = response.getWriter();
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		//
	}

}
