package org.kniftosoft.util;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

/**
 * @author julian
 * 
 */
public class Peerholder {

	private static Map<String, EuphratisSession> peers = new HashMap<String, EuphratisSession>();

	/**
	 * @param peer
	 */
	public static void addpeer(EuphratisSession peer) {
		peers.put(peer.getSession().getId(), peer);
	}

	/**
	 * @param peer
	 */
	public static void addpeer(Session peer) {
		final EuphratisSession es = new EuphratisSession(peer);
		peers.put(peer.getId(), es);
	}

	/**
	 * @param peer
	 * @return peer
	 */
	public static EuphratisSession getpeer(EuphratisSession peer) {
		return peers.get(peer.getSession().getId());
	}

	/**
	 * @param peer
	 * @return peer
	 */
	public static EuphratisSession getpeer(Session peer) {
		return peers.get(peer.getId());
	}

	/**
	 * @param peerID
	 * @return peer
	 */
	public static EuphratisSession getpeer(String peerID) {
		return peers.get(peerID);
	}

	/**
	 * @return peers
	 */
	public static Map<String, EuphratisSession> getpeers() {
		return peers;
	}

	/**
	 * @param peer
	 */
	public static void removepeer(EuphratisSession peer) {
		peers.remove(peer.getSession().getId());
	}

	/**
	 * @param peer
	 */
	public static void removepeer(Session peer) {
		peers.remove(peer.getId());
	}

	/**
	 * @param peer
	 */
	public static void updatepeer(EuphratisSession peer) {
		removepeer(peer);
		addpeer(peer);
	}
}
