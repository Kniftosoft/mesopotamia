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
public class ACK extends Packet {

	/**
	 * @param uid
	 * @param peer
	 */
	public ACK(int uid, EuphratisSession peer) {
		this.uid = uid;
		this.peer = peer;
	}

	// NO DATA FIELDS
	@Override
	public void createFromJSON(JsonObject o) {
		
	}
	// NO DATA FIELDS
	@Override
	protected JsonObject storeData() {
		return null;
	}

	@Override
	public PacketType getType() {
		// TODO Auto-generated method stub
		return PacketType.ACK;
	}

	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}

}
