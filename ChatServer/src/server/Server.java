package server;

import java.util.Properties;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.LinkedList;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
//import Test.ChatFrame;



public class Server {
	private int port;
	private Properties info;
	private ServerSocket serverSocket;
	private Map clients;
	
	public Server()
	{
		clients = new HashMap();
	}
	public Map getClients()
	{
		return clients;
	}
	private void setClients()
	{
		//
	}
	private void setEnvironment() 
	{
	   	try(FileInputStream fis = new FileInputStream("info.properties")) 
	   	{
	   		this.info = new Properties();
	   		this.info.load(fis);
	   		this.port = Integer.parseInt(info.getProperty("PORT"));
	   	} 
	   	catch(IOException ioe) 
	   	{
	   		ioe.printStackTrace(System.out);
	  	}
	}
	
	public void setServerSocket()
    {
    	try 
    	{
    		this.serverSocket = new ServerSocket(this.port);
    		System.out.println("WebServer Socket Created. PortNumber : " + this.port);    		
    		/*
    		 * ChatFrame serverFrame = new ChatFrame("Server");
    		 * 
    		 * 
    		 */
    	} 
    	catch(IOException ioe) 
    	{
    		ioe.printStackTrace(System.out);
    	}
    }
    
	
	public static void main(String args[]) throws Exception
    {
    	Server server = new Server();
    	server.setEnvironment();
    	server.setServerSocket();
    	
        Socket socket = null;
        List<Thread> list = new LinkedList<>();
        
        ServerThread serverThread;
        Thread thread;
        try 
        {
        	while(!Thread.currentThread().isInterrupted() && (socket = server.serverSocket.accept()) != null)
        	{
        		serverThread = new ServerThread(socket);
        		serverThread.start();
        		list.add(serverThread);
        		if(list.size() > 10)
        			break;
        	}
        } 
        catch(Exception e) 
        {
        	e.printStackTrace(System.out);
        } 
        finally 
        {
        	if(server.serverSocket != null)
        		server.serverSocket.close();
        	server.serverSocket = null;
        }

    }
}

/**
	Thread pool
	
	ServerSocket
	Socket
	
	$
	Server는 client 접속을 무한루프로 기다림.
			client의 write를 무한루프로 read함.
			모든 client에 synchronized write 해줌.(??)
	
	
	$server의
	read()에서 첫 바이트를 보고
	 readJoin() readChat() readQuit()으로 분기됨.
	그리고 각각 처리.
	
		readJoin()
			join 메시지를 읽고 해당 소켓의 name을 저장한다.
			client id 저장해둬야해.
		readChat()
			chat 메시지를 읽고 해당 소켓에서 온 채팅을 저장.
			채팅내용을 그대로 다른 client들에게 뿌려줌. server에는 표시X
		readQuit()
			
			
	readJoin() -> writeJoin()
		writeJoin() : server에   clientId + "님이 입장했습니다."
					  client에도 전부 뿌려줌.
	
	
	
	$	
	Server는 client에서 message를 받으면 그걸 그대로 모든 클라이언트로 보내기만 한다.
	각 client에서 받은 message 처리를 각각 해줘야한다.
		abc님>안녕하세요
		hkj님이 <파일>을 보냈습니다. <파일> 누르면 다운로드 됨.
		hkj님>hihi
		
	$작동
	1. server실행
	2. server에서 스레드 풀 생성. 무한루프 -> client 접속 기다림
	3. client 접속. server의 스레드 풀 중에 하나 받음.
		client는 접속 전에 자기 id를 설정해야함.
	4. server는 client의 write를 계속 read
	

	server client frame
	
	server에서 frame import
	server에 while문 두고 애들 대기하는거 -- frame에 전송하는 거
	
	

*/
