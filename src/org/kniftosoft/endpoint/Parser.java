/**
 * 
 */
package org.kniftosoft.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author julian
 *
 */
public class Parser {

	/**
	 * 
	 */
	public Parser() {
		// TODO Auto-generated constructor stub
	}
	public static JsonObject parse(String msg)
	{
		JsonParser parser = new JsonParser();
		System.out.println("parse: "+msg);
		System.out.flush();
		JsonObject packet = (JsonObject) parser.parse(msg);
		
		return packet;
		
	}

}
