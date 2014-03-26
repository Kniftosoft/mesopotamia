package org.kniftosoft.update;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Auftrag;
import org.kniftosoft.entity.Log;
import org.kniftosoft.util.Constants;

/**
 * 
 * @author julian
 *
 */
public class Jobupdater {
	/**
	 * set the Startzeit for the first log of a job
	 * @param log 
	 */
	public static void startJob(Log log)
	{
		
		EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		Auftrag job = em.find(Auftrag.class, log.getAuftragBean().getIdauftrag());
		if(job.getStartzeit()==null)
		{
			job.setStartzeit(log.getTimestamp());
		}
		em.persist(job);
		em.getTransaction().commit();
		em.close();
	}
}
