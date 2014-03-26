package org.kniftosoft.util.packet;

import org.kniftosoft.util.ErrorType;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class ERROR extends Packet {

	private int errorCode;
	private String errorMessage;

	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		errorCode = o.get("errorCode").getAsInt();
		errorMessage = o.get("errorMessage").getAsString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		System.err.print("Client error: \n  Errorcode: " + errorCode
				+ "\n  Errormessage: " + errorMessage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.ERROR;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("errorCode", errorCode);
		data.addProperty("errorMessage", errorMessage);
		return data;
	}
	
	/**
	 * @return errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @return errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errortype
	 */
	public void setError(ErrorType errortype) {
		errorCode = errortype.getErrorCode();
		errorMessage = errortype.getDefaultErrorMessage();
	}

	/**
	 * @param errorMessage
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
