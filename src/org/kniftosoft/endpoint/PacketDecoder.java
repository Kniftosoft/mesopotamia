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
		
	}
	
	@Override
	public Packet decode(String msg) throws DecodeException 
	{
		try{
		parser = new JsonParser();
		System.out.println("recived: "+msg);
		JsonObject jsonPacket = (JsonObject)parser.parse(msg);
		System.out.println("parse: "+msg);
		System.out.flush();
		
		int packetTypeID = jsonPacket.get("typeID").getAsInt();
		
		PacketType type = PacketType.byID(packetTypeID);
		
		if(type == null)
		{
			throw new DecodeException(msg,"Invalid packet type ID: " + packetTypeID);
		}
		
		try 
		{
			System.out.println("create packet: "+msg);
			Packet packet = (Packet) type.getPacketClass().newInstance();
			packet.createFromJSON(jsonPacket.get("data").getAsJsonObject());
			packet.setUID(jsonPacket.get("uid").getAsInt());
			
			return packet;
			
		}catch (InstantiationException e) 
		{
			System.out.println("error: "+e.toString());
			throw new DecodeException(msg,"Could not instantiate packet class of packet type " + type.name());
			
		}catch (IllegalAccessException e) 
		{
			System.out.println("error: "+e.toString());
			throw new DecodeException(msg,"Could not instantiate packet class of packet type " + type.name());
		}
		}catch(Exception e)
		{
			System.err.println(e.toString());
			throw new DecodeException(msg, "unknown");	
		}
	}

	@Override
	public boolean willDecode(String msg) 
	{
		System.out.println("will decode"+msg);
		return true;
	}

	
}
