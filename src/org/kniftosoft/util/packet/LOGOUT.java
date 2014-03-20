package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Session;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonObject;

public class LOGOUT extends Packet {
	private int reasonCode;
	private String reasonMessage;
	private int sessionID;
	
	
	public int getSessionID() {
		return sessionID;
	}
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}
	public LOGOUT() {
	}
	private void Logout()
	{
		
		
		try
		{
			EntityManager em = Constants.factory.createEntityManager();
		    em.remove(em.find(Session.class,sessionID));
		    em.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
		//TODO remove sys out if checkt and handle different codes
		System.out.println("logg out"+peer.getSession().getId());
		peer.setLoginverified(false);
		peer.setUser(null);
		peer.setSalt(null);
		ClientUpDater.updatepeer(peer);	
		ACK ap = new ACK();
		ap.setPeer(peer);
		ap.setUID(uid);
		ap.send();
	}
	
	@Override
	public void createFromJSON(JsonObject o) {
		reasonCode = o.get("reasonCode").getAsInt();
		reasonMessage = o.get("reasonMessage").getAsString();
		sessionID = o.get("sessionID").getAsInt();
		
	}
	@Override
	public void executerequest() {
		Logout();
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("reasonCode", reasonCode);
		data.addProperty("reasonMessage", reasonMessage);
		data.addProperty("sessionID", sessionID);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.LOGOUT;
	}

	/**
	 * @return the reasonCode
	 */
	public int getReasonCode() {
		return reasonCode;
	}
	/**
	 * @param reasonCode the reasonCode to set
	 */
	public void setReasonCode(int reasonCode) {
		this.reasonCode = reasonCode;
	}
	/**
	 * @return the reasonMessage
	 */
	public String getReasonMessage() {
		return reasonMessage;
	}
	/**
	 * @param reasonMessage the reasonMessage to set
	 */
	public void setReasonMessage(String reasonMessage) {
		this.reasonMessage = reasonMessage;
	}
}
