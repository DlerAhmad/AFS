import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


public class ServiceServerHandler implements Runnable {

	private Socket socket;
	private String ServiceServerSecretKey="h144cipj0pdtos9f47odq7jv6a";

	private Ticket clientTicket;
	private Authenticator clientAuthenticator;

	public ServiceServerHandler(Socket socket){
		this.socket=socket;
	}

	private static boolean isTicketValid(Ticket ticket,Authenticator authenticator){
		if(System.currentTimeMillis()-authenticator.getTimeStamp()<ticket.getTicketValidityPeriod()){
			return true;
		}else{
			return false;
		}
	}
	@Override
	public void run() {
		try{
			DataInputStream in=new DataInputStream(socket.getInputStream());
			DataOutputStream out=new DataOutputStream(socket.getOutputStream());

			//receiving SSticket from client
			int clientServerTicketByteSize=in.readInt();
			byte[] clientServerTicketByte=new byte[clientServerTicketByteSize];
			in.readFully(clientServerTicketByte);

			//receiving authenticator from client
			int clientAuthenticatorSize=in.readInt();
			byte[] clientAuthenticator=new byte[clientAuthenticatorSize];
			in.readFully(clientAuthenticator);

			//decrypting the clientTicket
			this.clientTicket=new Ticket(EncryptionManager.decrypt(clientServerTicketByte, EncryptionManager.generateSecretKey(ServiceServerSecretKey)));

			//decypting the client authenticator
			this.clientAuthenticator=new Authenticator(EncryptionManager.decrypt(clientAuthenticator,EncryptionManager.generateSecretKey(this.clientTicket.getClientSessionKey())));

			if (this.clientTicket.getClientId().equals(this.clientAuthenticator.getId())){


				String clientResult=Long.toString(this.clientAuthenticator.getTimeStamp()+1);

				int clientResultSize=EncryptionManager.encrypt(clientResult,EncryptionManager.generateSecretKey(this.clientTicket.getClientSessionKey())).length;
				out.writeInt(clientResultSize);
				out.write(EncryptionManager.encrypt(clientResult,EncryptionManager.generateSecretKey(this.clientTicket.getClientSessionKey())));
				System.out.println("The ticket of "+socket.getRemoteSocketAddress().toString()+" is valid.");


				int requestNumber=in.readInt();
				while(requestNumber!=0){
					if(isTicketValid(this.clientTicket,this.clientAuthenticator)){
						out.writeInt(0); //ticket valid
					
						if(requestNumber==1){

							String path=in.readUTF();
							Path path1=Paths.get(path);
							int r=FileOperations.create(path1);
							out.writeInt(r);
						}else if(requestNumber==2){
							String path=in.readUTF();

							String content=FileOperations.open(path);
							out.writeUTF(content);

						}else if(requestNumber==3){


							String path=in.readUTF();
							if(!Files.exists(Paths.get(path))){
								out.writeInt(-1);

							}else{
								out.writeInt(0);

								String input=in.readUTF();

								//System.out.println(input);
								FileOperations.append(path,input);
								//System.out.println(r);
							}



						}else if(requestNumber==4){
							String source=in.readUTF();
							if(!Files.exists(Paths.get(source))){
								out.writeInt(-1);

							}else{
								out.writeInt(-2);

								String target=in.readUTF();
								//System.out.print("this is in 1");
								FileOperations.copy(source, target);
							}
							//System.out.print("this is in 2");
						}else if(requestNumber==5){
							String source=in.readUTF();
							if(!Files.exists(Paths.get(source))){
								out.writeInt(-1);

							}else{
								out.writeInt(-2);

								String target=in.readUTF();
								FileOperations.move(source, target);
							}

						}else if(requestNumber==6){
							String path=in.readUTF();
							FileOperations.delete(path);

						}else if(requestNumber==7){
							String path=in.readUTF();
							String result=FileOperations.list(path);
							out.writeUTF(result);
						}else if(requestNumber==8){
							String path=in.readUTF();

							int r=FileOperations.delete(path);
							out.writeInt(r);

						}else if(requestNumber==9){

							String path=in.readUTF();
							int r=FileOperations.createDir(path);
							out.writeInt(r);
						}else if (requestNumber==10){ // doing nothin.exiting the system..

						}else{
							System.out.println("not ganna happen!");
						}
						requestNumber=in.readInt();
					}else{
						out.writeInt(-1);  //ticket not valid
					}

				}
			}else{

				String clientResult=Long.toString(this.clientAuthenticator.getTimeStamp()-1);

				int clientResultSize=EncryptionManager.encrypt(clientResult,EncryptionManager.generateSecretKey(this.clientTicket.getClientSessionKey())).length;
				out.writeInt(clientResultSize);
				out.write(EncryptionManager.encrypt(clientResult,EncryptionManager.generateSecretKey(this.clientTicket.getClientSessionKey())));


			}
		}catch(Exception e){
			//e.printStackTrace();	
		}

	}

}
