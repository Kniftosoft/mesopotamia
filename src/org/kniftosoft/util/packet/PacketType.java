package org.kniftosoft.util.packet;

import org.kniftosoft.util.Constants;


public enum PacketType 
{
	

	HANDSHAKE(1, HANDSHAKE.class,Constants.incoming),
	ACCEPT(2, ACCEPT.class,Constants.outgoing),
	LOGIN(10, LOGIN.class,Constants.incoming),
	AUTH(11, AUTH.class,Constants.outgoing),
	RELOG(12,RELOG.class,Constants.incoming),
	REAUTH(13,REAUTH.class,Constants.outgoing),
	LOGOUT(14,LOGOUT.class,Constants.bidirectional),
	QUERY(20,QUERY.class,Constants.incoming),
	DATA(21,DATA.class,Constants.outgoing),
	ACK(200, ACK.class,Constants.bidirectional),
	NACK(201, NACK.class,Constants.bidirectional),
	ERROR(242,ERROR.class,Constants.bidirectional);
	
	private int typeID;
	private int direction;
	private Class<?> packetClass;
	
	private PacketType(int typeID, Class<?> packetClass,int direction)
	{
		this.typeID = typeID;
		this.packetClass = packetClass;
		this.direction = direction;
	}
	
	
	public int getTypeID()
	{
		return typeID;
	}
	
	public Class<?> getPacketClass()
	{
		return packetClass;
	}
	
	public static PacketType byID(int id)
	{
		System.out.println("male packet by id");
		for(PacketType t : PacketType.values())
		{
			if(t.getTypeID() == id)
			{
				return t;
			}
		}
		
		return null;
	}


	public int getDirection() {
		return direction;
	}


	public void setDirection(int direction) {
		this.direction = direction;
	}
}
