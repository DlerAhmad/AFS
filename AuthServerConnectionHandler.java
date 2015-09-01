import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.Socket;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;


public class AuthServerConnectionHandler implements Runnable{

	private Socket socket;

	private String tgsSecretKey="sqpiqvbojhfrlrq51cd2geon45"; //later needs to be read from a file

	AuthServerConnectionHandler(Socket socket){
		this.socket=socket;

	}

	private String tgsSecretKey(){
		return "key";
	}

	private String findPassword(String clientId) {
		boolean isPassFound=false;
		String username;
		String password="";
		try
		{
			FileInputStream fstream = new FileInputStream(Constants.USER_INFO_PATH);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			
			br.readLine(); // skipping the first line
			
			while((username = br.readLine())!= null)
			{
				//System.out.println("client id ="+clientId);
				//System.out.println("username="+username);
				if(username.equals(clientId)){
					//System.out.println(clientId);
					password=br.readLine();

				}else{
					br.readLine();
				}
			}
			br.close();
			fstream.close();

		}
		catch (FileNotFoundException e)
		{
			System.err.println("Error: " + e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return password;

	}


	@Override
	public void run() {

		try {

			DataInputStream in=new DataInputStream(socket.getInputStream());
			DataOutputStream out=new DataOutputStream(socket.getOutputStream());


			byte[] byteID;
			String clientId= in.readUTF();
			//out.writeUTF(userId+" has been received.");

			//String pass=in.readUTF();
			//out.writeUTF(pass+" has been received.");

			// look up the id here and return password (using DB or file)
			// we assume at this point that the pass is "password"

			String clientPassword=findPassword(clientId);
			//System.out.println("found password=" +clientPassword);
			
			// generating a session key for client to communicate with TGS
			String clientTgsSessionKey=EncryptionManager.generateSessionKey();
			//System.out.println("sessionKey="+clientTgsSessionKey);

			/*
			 *  The AS generates the secret key by hashing the password of the user found at the database
			 *  and encrypt the generated session key with user secret key and send it to user  
			 */

			int sessionKeyByteSize=EncryptionManager.encrypt(clientTgsSessionKey,EncryptionManager.generateSecretKey(clientPassword)).length;
			out.writeInt(sessionKeyByteSize);
			//System.out.println("sessionKeyByteSize="+sessionKeyByteSize);
			out.write(EncryptionManager.encrypt(clientTgsSessionKey,EncryptionManager.generateSecretKey(clientPassword)));

			System.out.println("A client/TGS session key  has been sent to "+socket.getRemoteSocketAddress().toString());
			//System.out.println(decrypt(encrypt(sessionKey,generateSecretKey(userPassword))));
			/*
			 *  now AS sends TGS ticket as well. This ticket needs to be encrypted using TGS secret key.
			 *  TGS secret key is known to AS.
			 *  ticket includes ( user id, user ip, ticket life time (s), session key)  
			 */



			//System.out.println("client ip="+socket.getRemoteSocketAddress().toString());

			Ticket tgsTicket= new Ticket(clientId, socket.getRemoteSocketAddress().toString(),Constants.CLIENT_SERVER_TICKET_VALIDITY_TIME , clientTgsSessionKey);

			// sending ticket encrypted in TGS secret key

			int tgsTicketByteSize=EncryptionManager.encrypt(tgsTicket.toString(),EncryptionManager.generateSecretKey(tgsSecretKey)).length;
			//System.out.println("tgsTicketByteSize="+tgsTicketByteSize);
			out.writeInt(tgsTicketByteSize);

			out.write(EncryptionManager.encrypt(tgsTicket.toString(),EncryptionManager.generateSecretKey(tgsSecretKey)));

			System.out.println("A TGS ticket has been sent to "+socket.getRemoteSocketAddress().toString());
			/* Oid 1.2.840.113554.1.2.2 is Kerberos v5 GSS-API mechanism which is 
			 * defiened in RFC 1964 (request for command) in The Internet Engineering Task Force (IETF) org.
			 * 
			 */
			/*

			Oid krb5Mechanism = new Oid("1.2.840.113554.1.2.2");

			// GSSManager includes the GSS api classes
			GSSManager gssManager = GSSManager.getInstance();

			// Identify the client to gss manager
			GSSName clientId = gssManager.createName(userId,GSSName.NT_USER_NAME);

			/// Identify the name of the service to the gss manager

			GSSName serviceName = gssManager.createName("auth/glados.cs.rit.edu", null);

			// get the client's credentials
			GSSCredential clientCredentials = gssManager.createCredential(clientId, 12*60*60, krb5Mechanism, GSSCredential.INITIATE_ONLY);

			// create a security context between the client and the service
			GSSContext gssContext = gssManager.createContext(serviceName,krb5Mechanism,clientCredentials,12*60*60);

			encrypt(gssContext,generateSecretKey("asas"));


			// creating the service ticket (TGS)
			byte[] serviceTicket = gssContext.initSecContext(new byte[0], 0, 0);
			gssContext.dispose();



			 */

			socket.close();


		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}


	}
	public static void main(String args[]){
		GSSManager gssManager = GSSManager.getInstance();
		String name="client@DOMAIN.COM";
		try {
			GSSName userName = gssManager.createName(name, GSSName.NT_USER_NAME);
			//String str=clientID.toString();
			System.out.println(userName);
		} catch (GSSException e) {
			e.printStackTrace();
		}



	}


}
