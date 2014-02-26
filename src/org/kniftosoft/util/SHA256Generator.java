/**
 * 
 */
package org.kniftosoft.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author julian
 *
 */
public class SHA256Generator {

		
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes)
	{
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ )
		{
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
	return new String(hexChars);
	}
	public static String StringTOSHA256(String message)
	{
		MessageDigest md;
		 try {
				
	            md = MessageDigest.getInstance("SHA-256");
	
	        } catch (NoSuchAlgorithmException ex) {
	            System.out.println(ex.getMessage());
	            return "";
	        }
		 md.update(message.getBytes());
	return new String(bytesToHex(md.digest()));
	}
	/**
	 * 
	 */
	public SHA256Generator() {
		// TODO Auto-generated constructor stub
	}

}
