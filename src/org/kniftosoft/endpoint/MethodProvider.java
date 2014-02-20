/**
 * 
 */
package org.kniftosoft.endpoint;

import org.kniftosoft.Login.Loginmanager;
import org.kniftosoft.entity.EuphratisSession;

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
	static JsonObject login(JsonObject data, EuphratisSession es){
		JsonObject answer = Loginmanager.login(es, data.get("username").getAsString(), data.get("passwordHash").getAsString());
		return answer;
	}
}
