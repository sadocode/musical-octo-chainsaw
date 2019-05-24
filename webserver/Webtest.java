package webserver;
import java.net.ServerSocket;
import java.net.Socket;

public class Webtest
{
    public static void main(String args[]) throws Exception
    {
        int port = 8000;
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("WebServer Socket Created");

        Socket socket;
        NewServerThread serverThread;
        
        while((socket = serverSocket.accept()) != null)
        {
            serverThread = new NewServerThread(socket);
            serverThread.start();
        }
    }
}
