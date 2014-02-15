package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketQuit extends Packet 
{

	public PacketQuit()
	{
		reasonMsg = "";
		reasonCode = 0;
	}
	
	@Override
	public void createFromJSON(JsonObject o) 
	{
		reasonMsg = o.get("reasonMessage").getAsString();
		reasonCode = o.get("reasonCode").getAsInt();
	}

	@Override
	public JsonObject storeToJSON() 
	{
		JsonObject jo = new JsonObject();
		
		jo.addProperty("reasonCode", reasonCode);
		jo.addProperty("reasonMessage", reasonMsg);
		
		return jo;
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.QUIT;
	}
	
	public void setReasonMessage(String msg)
	{
		reasonMsg = msg;
	}
	
	public void setReasonCode(int c)
	{
		reasonCode = c;
	}
	
	private String reasonMsg;
	private int reasonCode;
	
}
