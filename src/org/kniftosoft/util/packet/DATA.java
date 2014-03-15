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

	private JsonArray result;
	/**
	 * @return the result
	 */
	public JsonArray getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(JsonArray result) {
		this.result = result;
	}

	private int category;

	@Override
	public void createFromJSON(JsonObject o) {
		result = o.getAsJsonArray("result");
	}

	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.add("result", result);
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
