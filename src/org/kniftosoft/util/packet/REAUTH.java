/**
 * 
 */
package org.kniftosoft.util.packet;

import java.util.Iterator;

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
	private JsonArray userConfig;
	private User user;
	
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		newSessionID = o.get("newSessionID").getAsInt();
		userConfig = o.getAsJsonArray("userConfig");		
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("newSessionID", newSessionID);
		data.add("userConfig",userConfig);
		data.addProperty("username", user.getEmail());
		return data;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.REAUTH;
	}
	
	/**
	 * @return newSessionID
	 */
	public int getNewSessionID() {
		return newSessionID;
	}

	/**
	 * @param newSessionID
	 */
	public void setNewSessionID(int newSessionID) {
		this.newSessionID = newSessionID;
	}
	/**
	 * @return userConfig
	 */
	public JsonArray getUserConfig() {
		return userConfig;
	}
	/**
	 * @param userConfig
	 */
	public void setUserConfig(JsonArray userConfig) {
		this.userConfig = userConfig;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user sets the user and loads the userconfigs
	 */
	public void setUser(User user) {
		this.user = user;
		for(Iterator<Userconfig> iteratur = user.getUserconfigs().iterator();iteratur.hasNext();)
		{
			JsonObject config = new JsonObject();
			config.addProperty("userConfig", iteratur.next().toString());
			userConfig.add(config);
		}		
	}
}
