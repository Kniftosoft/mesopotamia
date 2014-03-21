package org.kniftosoft.util;

/**
 * @author julian
 * 
 */
public enum ErrorType {

	UNKNOWN(0, " "),
	INVALID_PACKET(1, " "),
	SESSION_EXPIRED(2, " "),
	INTERNAL_EXCEPTION(3, "internal problems"),
	INVALID_RESPONSE(4, " "),
	WRONG_VERSION(5,"Connection Refused because of a wrong Client Version"),
	NOT_ALLOWED(6, " "),
	BAD_QUERY(7, " "),
	BAD_PACKET(8, "missing data fields");

	private int errorCode;
	private String defaultErrorMessage;

	/**
	 * @param typeID
	 * @param packetClass
	 * @param direction
	 */
	private ErrorType(int errorCode, String defaultErrorMessage) {
		setErrorCode(errorCode);
		setDefaultErrorMessage(defaultErrorMessage);
	}

	/**
	 * @return defaultErrorMessage
	 */
	public String getDefaultErrorMessage() {
		return defaultErrorMessage;
	}

	/**
	 * @return errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param defaultErrorMessage
	 */
	private void setDefaultErrorMessage(String defaultErrorMessage) {
		this.defaultErrorMessage = defaultErrorMessage;
	}

	/**
	 * @param errorCode
	 */
	private void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
}
