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
		jo.addProperty("errorMessage", errorMessage);
		
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
	
	public void setErrorMessage(String msg)
	{
		this.errorMessage = msg;
	}
	
	private ErrorType errorType;
	private String errorMessage;
}
