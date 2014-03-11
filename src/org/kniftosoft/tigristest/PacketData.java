package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketData extends Packet
{

	@Override
	public void createFromJSON(JsonObject o)
	{
		

	}

	@Override
	public JsonObject storeToJSON()
	{
		return null;
	}

	@Override
	public PacketType getType()
	{
		return PacketType.DATA;
	}

}
