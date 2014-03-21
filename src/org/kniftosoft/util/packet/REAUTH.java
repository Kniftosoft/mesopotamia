package org.kniftosoft.util.packet;

import org.kniftosoft.entity.User;
import org.kniftosoft.entity.Userconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */

public class REAUTH extends Packet {
	private int newSessionID;
	private User user;
	private JsonArray userConfig;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		newSessionID = o.get("newSessionID").getAsInt();
		userConfig = o.getAsJsonArray("userConfig");
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
		data.add("userConfig", userConfig);
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
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return userConfig
	 */
	public JsonArray getUserConfig() {
		return userConfig;
	}

	/**
	 * @param newSessionID
	 */
	public void setNewSessionID(int newSessionID) {
		this.newSessionID = newSessionID;
	}

	/**
	 * sets the user and loads the userconfigs
	 * @param user
	 *            
	 */
	public void setUser(User user) {
		this.user = user;
		for (final Userconfig userconfig2 : user.getUserconfigs()) {
			final JsonObject config = new JsonObject();
			config.addProperty("userConfig", userconfig2.toString());
			userConfig.add(config);
		}
	}

	/**
	 * @param userConfig
	 */
	public void setUserConfig(JsonArray userConfig) {
		this.userConfig = userConfig;
	}

}
