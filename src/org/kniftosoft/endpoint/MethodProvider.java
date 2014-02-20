/**
 * 
 */
package org.kniftosoft.endpoint;

import org.kniftosoft.Login.Loginmanager;
import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.Packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class MethodProvider {
	static Packet _default(JsonObject data){
		Packet answer = new Packet(201, data);
		return answer;
	}
	public static Packet login(JsonObject data, EuphratisSession es){
		Packet answer = Loginmanager.login(es, data.get("username").getAsString(), data.get("passwordHash").getAsString());
		return answer;
	}
	public static Packet handshake(JsonObject data,EuphratisSession es) {
		if(data.get("clientVersion").getAsString().equals(Constants.getClientversion()))
		{
			return new Packet(200, data);
		}
		else
		{
			return new Packet(201, data);
		}
	}
	public static Packet relog(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet logout(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet query(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet ack(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet nack(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet error(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
	public static Packet quit(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		return null;
	}
}
