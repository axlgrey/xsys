package com.xsys.api;

import android.util.Base64;

import java.security.SecureRandom;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class LibEncrypt {
	private static final String salt = "tu89geji340t89u2";
	private static final String password = "hau274hUAafa";

	public static String Encrypt_256(String inText) {
		/* Derive the key, given password and salt. */
		SecretKey secret = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);
			SecretKey tmp = factory.generateSecret(spec);
			secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES secret key spec error");
		}

		/* Encrypt the message. */
		byte[] ciphertext = null;
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, secret);
			ciphertext = c.doFinal(inText.getBytes());
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES encryption error");
		}

		return Base64.encodeToString(ciphertext, Base64.DEFAULT);
	}

	public static String Decrypt_256(String inText) {
		/* Derive the key, given password and salt. */
		SecretKey secret = null;
		try {
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
			KeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), 1024, 256);
			SecretKey tmp = factory.generateSecret(spec);
			secret = new SecretKeySpec(tmp.getEncoded(), "AES");
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES secret key spec error");
		}

		/* Decrypt the message, given derived key and initialization vector. */
		byte[] ciphertext = Base64.decode(inText, Base64.DEFAULT);
		String plaintext = null;
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, secret);
			plaintext = new String(c.doFinal(ciphertext));
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES decryption error");
		}

		return plaintext;
	}

	public static String Encrypt_128(String inText) {
		// Set up secret key spec for 128-bit AES encryption and decryption
		SecretKeySpec sks = null;
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(salt.getBytes());
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128, sr);
			sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES secret key spec error");
		}

		// Encode the original data with AES
		byte[] encodedBytes = null;
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.ENCRYPT_MODE, sks);
			encodedBytes = c.doFinal(inText.getBytes());
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES encryption error");
		}

		return Base64.encodeToString(encodedBytes, Base64.DEFAULT);
	}

	public static String Decrypt_128(String inText) {
		// Set up secret key spec for 128-bit AES encryption and decryption
		SecretKeySpec sks = null;
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			sr.setSeed(salt.getBytes());
			KeyGenerator kg = KeyGenerator.getInstance("AES");
			kg.init(128, sr);
			sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES secret key spec error");
		}

		// Decode the encoded data with AES
		byte[] encodedBytes = Base64.decode(inText, Base64.DEFAULT);
		byte[] decodedBytes = null;
		try {
			Cipher c = Cipher.getInstance("AES");
			c.init(Cipher.DECRYPT_MODE, sks);
			decodedBytes = c.doFinal(encodedBytes);
		} catch (Exception e) {
			LibLogger.writeLog(LibLogger.ID_ERROR, "AES decryption error");
		}

		return new String(decodedBytes);
	}
}
