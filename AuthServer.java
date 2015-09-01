import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class AuthServer {

	


	public AuthServer(){
		
	}
	
	public void start(){
		try {
			ServerSocket serverSocket=new ServerSocket(Constants.AUTH_SERVER_PORT);
			
			for(;;){
			
			Socket socket = serverSocket.accept();

			
			new Thread(new AuthServerConnectionHandler(socket)).start();
			
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		AuthServer server =new AuthServer();
		server.start();
		
		
	}

}
