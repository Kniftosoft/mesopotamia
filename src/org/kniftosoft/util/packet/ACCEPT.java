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
public class ACCEPT extends Packet {

	String salt;
	/**
	 * @param uid
	 * @param peer
	 */
	public ACCEPT(int uid, EuphratisSession peer) {
		this.uid = uid;
		this.peer = peer;
		this.salt = peer.getSalt();
	}

	@Override
	public void createFromJSON(JsonObject o) {
		this.salt = o.get("salt").getAsString();
		
	}

	@Override
	public JsonObject storeData() {
		JsonObject jo = new JsonObject();
		jo.addProperty("salt", salt);
		return jo;
	}

	@Override
	public PacketType getType() {
		return PacketType.ACCEPT;
	}

	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
}
