// reference: https://examples.javacodegeeks.com/core-java/crypto/generate-message-authentication-code-mac/
// addapted code

package cryptography;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

public class MACGenerator<ARG> {

	public byte[] generateMAC(ARG... args) {
		try {

			// get a key generator for the HMAC-MD5 keyed-hashing algorithm
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacMD5");

			// generate a key from the generator
			SecretKey key = keyGen.generateKey();

			// create a MAC and initialize with the above key
			Mac mac = Mac.getInstance(key.getAlgorithm());
			mac.init(key);

			String message = "";
			for (ARG arg : args) {
				message += arg;
			}

			// get the string as UTF-8 bytes
			byte[] b = message.getBytes("UTF-8");

			// create a digest from the byte array
			byte[] digest = mac.doFinal(b);
			return digest;

		} catch (NoSuchAlgorithmException e) {
			System.out.println("No Such Algorithm:" + e.getMessage());
			return null;
		} catch (UnsupportedEncodingException e) {
			System.out.println("Unsupported Encoding:" + e.getMessage());
			return null;
		} catch (InvalidKeyException e) {
			System.out.println("Invalid Key:" + e.getMessage());
			return null;
		}
	}
}
