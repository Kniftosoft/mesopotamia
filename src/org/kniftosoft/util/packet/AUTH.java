package org.kniftosoft.util.packet;

import org.kniftosoft.entity.Session;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class AUTH extends Packet {

	private int sessionID;

	
	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		sessionID = o.get("sessionID").getAsInt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.AUTH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		return data;
	}
	
	/**
	 * @param session
	 */
	public void setSessionID(Session session) {
		sessionID = session.getIdSessions();
	}

}
