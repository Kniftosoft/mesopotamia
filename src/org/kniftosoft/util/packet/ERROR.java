/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.util.EuphratisSession;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class ERROR extends Packet {

	private int errorCode;
	private String errorMessage;
	/**
	 * @param uid
	 * @param peer
	 */
	public ERROR(int uid, EuphratisSession peer,int errorCode,String errorMessage) 
	{
		this.uid = uid;
		this.peer = peer;
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}
	public ERROR()
	{
		
	}
	@Override
	public void createFromJSON(JsonObject o) {
		errorCode = o.get("errorCode").getAsInt();
		errorMessage = o.get("errorMessage").getAsString();
		
	}
	@Override
	public void executerequest() {
		System.err.print("Client error: \n  Errorcode: "+errorCode+"\n  Errormessage: "+errorMessage);
		LOGOUT logout = new LOGOUT();
		logout.setPeer(peer);
		logout.setSessionID(-1);
		logout.setReasonCode(3);
		logout.setReasonMessage("Logout because of critical error");
		logout.executerequest();
		logout.send();
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("errorCode", errorCode);
		data.addProperty("errorMessage", errorMessage);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.ERROR;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public void send()
	{
		//TODO remove
		System.out.println("try send:"+this.toString());
		peer.getSession().getAsyncRemote().sendObject(this);
		executerequest();
	}
	
}
