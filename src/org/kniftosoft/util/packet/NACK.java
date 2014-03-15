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
public class NACK extends Packet{

	/**
	 * @param uid
	 * @param peer
	 */
	public NACK(int uid, EuphratisSession peer) {
		this.uid = uid;
		this.peer = peer;
	}

	//NO DATA FIELDS
	@Override
	public void createFromJSON(JsonObject o) {
		
	}
	//NOT EXECUTABLE
	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
	//NO DATA FIELDS
	@Override
	public JsonObject storeData() {
		// TODO Auto-generated method stub
		return new JsonObject();
	}

	@Override
	public PacketType getType() {
		// TODO Auto-generated method stub
		return PacketType.NACK;
	}
}
