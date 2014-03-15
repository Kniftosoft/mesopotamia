/**
 * 
 */
package org.kniftosoft.thread.updater;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.application.Appinstance;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.Constants;


/**
 * @author julian
 *
 */
public class SubscribeUpDater {

	private static List<Subscribe> readSubscribes()
	{
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<Subscribe> userquery=em.createQuery("SELECT s FROM Subscribe s",Subscribe.class);
	    em.getTransaction().commit();
	    List<Subscribe> subscribes= userquery.getResultList();
	    em.close();
	    return subscribes;
	}
	/**
	 * @return 
	 * 
	 */
	
	public static void updateSubscriptions() {
		// TODO Auto-generated constructor stub
		 List<Subscribe> subscribes = readSubscribes();
		 for(Iterator<Subscribe> iterator = subscribes.iterator(); iterator.hasNext();)
		 {
			 Appinstance app = new Appinstance(iterator.next());	
			 app.update();
		 }
		
	}

}
