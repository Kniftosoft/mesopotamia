package org.kniftosoft.thread.updater;

import javax.persistence.PostPersist;

import org.kniftosoft.entity.Log;

public class Databaselistener {

	@PostPersist void onPostPersist(Log log)
	{
		//w0orks only if update is done by logGen
		System.err.println("POST PERSIST");
		new SubscribeUpDater().updateSubscriptions(log);
		System.err.println("has updated subscriptions");
	}
}
