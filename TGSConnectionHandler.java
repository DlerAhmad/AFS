import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;


public class TGSConnectionHandler implements Runnable {
	private Socket socket;
	private String tgsSecretKey="sqpiqvbojhfrlrq51cd2geon45"; //later needs to be read from a file
	private String ServiceServerSecretKey="h144cipj0pdtos9f47odq7jv6a";
	public TGSConnectionHandler(Socket socket){
		this.socket=socket;

	}

	@Override
	public void run() {
		try{
			DataInputStream in=new DataInputStream(socket.getInputStream());
			DataOutputStream out=new DataOutputStream(socket.getOutputStream());

			// receiving the tgsTicket from client
			int tgsTicketByteSize=in.readInt();
			byte[] tgsTicketByte=new byte[tgsTicketByteSize];
			in.readFully(tgsTicketByte);

			// receiving the client authenticator
			int clientAuthenticatorSize=in.readInt();
			byte[] clientAuthenticatorEncrpted=new byte[clientAuthenticatorSize];
			in.readFully(clientAuthenticatorEncrpted);

			// decrypting the tgs ticket using tgs secret key
			Ticket tgsTicket=new Ticket(EncryptionManager.decrypt(tgsTicketByte, EncryptionManager.generateSecretKey(tgsSecretKey)));

			// obtaining the client/tgs session key
			String clientTgsSessionKey=tgsTicket.getClientSessionKey();

			//decryping the client authenticator using client.tgs session key
			Authenticator clientAuthenticator=new Authenticator(EncryptionManager.decrypt(clientAuthenticatorEncrpted,EncryptionManager.generateSecretKey(clientTgsSessionKey)));

			
			// Authenticating the user using the time stamp in the authenticator time stamp user id in both received
			long currentTime=System.currentTimeMillis();
			if (clientAuthenticator.getTimeStamp()-currentTime>Constants.CLIENT_AUTHENTICATION_VALIDITY_TIME || !clientAuthenticator.getId().equals(tgsTicket.getClientId())){
				out.writeInt(-1);

			}else{
				out.writeInt(1);

				// generating client/Server session key
				String clientServerSessionKey=EncryptionManager.generateSessionKey();
				// generating the client/service server ticket and encrypting it using the service server secret key and sending to user

				Ticket clientServerTicket=new Ticket(tgsTicket.getClientId(),tgsTicket.getClientIp(),Constants.CLIENT_SERVER_TICKET_VALIDITY_TIME,clientServerSessionKey);

				int clientServerTicketByteSize=EncryptionManager.encrypt(clientServerTicket.toString(),EncryptionManager.generateSecretKey(ServiceServerSecretKey)).length;
				out.writeInt(clientServerTicketByteSize);
				out.write(EncryptionManager.encrypt(clientServerTicket.toString(),EncryptionManager.generateSecretKey(ServiceServerSecretKey)));

				System.out.println("A client/service server ticket has been sent to "+socket.getRemoteSocketAddress().toString());

				
				// encrypting client/Server session key using the client/tgs session key and sending to user
				int clientServerSessionKeySize=EncryptionManager.encrypt(clientServerSessionKey,EncryptionManager.generateSecretKey(clientTgsSessionKey)).length;
				out.writeInt(clientServerSessionKeySize);
				out.write(EncryptionManager.encrypt(clientServerSessionKey,EncryptionManager.generateSecretKey(clientTgsSessionKey)));

				System.out.println("A client/service server session key  has been sent to "+socket.getRemoteSocketAddress().toString());

				socket.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
