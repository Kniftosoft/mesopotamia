package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public abstract class Packet 
{
	
	public abstract void createFromJSON(JsonObject o);
	
	public abstract JsonObject storeToJSON();
	
	public abstract PacketType getType();
	
	public int getUID()
	{
		return uid;
	}
	
	public void setUID(int uid)
	{
		this.uid = uid; 
	}
	
	private int uid = UIDGen.instance().generateUID();
}
