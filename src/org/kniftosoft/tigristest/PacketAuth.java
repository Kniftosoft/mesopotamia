package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketAuth extends Packet 
{

	@Override
	public void createFromJSON(JsonObject o) 
	{
		
	}

	@Override
	public JsonObject storeToJSON() 
	{
		JsonObject jo = new JsonObject();
		
		jo.addProperty("sessionID", "thisIsYourSessionIDExample");
		
		return jo;
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.AUTH;
	}

}
