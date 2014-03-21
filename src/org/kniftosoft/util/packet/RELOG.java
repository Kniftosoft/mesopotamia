package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.kniftosoft.entity.Session;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.Peerholder;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class RELOG extends Packet {

	private int sessionID;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		try {
			sessionID = o.get("sessionID").getAsInt();
		} catch (final NumberFormatException e) {
			sessionID = 0;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		if (sessionID != 0) {
			try {
				final EntityManager em = Constants.factory
						.createEntityManager();
				final Session session = em.find(Session.class, sessionID);
				em.close();
				if (session.getIdSessions() == sessionID) {
					System.out.println("relog");
					peer.setUser(session.getUserBean());
					peer.setLoginverified(true);
					Peerholder.updatepeer(peer);
					final REAUTH reauth = new REAUTH();
					reauth.setPeer(peer);
					reauth.setUID(uid);
					// TODO give a real new id
					reauth.setNewSessionID(session.getIdSessions());
					reauth.setUser(session.getUserBean());
					reauth.send();
				} else {
					System.out.println("no session");
					final NACK nack = new NACK();
					nack.setPeer(peer);
					nack.setUID(uid);
					nack.send();
				}
			} catch (final NoResultException nr) {
				nr.printStackTrace();
				final NACK nack = new NACK();
				nack.setPeer(peer);
				nack.setUID(uid);
				nack.send();
			} catch (final Exception e) {
				e.printStackTrace();
				final NACK nack = new NACK();
				nack.setPeer(peer);
				nack.setUID(uid);
				nack.send();
			}
		} else {
			final NACK nack = new NACK();
			nack.setPeer(peer);
			nack.setUID(uid);
			nack.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.RELOG;
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
	 * @return sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}

	/**
	 * @param sessionID
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

}
