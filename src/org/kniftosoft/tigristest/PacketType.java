package org.kniftosoft.tigristest;

public enum PacketType 
{

	HANDSHAKE(1, PacketHandshake.class),
	LOGIN(10, PacketLogin.class),
	AUTH(11, PacketAuth.class),
	ACK(200, PacketAck.class),
	NACK(201, PacketNack.class),
	QUIT(255, PacketQuit.class);
	
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
