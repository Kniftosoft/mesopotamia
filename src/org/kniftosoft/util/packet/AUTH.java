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
public class AUTH extends Packet {

	private String sessionID;
	private JsonObject userconfig;
	
	/**
	 * @param uid
	 * @param peer
	 */
	public AUTH(int uid, EuphratisSession peer,JsonObject userconfig) {
		this.sessionID = peer.getSession().getId();
		this.userconfig = userconfig;
	}

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
	protected JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		data.add("userconfig", userconfig);
		return data;
	}

	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
}
