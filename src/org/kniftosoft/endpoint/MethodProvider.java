/**
 * 
 */
package org.kniftosoft.endpoint;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.websocket.Session;

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
	static JsonObject login(JsonObject data, Session peer){
		JsonObject answer = new JsonObject();
		Loginmanager.login(peer, data.get("user").getAsString(), data.get("pass").getAsString());
		return answer;
	}
}
