package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketHandshake extends Packet 
{

	@Override
	public PacketType getType() 
	{
		return PacketType.HANDSHAKE;
	}

	@Override
	public void createFromJSON(JsonObject o) 
	{
		clientVersion = o.get("clientVersion").getAsString();
	}
	
	@Override
	public JsonObject storeToJSON() 
	{
		return null; //Handshakes can not be sent by server
	}
	
	public String getClientVersion()
	{
		return clientVersion;
	}
	
	private String clientVersion;
	
}
