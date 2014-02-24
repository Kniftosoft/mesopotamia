package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketAccept extends Packet
{

	@Override
	public void createFromJSON(JsonObject o) 
	{
		
	}

	@Override
	public JsonObject storeToJSON() 
	{
		JsonObject jo = new JsonObject();
		
		jo.addProperty("salt", salt);
		
		return jo;
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.ACCEPT;
	}

	public void setSalt(String salt)
	{
		this.salt = salt;
	}
	
	private String salt;
	
}
