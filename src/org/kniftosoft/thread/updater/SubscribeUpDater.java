/**
 * 
 */
package org.kniftosoft.thread.updater;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kniftosoft.application.Appinstance;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.EuphratisSession;


/**
 * @author julian
 *
 */
public class SubscribeUpDater {

	private static Map<String, EuphratisSession> peers;
	
	private static void update(List<Subscribe> subscribes, EuphratisSession peer)
	{
		System.out.println("found subs: "+subscribes.toString());
		 for(Iterator<Subscribe> iterator = subscribes.iterator(); iterator.hasNext();)
		 {
			 Appinstance app = new Appinstance(iterator.next(),peer);	
			 app.update();
		 }
	}
	/**
	 * @return 
	 * 
	 */
	
	public static void updateSubscriptions() {
		peers = ClientUpDater.getpeers();
		for(Iterator<EuphratisSession> iterator = peers.values().iterator();iterator.hasNext();)
		{
			
			try
			{
				EuphratisSession peer = iterator.next();
				update(peer.getUser().getSubscribes(), peer);
				
			}
			catch(NullPointerException e)
			{
				
			}
		    
		}
	}

}
