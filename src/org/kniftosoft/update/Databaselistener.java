package org.kniftosoft.update;

import javax.persistence.PostPersist;

import org.kniftosoft.entity.Log;

/**
 * @author julian
 * 
 */
public class Databaselistener {

	/**
	 * update subscribers after a new log is found
	 * 
	 * @param log       
	 */
	@PostPersist
	void onPostPersist(Log log) {
		// works only if update is done by logGen
		new SubscribeUpDater().updateSubscriptions(log);
	}
}
