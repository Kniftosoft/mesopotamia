package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class PacketError extends Packet 
{

	@Override
	public void createFromJSON(JsonObject o) 
	{

	}

	@Override
	public JsonObject storeToJSON() 
	{
		JsonObject jo = new JsonObject();
		
		jo.addProperty("errorCode",errorType.getID());
		
		return jo;
	}

	@Override
	public PacketType getType() 
	{
		return PacketType.ERROR;
	}

	
	public void setErrorType(ErrorType e)
	{
		errorType = e;
	}
	
	public ErrorType getErrorType()
	{
		return errorType;
	}
	
	private ErrorType errorType;
	
}
