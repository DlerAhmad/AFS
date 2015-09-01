import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class ServiceServer {

	public ServiceServer(){
		
	}
	
	public void start(){
		try {
			ServerSocket serverSocket=new ServerSocket(Constants.SERVICE_SERVER_PORT);
			
			for(;;){
			
			Socket socket = serverSocket.accept();

			
			new Thread(new ServiceServerHandler(socket)).start();
			
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		ServiceServer server =new ServiceServer();
		server.start();
		
		
	}

}
