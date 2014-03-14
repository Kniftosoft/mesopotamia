package org.kniftosoft.tigristest;


import com.google.gson.JsonArray;
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
		JsonObject jo = new JsonObject();
		
		jo.addProperty("category", category);
		jo.add("result", result);
		
		return jo;
	}

	@Override
	public PacketType getType()
	{
		return PacketType.DATA;
	}

	public void setCategory(int cat)
	{
		this.category = cat;
	}
	
	public void setResult(JsonArray result)
	{
		this.result = result;
	}
	
	private JsonArray result;
	private int category;
}
