import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.Timestamp;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class EncryptionManager {

	private static SecureRandom random = new SecureRandom();
	
	protected static String generateSessionKey(){
		//System.out.println("random= "+random);

		//BigInteger(int numBits, Random rnd)   : Constructs a randomly generated BigInteger
		return new BigInteger(130, random).toString(32); //String representation of this BigInteger in radix 32.
	}

	protected static SecretKeySpec generateSecretKey(String str) throws NoSuchAlgorithmException{
		MessageDigest digest;

		digest = MessageDigest.getInstance("SHA"); //generates hash value using secure hash algorithm
		digest.update(str.getBytes());
		SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

		return key;
	}

	protected static byte[] encrypt(String str, SecretKeySpec key) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.ENCRYPT_MODE, key);
		byte[] cipherText = aes.doFinal(str.getBytes());
		return cipherText;

	}

	protected static String decrypt(byte[] cipherText, SecretKeySpec key) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException{
		Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
		aes.init(Cipher.DECRYPT_MODE, key);
		String clearText = new String(aes.doFinal(cipherText));
		return clearText;
	}

	protected static Timestamp generateTimeStamp(){

		long time=System.currentTimeMillis();
		return new Timestamp(time);
	}
}
