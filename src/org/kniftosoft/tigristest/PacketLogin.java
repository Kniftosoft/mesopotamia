package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketLogin extends Packet {

	@Override
	public void createFromJSON(JsonObject o) 
	{
		username = o.get("username").getAsString();
		passwordHash = o.get("passwordHash").getAsString();
	}

	@Override
	public JsonObject storeToJSON() 
	{
		return null;
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.LOGIN;
	}

	public String getPasswordHash()
	{
		return passwordHash;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	private String username;
	private String passwordHash;
	
}
