import java.net.Socket;
import java.net.ServerSocket;
import java.util.List;
import java.util.ArrayList;

public class Server {
	private Socket client;
	private ServerSocket server;
	private static int port;
	private List threadList;

	public Server()
	{
		this.port = 10033;
		this.threadList = new ArrayList();
	}
	
	public static void main(String args[])
	{
		//this.server = new ServerSocket(this.port);
		//serversocket, socket 초기화
		//새로운 client면 소켓 반환
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
	
	$
	client -> server로 가는 메시지 종류
	1. join
	2. data
	3. quit
	
	$1.join
	client가 접속할 때, server로 보내는 메시지
	
	
	$2.data		
	데이터 형식
		바이트로 송수신
		0-23바이트 : client의 id
		24바이트      : 보내는 데이터의 타입. 0 : text, 1 : file
		25바이트     : 보내는 데이터의 형식.
		 즉 24 25 바이트로 .
		 	image도 file로 취급해서 할 것!
			00 : text/plain(채팅)
			10 : image/jpg, 11 : image/bmp, 12 : image/png
			20 : file/txt, 21 : file/html, 22 : file/js, 23 : file/css, 24 : file/java, 25 : file/zip			
		26 바이트부터는 데이터의 내용.
		데이터의 끝에는 @finish@ 를 붙여줌.
			40 66 69 6e 69 73 68 40
			@  f  i  n  i  s  h  @
	
	
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
	

	

*/
