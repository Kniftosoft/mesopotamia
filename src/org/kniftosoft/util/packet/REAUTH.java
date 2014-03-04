/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */

public class REAUTH extends Packet {
	private String newSessionID;
	private JsonObject userConfig;
	/**
	 * @param uid
	 * @param peer
	 */
	public REAUTH(int uid, EuphratisSession peer,String newSessionID,JsonObject userConfig) {
		// TODO Auto-generated constructor stub
		this.uid = uid;
		this.peer = peer;
		this.setNewSessionID(newSessionID);
		this.setUserConfig(userConfig);
	}
	@Override
	public void createFromJSON(JsonObject o) {
		newSessionID = o.get("newSessionID").getAsString();
		userConfig = o.getAsJsonObject("userConfig");
		
	}
	@Override
	public void executerequest() {
		// TODO not executable
		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("newSessionID", newSessionID);
		data.add("userConfig",userConfig);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.REAUTH;
	}
	public String getNewSessionID() {
		return newSessionID;
	}
	public void setNewSessionID(String newSessionID) {
		this.newSessionID = newSessionID;
	}
	public JsonObject getUserConfig() {
		return userConfig;
	}
	public void setUserConfig(JsonObject userConfig) {
		this.userConfig = userConfig;
	}
}
