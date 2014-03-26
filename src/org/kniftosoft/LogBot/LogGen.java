package org.kniftosoft.LogBot;

import java.sql.Timestamp;
import java.util.Date;
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

	/**
	 * @param maschine
	 */
	private void generatelog(Maschine maschine) {
		try{
			//create new log
			Log log = new Log();
			
			
			
			try {
				//get last log as log base
				final EntityManager em = Constants.factory.createEntityManager();
				em.getTransaction().begin();
				log = em.createQuery(
						"Select l FROM Log l WHERE l.maschineBean =:maschine ORDER BY l.timestamp DESC ",
						Log.class).setParameter("maschine", maschine)
						.setMaxResults(1).getSingleResult();
				em.getTransaction().commit();
				em.close();

			} catch (final NoResultException e) {
				// create new log
				log.setAuftragBean(jobs.get(random(jobs.size())));
				log.setZustandBean(stats.get(random(stats.size())));
			}

			//set new values
			//only running machines have a speed
			log.setProduziert(random(getmax(maschine.getMaximumspeed(), log.getTimestamp(), new Date()))+1);
			log.setIdlog(0);
			log.setMaschineBean(maschine);
			log.setTimestamp(new Timestamp(new Date().getTime()));
			//change state 10% rate
			if(random(1000)>900)
			{
				
				if(random(10)>5)
				{
					log.setZustandBean(stats.get(random(stats.size())-1));
				}else{
					log.setZustandBean(stats.get(0));
				}
				
			}
			
			if(log.getZustandBean().getBeschreibung().equals("Läuft")!=true)
			{
				log.setProduziert(0);
			}
			
			final EntityManager ems = Constants.factory.createEntityManager();
			ems.getTransaction().begin();
			//store log
			ems.persist(log);
			System.err.print("new log");
			ems.getTransaction().commit();
			ems.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	/**
	 * Generate logs to simulate working machines
	 */
	public void genlogs() {
		final EntityManager em = Constants.factory.createEntityManager();
		maschines = em.createNamedQuery("Maschine.findAll", Maschine.class)
				.getResultList();
		jobs = em.createNamedQuery("Auftrag.findAll", Auftrag.class)
				.getResultList();
		stats = em.createNamedQuery("Zustand.findAll", Zustand.class)
				.getResultList();
		for (final Maschine maschine : maschines) {
			generatelog(maschine);
		}
		em.close();
	}
	
	/**
	 * calculate maximal production of a machine
	 * @param maxspeed
	 * @param lastlog
	 * @param aktualltime
	 * @return maximum produced 
	 */
	private double getmax(double maxspeed,Date lastlog,Date aktualltime)
	{
		double maxproduce = (maxspeed* (aktualltime.getTime() - lastlog.getTime()) / 3600000)-1;
		return maxproduce;	
	}

	/**
	 * @param max
	 * @return rand
	 */
	private int random(int max) {
		int rand = new Random().nextInt();
		if (rand < 0) {
			rand *= -1;
		}
		rand = rand % max;
		return rand;
	}
	/**
	 * @param max
	 * @return rand
	 */
	private int random(double max) {
		int rand = new Random().nextInt();
		if (rand < 0) {
			rand *= -1;
		}
		rand = (int) (rand % max);
		return rand;
	}
}
