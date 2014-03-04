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
public class DATA extends Packet {

	private JsonObject queryResult;
	/**
	 * @param typeID
	 * @param uid
	 * @param peer
	 */
	public DATA(int uid, EuphratisSession peer,JsonObject queryResult) {
		
		this.queryResult = queryResult;
	}

	@Override
	public void createFromJSON(JsonObject o) {
		queryResult = o.get("queryResult").getAsJsonObject();
		
	}

	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.add("queryResult", queryResult);
		return data;
	}

	@Override
	public PacketType getType() {
		// TODO Auto-generated method stub
		return PacketType.DATA;
	}

}
