package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketNack extends Packet
{

	@Override
	public void createFromJSON(JsonObject o) 
	{

	}

	@Override
	public JsonObject storeToJSON() 
	{
		return new JsonObject();
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.NACK;
	}

}
