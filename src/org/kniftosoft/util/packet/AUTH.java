/**
 * 
 */
package org.kniftosoft.util.packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class AUTH extends Packet {

	private String sessionID;
	private JsonObject userconfig;

	@Override
	public void createFromJSON(JsonObject o) {
		sessionID = o.get("sessionID").getAsString();
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
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
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
