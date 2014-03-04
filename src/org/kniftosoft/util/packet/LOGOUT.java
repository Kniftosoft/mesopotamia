package org.kniftosoft.util.packet;

import org.kniftosoft.Login.Loginmanager;
import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;

public class LOGOUT extends Packet {
	private String sessionID;
	private int reasonCode;
	private String reasonMessage;
	
	
	public LOGOUT(int uid, EuphratisSession peer,int reasoncode,String reasonmessage) {
		this.sessionID = peer.getSession().getId();
		this.reasonCode = reasoncode;
		this.reasonMessage = reasonmessage;
	}
	@Override
	public void createFromJSON(JsonObject o) {
		sessionID = o.get("sessionID").getAsString();
		reasonCode = o.get("reasonCode").getAsInt();
		reasonMessage = o.get("reasonMessage").getAsString();
		
	}
	@Override
	public void executerequest() {
		Loginmanager.Logout(this);
		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		data.addProperty("reasonCode", reasonCode);
		data.addProperty("reasonMessage", reasonMessage);
		return null;
	}
	@Override
	public PacketType getType() {
		return PacketType.LOGOUT;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID the sessionID to set
	 */
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
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
