package post;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Properties;
import java.io.FileInputStream;

//@WebServlet("/PostImages")
public class PostImages extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private String webRoot;   
    private boolean imageError;
    
    public PostImages() {
        super();

    }
    /*
    
    private void getSettings() 
    {
    	StringBuilder webPath = new StringBuilder(getServletContext().getRealPath("/"));
        
    	try(FileInputStream fis = new FileInputStream(webPath.append("/info.properties").toString()))
    	{
    		Properties info = new Properties();
    		info.load(fis);
    		this.webRoot = info.getProperty("WEBROOT");
    	} 
    	catch(IOException ioe)
    	{
    		//예외 처리 필요
    		ioe.printStackTrace(System.out);
    	}
    }
    
    private int getNumberOfFile() 
    {
    	StringBuilder fileName = new StringBuilder()
    }
    private boolean isImageError() 
    {
    	
    	
    }
    private boolean getImageError() 
    {
    	return this.imageError;
    }
    */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		//
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		
		
		
	}

}
