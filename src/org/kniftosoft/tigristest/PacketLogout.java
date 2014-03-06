package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketLogout extends Packet
{

	@Override
	public void createFromJSON(JsonObject o)
	{
		reasonCode = o.get("reasonCode").getAsInt();
	}

	@Override
	public JsonObject storeToJSON()
	{
		return null;
	}

	@Override
	public PacketType getType()
	{
		return PacketType.LOGOUT;
	}

	public int getReasonCode()
	{
		return reasonCode;
	}
	
	private int reasonCode;
	
}
