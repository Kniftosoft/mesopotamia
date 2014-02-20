/**
 * 
 */
package org.kniftosoft.util;

/**
 * @author julian
 *
 */
public class Constants {
	private static final String Clientversion="0.0.4";
	static final String PERSISTENCE_UNIT_NAME = "Euphratis";
	/**
	 * @return the version
	 */
	public static String getClientversion() {
		return Clientversion;
	}
	/**
	 * @return the persistenceUnitName
	 */
	public static String getPersistenceUnitName() {
		return PERSISTENCE_UNIT_NAME;
	}
	
}
