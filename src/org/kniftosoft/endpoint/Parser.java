package org.kniftosoft.endpoint;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author julian
 * 
 */
public class Parser {

	/**
	 * Parse String to Packet
	 * @param msg
	 * @return packet
	 */
	public static JsonObject parse(String msg) {
		final JsonParser parser = new JsonParser();
		System.out.println("parse: " + msg);
		System.out.flush();
		final JsonObject packet = (JsonObject) parser.parse(msg);

		return packet;

	}

}
