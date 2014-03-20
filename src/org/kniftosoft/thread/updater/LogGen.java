/**
 * 
 */
package org.kniftosoft.thread.updater;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.kniftosoft.entity.Auftrag;
import org.kniftosoft.entity.Log;
import org.kniftosoft.entity.Maschine;
import org.kniftosoft.entity.Zustand;
import org.kniftosoft.util.Constants;

/**
 * @author julian
 *
 */
public class LogGen {

	private List<Maschine> maschines;
	private List<Auftrag> jobs;
	private List<Zustand> stats;

	private void generatelog(Maschine maschine)
	{
    	System.out.println("gen log for maschine: "+maschine.getIdmaschine());
		Log log = new Log();
		EntityManager ems = Constants.factory.createEntityManager();
		ems.getTransaction().begin();
		try
		{
			EntityManager em = Constants.factory.createEntityManager();
		    em.getTransaction().begin();
		    log = em.createQuery("Select l FROM Log l WHERE l.maschineBean =:maschine ORDER BY l.timestamp DESC ", Log.class).setParameter("maschine", maschine).setMaxResults(1).getSingleResult();
		    em.getTransaction().commit();
			em.close();
			
		}catch(NoResultException e)
		{
			
			jobs = ems.createNamedQuery("Auftrag.findAll",Auftrag.class).getResultList();
			stats = ems.createNamedQuery("Zustand.findAll",Zustand.class).getResultList();
			log.setAuftragBean(jobs.get(random(jobs.size())));
			log.setZustandBean(stats.get(random(stats.size())));
		}
		log.setIdlog(0);
		log.setMaschineBean(maschine);
		log.setProduziert(random(3));
		log.setTimestamp(new Timestamp(new Date().getTime()));
		ems.persist(log);
		ems.getTransaction().commit();
		ems.close();
	}
	private int random(int max)
	{
		int rand =new Random().nextInt();
		if(rand < 0)
		{
			rand *= -1;
		}
		rand = rand %max;
		System.out.println("rand: "+rand);
		return rand;
	}
	public void genlogs()
	{
		EntityManager em = Constants.factory.createEntityManager();
	    maschines = em.createNamedQuery("Maschine.findAll",Maschine.class).getResultList();
	    for(Iterator<Maschine> iterator = maschines.iterator();iterator.hasNext();)
	    {
	    	generatelog(iterator.next());
	    }
	    em.close();
	}
}
