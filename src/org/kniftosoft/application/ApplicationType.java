package org.kniftosoft.application;

/**
 * @author julian
 *
 */
public enum ApplicationType {

	Maschineapp(1, Maschineapp.class),
	jobapp(2, JobApp.class),
	produktapp(11,Produktapp.class),
	Configapp(20, Config.class);

	private int typeID;
	private Class<?> appClass;
	
	/**
	 * @param id
	 * @return ApplicationType
	 */
	public static ApplicationType byID(int id) {
		for (final ApplicationType a : ApplicationType.values()) {
			if (a.getTypeID() == id) {
				return a;
			}
		}

		return null;
	}

	/**
	 * @param typeID
	 * @param appClass
	 */
	private ApplicationType(int typeID, Class<?> appClass) {
		this.typeID = typeID;
		this.appClass = appClass;
	}

	/**
	 * @return appclass
	 */
	public Class<?> getAppClass() {
		return appClass;
	}

	/**
	 * @return typeID
	 */
	public int getTypeID() {
		return typeID;
	}
}
