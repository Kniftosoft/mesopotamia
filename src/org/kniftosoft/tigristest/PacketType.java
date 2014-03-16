package org.kniftosoft.tigristest;

public enum PacketType 
{

	HANDSHAKE(1, PacketHandshake.class),
	ACCEPT(2, PacketAccept.class),
	LOGIN(10, PacketLogin.class),
	AUTH(11, PacketAuth.class),
	RELOG(12, PacketRelog.class),
	REAUTH(13, PacketReauth.class),
	LOGOUT(14, PacketLogout.class),
	QUERY(20, PacketQuery.class),
	DATA(21, PacketData.class),
	SUBSCRIBE(22, PacketSubscribe.class),
	ACK(200, PacketAck.class),
	NACK(201, PacketNack.class),
	ERROR(242, PacketError.class);
	
	private PacketType(int typeID, Class packetClass)
	{
		this.typeID = typeID;
		this.packetClass = packetClass;
	}
	
	
	public int getTypeID()
	{
		return typeID;
	}
	
	public Class getPacketClass()
	{
		return packetClass;
	}
	
	
	private int typeID;
	private Class packetClass;
	
	
	public static PacketType byID(int id)
	{
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
