package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketUnsubscribe extends Packet
{

	@Override
	public void createFromJSON(JsonObject o)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public JsonObject storeToJSON()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PacketType getType()
	{
		return PacketType.UNSUBSCRIBE;
	}


	
}
