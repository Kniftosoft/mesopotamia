/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.util.EuphratisSession;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */

public class REAUTH extends Packet {
	private int newSessionID;
	private JsonObject userConfig;
	/**
	 * @param uid
	 * @param peer
	 */
	public REAUTH(int uid, EuphratisSession peer,int i,JsonObject userConfig) {
		// TODO Auto-generated constructor stub
		this.uid = uid;
		this.peer = peer;
		this.setNewSessionID(i);
		this.setUserConfig(userConfig);
	}
	@Override
	public void createFromJSON(JsonObject o) {
		newSessionID = o.get("newSessionID").getAsInt();
		userConfig = o.getAsJsonObject("userConfig");
		
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
	public int getNewSessionID() {
		return newSessionID;
	}
	public void setNewSessionID(int i) {
		this.newSessionID = i;
	}
	public JsonObject getUserConfig() {
		return userConfig;
	}
	public void setUserConfig(JsonObject userConfig) {
		this.userConfig = userConfig;
	}
	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
}
