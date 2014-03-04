package org.kniftosoft.endpoint;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.kniftosoft.util.packet.Packet;
import org.kniftosoft.util.packet.PacketType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketDecoder implements Decoder.Text<Packet>
{
	private JsonParser parser;
	
	@Override
	public void destroy() 
	{
		System.out.println("destroy decode");
	}

	@Override
	public void init(EndpointConfig ec) 
	{
		
		System.out.println("init decode");
		parser = new JsonParser();
	}

	@Override
	public Packet decode(String msg) throws DecodeException 
	{
		System.out.println("recived: "+msg);
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
			
			packet.createFromJSON(jsonPacket.get("data").getAsJsonObject());
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
		System.out.println("will decode"+msg);
		return true;
	}

	
}
