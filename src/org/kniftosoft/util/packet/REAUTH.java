/**
 * 
 */
package org.kniftosoft.util.packet;

import java.util.Iterator;

import org.kniftosoft.entity.User;
import org.kniftosoft.entity.Userconfig;
import org.kniftosoft.util.EuphratisSession;

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
	/**
	 * @param uid
	 * @param peer
	 */
	public REAUTH(int uid, EuphratisSession peer,int i,User user) {
		
		// TODO Auto-generated constructor stub
		this.uid = uid;
		this.peer = peer;
		this.user = user;
		this.setNewSessionID(i);
		for(Iterator<Userconfig> iteratur = user.getUserconfigs().iterator();iteratur.hasNext();)
		{
			JsonObject config = new JsonObject();
			config.addProperty("userConfig", iteratur.next().toString());
			userConfig.add(config);
		}		
	}
	@Override
	public void createFromJSON(JsonObject o) {
		newSessionID = o.get("newSessionID").getAsInt();
		userConfig = o.getAsJsonArray("userConfig");		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("newSessionID", newSessionID);
		data.add("userConfig",userConfig);
		data.addProperty("username", user.getEmail());
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.REAUTH;
	}
	public int getNewSessionID() {
		return newSessionID;
	}
	public void setNewSessionID(int i) {
		this.newSessionID = i;
	}
	public JsonArray getUserConfig() {
		return userConfig;
	}
	public void setUserConfig(JsonArray userConfig) {
		this.userConfig = userConfig;
	}
	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
}
