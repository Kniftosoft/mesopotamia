/**
 * 
 */
package org.kniftosoft.util.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class DATA extends Packet {

	private JsonArray queryResult;
	private int category;

	@Override
	public void createFromJSON(JsonObject o) {
		queryResult = o.getAsJsonArray("queryResult");
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

	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

}
