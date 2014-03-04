package org.kniftosoft.util.packet;

import java.nio.channels.AcceptPendingException;

public enum PacketType 
{
	

	HANDSHAKE(1, HANDSHAKE.class),
	ACCEPT(2, AcceptPendingException.class),
	LOGIN(10, LOGIN.class),
	AUTH(11, AUTH.class),
	RELOG(12,RELOG.class),
	REAUTH(13,REAUTH.class),
	LOGOUT(14,LOGOUT.class),
	QUERY(20,QUERY.class),
	DATA(21,DATA.class),
	ACK(200, ACK.class),
	NACK(201, NACK.class),
	ERROR(242,ERROR.class);
	
	private int typeID;
	private Class<?> packetClass;
	
	private PacketType(int typeID, Class<?> packetClass)
	{
		this.typeID = typeID;
		this.packetClass = packetClass;
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
}
