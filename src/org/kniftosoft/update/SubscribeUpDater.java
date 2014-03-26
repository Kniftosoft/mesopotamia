package org.kniftosoft.update;

import java.util.Map;

import org.kniftosoft.application.Appinstance;
import org.kniftosoft.entity.Log;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.EuphratisSession;
import org.kniftosoft.util.Peerholder;

/**
 * @author julian
 * 
 */
public class SubscribeUpDater {

	/**
	 * @param log
	 */
	public static void updateSubscriptions(Log log) {
		final Map<String, EuphratisSession> peers = Peerholder.getpeers();
		for (final EuphratisSession peer : peers.values()) {
			if (peer.isLoginverified() == true) {
				try {
					for (final Subscribe sub : peer.getUser().getSubscribes()) {
						if (sub.getObjektID() == log.getMaschineBean()
								.getIdmaschine()) {
							final Appinstance app = new Appinstance(sub, peer);
							app.update();
						}
					}

				} catch (final NullPointerException e) {
					// TODO Could happen if user has no subscribes find other way
				}
			}

		}
	}

}
