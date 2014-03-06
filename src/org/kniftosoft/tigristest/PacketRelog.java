package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketRelog extends Packet 
{

	@Override
	public void createFromJSON(JsonObject o) 
	{
		sessionID = o.get("sessionID").getAsString();
	}

	@Override
	public JsonObject storeToJSON() 
	{
		return null;
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.RELOG;
	}

	public String getSessionID()
	{
		return sessionID;
	}
	
	private String sessionID;
	
}
