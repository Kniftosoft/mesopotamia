/**
 * 
 */
package org.kniftosoft.thread.updater;

import java.util.Iterator;
import java.util.Map;

import org.kniftosoft.application.Appinstance;
import org.kniftosoft.entity.Log;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.EuphratisSession;


/**
 * @author julian
 *
 */
public class SubscribeUpDater {
	
	/**
	 * @return 
	 * 
	 */
	
	public  void updateSubscriptions(Log log) 
	{
		Map<String, EuphratisSession> peers = ClientUpDater.getpeers();
		for(Iterator<EuphratisSession> iterator = peers.values().iterator();iterator.hasNext();)
		{
			EuphratisSession peer = iterator.next();
			if(peer.isLoginverified()==true)
			{
				System.out.println("found logged in connection: "+peer.toString());
				try
				{
					System.out.println(peer.getUser().toString());
					System.out.println(peer.getUser().getSubscribes().toString());
					for(Iterator<Subscribe> iterator2 = peer.getUser().getSubscribes().iterator();iterator2.hasNext();)
					{
						Subscribe sub = iterator2.next();
						System.out.println("comparing ids: "+sub.getObjektID()+" "+log.getMaschineBean().getIdmaschine());
						if(sub.getObjektID()==log.getMaschineBean().getIdmaschine())
						{
							Appinstance app = new Appinstance(sub,peer);	
							app.update();
							//update(peer.getUser().getSubscribes(), peer);
						}
					}
		
				}
				catch(NullPointerException e)
				{
					
				}
			}
		    
		}
	}

}
