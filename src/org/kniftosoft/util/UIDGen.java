package org.kniftosoft.util;

/**
 * @author julian
 */
public class UIDGen {

	private int nextUID;
	private static UIDGen singleton;

	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	// Warning!!! This Class is only for testing - it is not useful for UID
	// generation
	// where UIDs are supposed to be re-used
	// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

	/**
	 * @return singleton Return only a new UIDGen if no one already exist
	 */
	public static UIDGen instance() {
		if (singleton == null) {
			singleton = new UIDGen();
		}

		return singleton;
	}

	/**
	 * 
	 */
	private UIDGen() {
		nextUID = 2;
	}

	/**
	 * @return uid
	 */
	public int generateUID() {
		final int uid = nextUID;

		nextUID += 2; // Generate only odd UIDs

		return uid;
	}

}
