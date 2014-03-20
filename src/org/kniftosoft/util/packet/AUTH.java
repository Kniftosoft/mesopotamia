/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.entity.Session;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class AUTH extends Packet {

	private int sessionID;
	private JsonObject userconfig;

	@Override
	public void createFromJSON(JsonObject o) {
		sessionID = o.get("sessionID").getAsInt();
		userconfig = o.get("userconfig").getAsJsonObject();
	}

	@Override
	public PacketType getType() {
		return PacketType.AUTH;
	}

	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		data.add("userconfig", userconfig);
		return data;
	}

	/**
	 * @param i the sessionID to set
	 */
	public void setSessionID(Session session) {
		this.sessionID = session.getIdSessions();
	}

	/**
	 * @param userconfig the userconfig to set
	 */
	public void setUserconfig(JsonObject userconfig) {
		this.userconfig = userconfig;
	}

	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}


}
