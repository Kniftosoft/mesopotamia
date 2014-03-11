/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.Login.Loginmanager;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class RELOG extends Packet{

	private String sessionID;

	@Override
	public void createFromJSON(JsonObject o) {
		sessionID = o.get("sessionID").getAsString();
		
	}
	@Override
	public void executerequest() {
		Loginmanager.relog(this);
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		return data;
	}
	@Override
	public PacketType getType() {
		// TODO Auto-generated method stub
		return PacketType.RELOG;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

}
