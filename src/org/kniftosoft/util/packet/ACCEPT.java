package org.kniftosoft.util.packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class ACCEPT extends Packet {

	String salt;


	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		salt = o.get("salt").getAsString();

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
		return PacketType.ACCEPT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject jo = new JsonObject();
		jo.addProperty("salt", salt);
		return jo;
	}
	
	/**
	 * @param salt
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}
}
