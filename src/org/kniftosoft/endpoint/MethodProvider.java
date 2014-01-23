/**
 * 
 */
package org.kniftosoft.endpoint;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class MethodProvider {

	static JsonObject _default(JsonObject data){
		JsonObject answer = new JsonObject();
		return answer;
	}
	static JsonObject test1(JsonObject data){
		JsonObject answer = new JsonObject();
		System.out.println("test1 called ");
		return answer;
	}
}
