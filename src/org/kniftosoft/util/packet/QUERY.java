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
public class QUERY extends Packet {

	private String sessionID;
	private JsonObject filterData;
	/**
	 * 
	 */
	public QUERY(int uid, EuphratisSession peer,JsonObject filterData) {
		this.uid = uid;
		this.peer = peer;
		this.sessionID = peer.getSession().getId();
		this.filterData = filterData;
	}

	@Override
	public void createFromJSON(JsonObject o) {
		sessionID = o.get("sessionID").getAsString();
		filterData = o.getAsJsonObject("filterData");
		
	}

	@Override
	public void executerequest() {
		// TODO execute query
		
	}

	@Override
	protected JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		data.add("filterData", filterData);
		return data;
	}

	@Override
	public PacketType getType() {
		return PacketType.QUERY;
	}

}
