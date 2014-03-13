package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketQuery extends Packet
{

	@Override
	public void createFromJSON(JsonObject o)
	{
		category = o.get("category").getAsInt();
		
		queryString = o.get("ident").getAsString();
	}

	@Override
	public JsonObject storeToJSON()
	{
		return null;
	}

	@Override
	public PacketType getType()
	{
		return PacketType.QUERY;
	}

	public int getCategory()
	{
		return category;
	}
	
	public String getQuery()
	{
		return queryString;
	}
	
	private int category;
	private String queryString;
	
}
