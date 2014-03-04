/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.Login.Loginmanager;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class LOGIN extends Packet {

	private String username;
	private String passwordHash;
	
	public void executerequest(){
		Loginmanager.login(this);
	}

	@Override
	public void createFromJSON(JsonObject o) {
		username = o.get("username").getAsString();
		passwordHash = o.get("passwordHash").getAsString();
		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("username", username);
		data.addProperty("passwordHash", passwordHash);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.LOGIN;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the passwordHash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}
	/**
	 * @param passwordHash the passwordHash to set
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

}
