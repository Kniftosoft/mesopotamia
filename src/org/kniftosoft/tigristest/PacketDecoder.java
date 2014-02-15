package org.kniftosoft.tigristest;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketDecoder implements Decoder.Text<Packet>
{
	
	@Override
	public void destroy() 
	{
		
	}

	@Override
	public void init(EndpointConfig ec) 
	{
		parser = new JsonParser();
	}

	@Override
	public Packet decode(String msg) throws DecodeException 
	{
		JsonObject jsonPacket = (JsonObject)parser.parse(msg);
		
		int packetTypeID = jsonPacket.get("typeID").getAsInt();
		
		PacketType type = PacketType.byID(packetTypeID);
		
		if(type == null)
		{
			throw new DecodeException(msg,"Invalid packet type ID: " + packetTypeID);
		}
		
		try 
		{
			Packet packet = (Packet) type.getPacketClass().newInstance();
			
			packet.createFromJSON((JsonObject) jsonPacket.get("data"));
			packet.setUID(jsonPacket.get("uid").getAsInt());
			
			return packet;
			
		}catch (InstantiationException e) 
		{
			throw new DecodeException(msg,"Could not instantiate packet class of packet type " + type.name());
		}catch (IllegalAccessException e) 
		{
			throw new DecodeException(msg,"Could not instantiate packet class of packet type " + type.name());
		}
	}

	@Override
	public boolean willDecode(String msg) 
	{
		return true;
	}

	private JsonParser parser;
	
}
