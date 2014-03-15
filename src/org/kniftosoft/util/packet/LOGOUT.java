package org.kniftosoft.util.packet;

import org.kniftosoft.thread.ClientUpDater;

import com.google.gson.JsonObject;

public class LOGOUT extends Packet {
	private int reasonCode;
	private String reasonMessage;
	
	
	public LOGOUT() {
	}
	private void Logout()
	{
		
		/*TODO need session id back 
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.remove(em.find(Session.class, rp.getsessionID));
	    em.close();
	    */
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
		return null;
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
