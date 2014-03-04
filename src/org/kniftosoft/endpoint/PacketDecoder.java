package org.kniftosoft.endpoint;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.kniftosoft.util.packet.Packet;
import org.kniftosoft.util.packet.PacketType;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class PacketDecoder implements Decoder.Text<Packet>
{
	
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
		System.out.println("recived: "+msg);
		JsonObject jsonPacket =  new Gson().fromJson("[[]", JsonObject.class);
		//JsonObject jsonPacket = (JsonObject) new  JsonParser().parse(msg);
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
		}catch(Exception e)
		{
			System.out.println(e.toString());
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
