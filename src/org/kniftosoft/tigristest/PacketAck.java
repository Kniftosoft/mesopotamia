package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketAck extends Packet 
{

	@Override
	public void createFromJSON(JsonObject o) 
	{
		
	}

	@Override
	public JsonObject storeToJSON() 
	{
		return new JsonObject(); //ACK has not data, return empty object
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.ACK;
	}

}
