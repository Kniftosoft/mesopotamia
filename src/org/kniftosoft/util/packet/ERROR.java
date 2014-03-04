/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.entity.EuphratisSession;

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
	public ERROR(int uid, EuphratisSession peer,int errorCode,String errorMessage) {
		this.uid = uid;
		this.peer = peer;
		this.setErrorCode(errorCode);
		this.setErrorMessage(errorMessage);
	}
	@Override
	public void createFromJSON(JsonObject o) {
		errorCode = o.get("errorCode").getAsInt();
		errorMessage = o.get("errorMessage").getAsString();
		
	}
	@Override
	public void executerequest() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("errorCode", errorCode);
		data.addProperty("errorMessage", errorMessage);
		return null;
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
}
