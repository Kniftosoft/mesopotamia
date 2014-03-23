package org.kniftosoft.util.packet;

import org.kniftosoft.entity.User;
import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */

public class REAUTH extends Packet {
	private int newSessionID;
	private User user;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		newSessionID = o.get("newSessionID").getAsInt();
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
		return PacketType.REAUTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("newSessionID", newSessionID);
		data.addProperty("username", user.getEmail());
		return data;
	}
	
	/**
	 * @return newSessionID
	 */
	public int getNewSessionID() {
		return newSessionID;
	}
	
	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param newSessionID
	 */
	public void setNewSessionID(int newSessionID) {
		this.newSessionID = newSessionID;
	}

	/**
	 * sets the user 
	 * @param user
	 *            
	 */
	public void setUser(User user) {
		this.user = user;
	}

}
