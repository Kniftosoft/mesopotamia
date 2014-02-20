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
	static Packet _default(JsonObject data, EuphratisSession es){
		return new Packet(201, null);
	}
	
	public static Packet login(JsonObject data, EuphratisSession es){
		return Loginmanager.login(es, data.get("username").getAsString(), data.get("passwordHash").getAsString());
	}
	
	public static Packet handshake(JsonObject data,EuphratisSession es) {
		if(data.get("clientVersion").getAsString().equals(Constants.getClientversion()))
		{
			return new Packet(200, null);
		}
		else
		{
			// TODO A Refused Connection is not removed from sessions
			JsonObject packetdata = new JsonObject();
			packetdata.addProperty("reasonCode", 4);
			packetdata.addProperty("reasonMessage", "Connection Refused because of a wrong Client Version");
			return new Packet(255, packetdata);
		}
	}
	public static Packet relog(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub	
		return Loginmanager.relog(es, data);
	}
	public static Packet logout(JsonObject data, EuphratisSession es) {
		// TODO Auto-generated method stub
		Loginmanager.Logout(es, data);
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
