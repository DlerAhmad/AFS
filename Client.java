import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.Console;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class Client {

	private String cachCopy;
	private String tgsSessionKey;
	private boolean isLoggedIn=false;
	int ticketValidationCode=1;
	DataInputStream in1;
	DataOutputStream out1;
	DataInputStream in2;
	DataOutputStream out2;
	DataInputStream in3;
	DataOutputStream out3;


	public void showMenu(){

		Scanner in =new Scanner(System.in);
		System.out.println("Welcome to the System!");
		System.out.println("-login\n-exit\n**********");
		String command;

		while (true) {
			command = in.next();
			if (!isLoggedIn){	
				if (command.equals("exit")) {

					System.out.println("Thanks for using the system.");
					System.exit(0);

				} else if (command.equals("login")) {

					// if not already logged in,it will log in

					Console console=System.console();
					System.out.print("username:");
					String id=in.next();
					System.out.print("password:");

					char[] pass=console.readPassword();
					String password=String.valueOf(pass);
					//System.err.println(password);

					//String password=in.next();
					int result=login(id,password); 

					if (result==0){
						System.err.println("Invalid username or password!");

					}else if (result==1){		
						System.out.println("You are successfully authenticated by kerberos server. ");
						isLoggedIn=true;
						System.out.println("-create\n-open\n-Append\n-Copy"
								+ "\n-Move\n-Delete\n-List\n-createdir\n-deletedir\n-logout\n**********");

					}else if (result==-1){

					}	
				}else if (command.equals("logout")) {
					System.err.println("You are already logged out.");
				}
				/*
				}else{										// if already logged in, check for validity

					System.err.println("Already logged in. ");

			}
				 */

			}else if (this.isLoggedIn){
				if(command.equals("login")){
					System.err.println("Already logged in. ");

				}else if(command.equals("create")){

					System.out.println("enter the path");
					String path = in.next();
					//send to server
					try {
						out3.writeInt(1);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);
							int r=in3.readInt();
							if(r==-1){
								System.out.println("The file is already exist!");

							}else{
								System.out.println("The file is created in "+path);
							}
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					//get a yes or no


					//create(file1);

				}else if(command.equals("open")){

					System.out.println("enter the path");

					String path = in.next();
					try {
						out3.writeInt(2);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);
							String r=in3.readUTF();

							if(r.equals("NO")){
								System.out.println("The file did not exist. A new file was created.");

							}else{
								this.cachCopy=r;
								System.out.println(cachCopy);
							}
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}


				}else if(command.equals("append")){

					System.out.println("enter the path");
					String path = in.next();

					try {
						out3.writeInt(3);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);
							int r=in3.readInt();
							if(r==-1){
								System.out.println("No content added! The path may be wrong." );

							}else{

								//out3.writeUTF(input);
								//String contents=FileOperations.readFile(path, Charset.defaultCharset());
								String contents=FileOperations.append(path);
								out3.writeUTF(cachCopy+"\n"+contents);
								//System.out.println(contents);

								System.out.println("The input was appended to the file." );
							}
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				else if(command.equals("copy")){
					System.out.println("please enter source path:");
					String sour = in.next();
					try {
						out3.writeInt(4);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(sour);
							int r=in3.readInt();
							//System.out.println(r3);

							if(r==-1){
								System.err.println("File can not be copied.The file may not exist!" );

							}else if(r==-2){
								System.out.println("please enter target path:");
								String tar = in.next();




								out3.writeUTF(tar);


								System.out.println("The file was copied." );
							}
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}else if(command.equals("move")){

					System.out.println("please enter source path:");
					String sour = in.next();
					try {
						out3.writeInt(5);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(sour);
							int r = in3.readInt();
							//System.out.println(r2);
							if(r==-1){
								System.out.println("File does not exist!");
							}else if(r==-2){
								System.out.println("please enter target path:");
								String tar = in.next();
								out3.writeUTF(tar);
								System.out.println("The file was moved.");
							}else{
								this.isLoggedIn=false;
							}
						} 
					}catch (IOException e) {
						e.printStackTrace();
					}

				}else if(command.equals("delete")){
					System.out.println("enter the path");
					String path = in.next();

					try {
						out3.writeInt(6);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);
							
							int r =in3.readInt();
							 if(r==-2){
								System.out.println("No such file exist!" );
							}else{
								System.out.println("file was deleted." );

							}
						

						
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}


				}else if(command.equals("list")){
					System.out.println("enter the path");
					String path = in.next();
					try {
						out3.writeInt(7);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);
							String result=in3.readUTF();
							if(result.equals("NO")){
								System.out.println("An error happend while listing. Directory may not exist.");

							}else{
								System.out.println(result);
							}
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}else if(command.equals("deletedir")){
					System.out.println("enter the path");
					String path = in.next();

					try {
						out3.writeInt(8);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);
							int r=in3.readInt();
							if(r==-1){
								System.out.println("The directory can not be deleted.It is not empty!" );
							}else if(r==-2){

								System.out.println("No such directory exist!" );
							}else{
								System.out.println("Directory was deleted." );

							}
						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}else if(command.equals("createdir")){
					System.out.println("enter the path");
					String path = in.next();

					try {
						out3.writeInt(9);
						ticketValidationCode=in3.readInt();
						if (ticketValidationCode!=-1){
							out3.writeUTF(path);

							int r=in3.readInt();
							if(r==-1){
								System.out.println("The directory already exist!" );
							}else{
								System.out.println("The directory was created." );	
							}

						}else{
							this.isLoggedIn=false;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}else if (command.equals("logout")) {
					if(isLoggedIn){
						isLoggedIn=false;
						System.out.println("You successfully logged out.");
						System.out.println("-login\n-exit\n**********");
					}else{
						System.err.println("You are already logged out.");

					}
				}else{
					System.err.println("Invalid command!");
				}
			}else{
				/*
				if(command.equals("logout")){
					System.err.println("You are already logged out.");
				}else if (command.equals("exit")) {
					System.out.println("Thanks for using the system.");
					System.exit(0);

				}else{
				 */
				System.out.println("You ticket is expired. You are logged out. Please log in to continue");
				System.out.println("-login\n-exit\n**********");


			}
		}
	}


	/*
	 * returns	1 if successful log in
	 * 			-1 if unsuccessful log in
	 */

	private int login(String id, String pass){





		try {
			// connecting to Auth. server
			Socket socket1= new Socket(Constants.AUTH_SERVER_HOST, Constants.AUTH_SERVER_PORT);

			in1=new DataInputStream(socket1.getInputStream());
			out1=new DataOutputStream(socket1.getOutputStream());

			//out.writeInt(1);

			//byte[] byteID=id.getBytes();

			out1.writeUTF(id);

			//String reply=in.readUTF();
			//System.out.println(reply);

			//out.writeUTF(pass);
			//reply=in.readUTF();
			//System.out.println(reply);

			// receiving the session key msg
			int sessionKeyByteSize=in1.readInt();

			byte[] sessionKeyByte=new byte[sessionKeyByteSize];
			in1.readFully(sessionKeyByte);


			// obtaining the session key
			tgsSessionKey= EncryptionManager.decrypt(sessionKeyByte,EncryptionManager.generateSecretKey(pass));

			// receiving the tgs ticket msg
			int tgsTicketByteSize=in1.readInt();

			byte[] tgsTicketByte=new byte[tgsTicketByteSize];

			in1.readFully(tgsTicketByte); 

			// at this point client needs to communicate with TGS , sending the ticket and a new authenticator

			//establishing the connection with TGS 

			Socket socket2=new Socket(Constants.TG_SERVER_HOST,Constants.TGS_PORT);
			in2=new DataInputStream(socket2.getInputStream()); 
			out2=new DataOutputStream(socket2.getOutputStream());

			// forwarding the tgs ticket to tgs server 
			out2.writeInt(tgsTicketByteSize);
			out2.write(tgsTicketByte);


			// generating and sending an authenticator to send to tgs server
			Authenticator authenticator=new Authenticator(id,EncryptionManager.generateTimeStamp().getTime());
			out2.writeInt(EncryptionManager.encrypt(authenticator.toString(),EncryptionManager.generateSecretKey(tgsSessionKey)).length);
			out2.write(EncryptionManager.encrypt(authenticator.toString(),EncryptionManager.generateSecretKey(tgsSessionKey)));

			// receiving the authentication code( -1 for unvalidity and 1 for validity)
			int clientAuthValidityCode=in2.readInt();
			if (clientAuthValidityCode==-1){
				// client is not authenticated
				return -1;

			}else if (clientAuthValidityCode==1){
				//System.out.println("The user is authenticated by TGS ");

				// receiving Service server ticket
				int  clientServerTicketByteSize=in2.readInt();
				byte[] clientServerTicketByte=new byte[clientServerTicketByteSize];
				in2.readFully(clientServerTicketByte);

				// receiving client/server session key
				int  clientServerSessionKeySize=in2.readInt();
				byte[] clientServerSessionKeyByte=new byte[clientServerSessionKeySize];
				in2.readFully(clientServerSessionKeyByte);

				//decrypting clientServerSessionKey
				String clientServerSessionKey=EncryptionManager.decrypt(clientServerSessionKeyByte,EncryptionManager.generateSecretKey(tgsSessionKey));

				//stablishing connection with service serve
				Socket socket3=new Socket(Constants.SERVICE_SERVER_HOST,Constants.SERVICE_SERVER_PORT);
				in3=new DataInputStream(socket3.getInputStream()); 
				out3=new DataOutputStream(socket3.getOutputStream());

				//forwarding the clientServerTicket to service server
				out3.writeInt(clientServerTicketByteSize);
				out3.write(clientServerTicketByte);

				Authenticator authenticator2=new Authenticator(id,EncryptionManager.generateTimeStamp().getTime());
				// sending a new authenticator encrypted in clientServerSessionKey to service server
				out3.writeInt(EncryptionManager.encrypt(authenticator2.toString(),EncryptionManager.generateSecretKey(clientServerSessionKey)).length);
				out3.write(EncryptionManager.encrypt(authenticator2.toString(),EncryptionManager.generateSecretKey(clientServerSessionKey)));

				// The SS decrypts the ticket using its own secret key to retrieve the Client/Server Session Key.
				//Using the sessions key, SS decrypts the Authenticator and sends the following message to the 
				//client to confirm its true identity and willingness to serve the client:
				// (the timestamp found in client's Authenticator plus 1, encrypted using the Client/Server Session Key)

				//
				//receiving the confirmation from server and decrypting it using SS session key

				int clientResultSize=in3.readInt();
				byte[] clientResult=new byte[clientResultSize];
				in3.readFully(clientResult);

				//decrypting the result from SS
				long result=Long.valueOf(EncryptionManager.decrypt(clientResult,EncryptionManager.generateSecretKey(clientServerSessionKey)));
				if(result==authenticator2.getTimeStamp()+1){
					//isLoggedIn=true;
					System.out.println("Client ready to communicate with service server..");


					return 1;
				}else if(result==authenticator2.getTimeStamp()-1){

					System.out.println("Client is not authenticated!");
					return -1;
				}else{
					System.out.println("result:"+result);
					System.out.println("authenticator.getTimeStamp(): "+authenticator.getTimeStamp());
					System.out.println("This is not gonna happen!");
				}
				// closing all sockets

				socket1.close();
				socket2.close();

			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			return 0;
			//e.printStackTrace();
		}
		return 1;

	}


}
