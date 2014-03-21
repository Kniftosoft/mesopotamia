package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Session;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.Peerholder;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class LOGOUT extends Packet {

	private int reasonCode;
	private String reasonMessage;
	private int sessionID;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		reasonCode = o.get("reasonCode").getAsInt();
		reasonMessage = o.get("reasonMessage").getAsString();
		sessionID = o.get("sessionID").getAsInt();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		try {
			final EntityManager em = Constants.factory.createEntityManager();
			em.remove(em.find(Session.class, sessionID));
			em.close();
		} catch (final IllegalArgumentException e) {

		}

		// TODO and handle different codes
		peer.setLoginverified(false);
		peer.setUser(null);
		peer.setSalt(null);
		Peerholder.updatepeer(peer);
		final ACK ap = new ACK();
		ap.setPeer(peer);
		ap.setUID(uid);
		ap.send();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.LOGOUT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("reasonCode", reasonCode);
		data.addProperty("reasonMessage", reasonMessage);
		data.addProperty("sessionID", sessionID);
		return data;
	}

	/**
	 * @return reasonCode
	 */
	public int getReasonCode() {
		return reasonCode;
	}

	/**
	 * @return reasonMessage
	 */
	public String getReasonMessage() {
		return reasonMessage;
	}

	/**
	 * @return sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @param reasonCode
	 */
	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}

	/**
	 * @param reasonMessage
	 */
	public void setReasonMessage(String reasonMessage) {
		this.reasonMessage = reasonMessage;
	}

	/**
	 * @param sessionID
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
}
