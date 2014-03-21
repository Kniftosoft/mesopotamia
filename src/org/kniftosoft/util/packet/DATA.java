package org.kniftosoft.util.packet;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class DATA extends Packet {

	private int category;
	private JsonArray result;


	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject data) {
		result = data.getAsJsonArray("result");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.DATA;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.add("result", result);
		data.addProperty("category", category);
		return data;
	}

	/**
	 * @param category
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * @param result
	 */
	public void setResult(JsonArray result) {
		this.result = result;
	}

}
