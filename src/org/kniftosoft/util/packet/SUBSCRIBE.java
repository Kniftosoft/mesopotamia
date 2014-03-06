/**
 * 
 */
package org.kniftosoft.util.packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class SUBSCRIBE extends Packet {

	private String category;
	private String ident;

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		// TODO register subscribe

	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		category = o.get("category").getAsString();
		ident = o.get("ident").getAsString();

	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.SUBSCRIBE;
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("category", category);
		data.addProperty("ident", ident);
		return data;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

}
