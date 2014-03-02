package org.kniftosoft.tigristest;

public enum ErrorType 
{
	
	UNKNOWN(0),
	INVALID_PACKET(1),
	SESSION_EXPIRED(2),
	INTERNAL_EXCEPTION(3),
	INVALID_RESPONSE(4),
	WRONG_VERSION(5);
	
	
	private ErrorType(int id)
	{
		this.id = id;
	}
	
	public int getID()
	{
		return id;
	}
	
	private int id;
}
