/**
 * 
 */
package org.kniftosoft.util.packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class ACK extends Packet {

	// NO DATA FIELDS
	@Override
	public void createFromJSON(JsonObject o) {
		
	}
	// NO DATA FIELDS
	@Override
	public JsonObject storeData() {
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
