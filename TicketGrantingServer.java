import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class TicketGrantingServer {

	
	DataInputStream in;
	DataOutputStream out;

	public void start(){
		try {
			ServerSocket serverSocket=new ServerSocket(Constants.TGS_PORT);
			
			for(;;){
			
			Socket socket = serverSocket.accept();

			new Thread(new TGSConnectionHandler(socket)).start();
			
			
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String args[]){
		TicketGrantingServer tgsServer =new TicketGrantingServer();
		tgsServer.start();
		
		
	}
}
