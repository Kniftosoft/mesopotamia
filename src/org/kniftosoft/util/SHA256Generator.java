package org.kniftosoft.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author julian
 * 
 */
public class SHA256Generator {

	final protected static char[] hexArray = "0123456789abcdef".toCharArray();

	/**
	 * @param bytes
	 * @return hexChars Converts bytes to Hex String
	 */
	private static String bytesToHex(byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	/**
	 * @param message
	 * @return SHA156 Parse Message to SHA256
	 */
	public static String StringTOSHA256(String message) {
		MessageDigest md;
		try {

			md = MessageDigest.getInstance("SHA-256");

		} catch (final NoSuchAlgorithmException e) {
			e.printStackTrace();
			return "";
		}
		md.update(message.getBytes());
		return new String(bytesToHex(md.digest()));
	}

}
