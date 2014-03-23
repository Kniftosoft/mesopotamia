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
		Log log = new Log();
		final EntityManager ems = Constants.factory.createEntityManager();
		ems.getTransaction().begin();
		try {
			final EntityManager em = Constants.factory.createEntityManager();
			em.getTransaction().begin();
			log = em.createQuery(
					"Select l FROM Log l WHERE l.maschineBean =:maschine ORDER BY l.timestamp DESC ",
					Log.class).setParameter("maschine", maschine)
					.setMaxResults(1).getSingleResult();
			em.getTransaction().commit();
			em.close();

		} catch (final NoResultException e) {

			jobs = ems.createNamedQuery("Auftrag.findAll", Auftrag.class)
					.getResultList();
			stats = ems.createNamedQuery("Zustand.findAll", Zustand.class)
					.getResultList();
			log.setAuftragBean(jobs.get(random(jobs.size())));
			log.setZustandBean(stats.get(random(stats.size())));
		}
		log.setIdlog(0);
		log.setMaschineBean(maschine);
		// TODO only running machines have a speed
		log.setProduziert(random(3));
		log.setTimestamp(new Timestamp(new Date().getTime()));
		ems.persist(log);
		System.err.print("new log");
		ems.getTransaction().commit();
		ems.close();
	}

	/**
	 * Generate logs to simulate working machines
	 */
	public void genlogs() {
		final EntityManager em = Constants.factory.createEntityManager();
		maschines = em.createNamedQuery("Maschine.findAll", Maschine.class)
				.getResultList();
		for (final Maschine maschine : maschines) {
			generatelog(maschine);
		}
		em.close();
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
}
