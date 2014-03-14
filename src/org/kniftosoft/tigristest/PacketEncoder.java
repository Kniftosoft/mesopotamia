package org.kniftosoft.tigristest;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import com.google.gson.JsonObject;

public class PacketEncoder implements Encoder.Text<Packet>
{

	@Override
	public void destroy() 
	{
		
	}

	@Override
	public void init(EndpointConfig ec) 
	{
		
	}

	@Override
	public String encode(Packet packet) throws EncodeException 
	{
		System.out.println("Sent packet: " + packet.getType().name());
		
		JsonObject jo = packet.storeToJSON();
		
		if(jo == null)
		{
			throw new EncodeException(packet,"The packet of type " + packet.getType().name() + " could not be stored to JSON.");
		}
		
		JsonObject fullPacket = new JsonObject();
		
		fullPacket.add("data", jo);
		
		fullPacket.addProperty("typeID", packet.getType().getTypeID());
		
		fullPacket.addProperty("uid", packet.getUID());
		
		return fullPacket.toString();
	}
	
}
