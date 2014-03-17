/**
 * 
 */
package org.kniftosoft.thread;

import java.util.HashMap;
import java.util.Map;

import javax.websocket.Session;

import org.kniftosoft.thread.updater.SubscribeUpDater;
import org.kniftosoft.util.EuphratisSession;

/**
 * Thread Stores Connected peers and updates them 
 * @author julian
 *
 */
public class ClientUpDater extends Thread {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	int sleepTime = 10000;
	private static Map<String, EuphratisSession> peers = new HashMap<String, EuphratisSession>();
	
	/**
	 * 
	 * @param peerID
	 * @return Stored Session with this ID
	 */
	public static Map<String, EuphratisSession> getpeers()
	{
		return peers;
	}

	/** 
	 * @param peerID
	 * @return Stored Session with this ID
	 */
	public static EuphratisSession getpeer(String peerID)
	{
		return peers.get(peerID);
	}

	
	/**
	 * 
	 * @param peer adds this peer to the connected peers
	 */
	public static void addpeer(EuphratisSession peer)
	{
		peers.put(peer.getSession().getId(), peer);
	}
	/**
	 * 
	 * @param peer removes this peer from the connected peers
	 */
	public static void removepeer(EuphratisSession peer)
	{
		peers.remove(peer.getSession().getId());
	}
	/**
	 *  Updates a Stored peer
	 * @param peer
	 */
	public static void updatepeer(EuphratisSession peer)
	{
		removepeer(peer);
		addpeer(peer);
	}
	
	/**
	 * 
	 */
	public ClientUpDater()
	{
		System.out.println("Create Thread");
	}
	
	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			while(true) {
				//TODO remove
	        	//System.out.println("update" +Thread.currentThread().getName()+peers.toString());
	        	SubscribeUpDater.updateSubscriptions();
				Thread.currentThread();
				Thread.sleep(sleepTime);

			}
		} catch (InterruptedException e) {
			System.out.println("Update Thread stopps by Interrupt");
		}
	}

	public static EuphratisSession getpeer(EuphratisSession peer) {
		return peers.get(peer.getSession().getId());
	}

	public static EuphratisSession getpeer(Session peer) {
		return peers.get(peer.getId());
	}
}