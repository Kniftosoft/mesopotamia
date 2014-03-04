package org.kniftosoft.endpoint;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.kniftosoft.util.packet.Packet;

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
		JsonObject jo = packet.storeToJSON();
		
		if(jo == null)
		{
			throw new EncodeException(packet,"The packet of type " + packet.getType().name() + " could not be stored to JSON.");
		}
		
		JsonObject fullPacket = new JsonObject();
		
		fullPacket.add("data", jo);
		
		fullPacket.addProperty("typeID", packet.getType().getTypeID());
		
		fullPacket.addProperty("uid", packet.getUID()); //TODO: Implement UID generator
		
		return fullPacket.toString();
	}
	
}
