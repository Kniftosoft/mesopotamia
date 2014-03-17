package org.kniftosoft.endpoint;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.kniftosoft.util.Constants;
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
		
	}

	@Override
	public void init(EndpointConfig ec) 
	{

	}
	
	@Override
	public Packet decode(String msg) throws DecodeException 
	{
		try{
		parser = new JsonParser();
		JsonObject jsonPacket = (JsonObject)parser.parse(msg);
		
		int packetTypeID = jsonPacket.get("typeID").getAsInt();
		
		PacketType type = PacketType.byID(packetTypeID);
		
		if(type == null|| type.getDirection()==Constants.outgoing)
		{
			throw new DecodeException(msg,"Invalid packet type ID: " + packetTypeID);
		}
		
		try 
		{
			System.out.println("create packet: "+msg);
			Packet packet = (Packet) type.getPacketClass().newInstance();
			packet.setUID(jsonPacket.get("uid").getAsInt());
			packet.createFromJSON(jsonPacket.get("data").getAsJsonObject());
			
			return packet;
			
		}catch (InstantiationException e) 
		{
			throw new DecodeException(msg,"Could not instantiate packet class of packet type " + type.name());
			
		}catch (IllegalAccessException e) 
		{
			throw new DecodeException(msg,"Could not instantiate packet class of packet type " + type.name());
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new DecodeException(msg, "unknown");	
		}
	}

	@Override
	public boolean willDecode(String msg) 
	{
		parser = new JsonParser();
		JsonObject jsonPacket = (JsonObject)parser.parse(msg);
		if(jsonPacket.has("typeID")&&jsonPacket.has("uid")&&jsonPacket.has("data"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}	
}
