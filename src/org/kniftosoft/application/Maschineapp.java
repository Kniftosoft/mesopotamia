package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.Log;
import org.kniftosoft.entity.Maschine;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.entity.User;
import org.kniftosoft.entity.Useraccess;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class Maschineapp extends Application {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		final JsonArray datas = new JsonArray();
		final EntityManager em = Constants.factory.createEntityManager();
		datas.add(getsingledataset(em.find(Maschine.class, sub.getObjektID())));
		em.close();

		return datas;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(User user, String id) {
		final JsonArray datas = new JsonArray();
		// TODO don't return all specify by id
		for (final Maschine maschine : getids(user)) {
			datas.add(getsingledataset(maschine));
		}

		return datas;
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getid()
	 */
	@Override
	public int getid() {
		return ApplicationType.Maschineapp.getTypeID();
	}

	/**
	 * get all machines the user have permissions for
	 * @param user
	 * @return machines
	 */
	private List<Maschine> getids(User user) {
		final List<Maschine> machines = new ArrayList<Maschine>();
		final EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		final TypedQuery<Useraccess> acc = em.createQuery(
				"Select u FROM Useraccess u WHERE u.userBean=:user",
				Useraccess.class).setParameter("user", user);
		em.getTransaction().commit();
		for (final Useraccess useraccess : acc.getResultList()) {
			// TODO add id check
			machines.add(useraccess.getMaschineBean());
		}
		em.close();
		return machines;
	}

	/**
	 * Collect all data for a single Machine dataset
	 * @param maschine
	 * @return data
	 */
	private JsonObject getsingledataset(Maschine maschine) {
		List<Log> logs;
		Long total=(long) 0;
		final JsonObject data = new JsonObject();
		System.out.println("found" + maschine.toString());
		final EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		logs = em
				.createQuery(
						"Select l FROM Log l WHERE l.maschineBean =:maschine ORDER BY l.timestamp DESC ",
						Log.class).setParameter("maschine", maschine)
				.setMaxResults(2).getResultList();
		Query q =em.createQuery("SELECT SUM(l.produziert) FROM Log l WHERE l.maschineBean =:maschine GROUP BY l.maschineBean").setParameter("maschine", maschine);
		total = (Long) q.getSingleResult();	
		em.getTransaction().commit();
		em.close();
		
		// TODO Array index out of range
		data.addProperty("id", maschine.getIdmaschine());
		try {
			// Differenz zwischen timestamps in ms /(1000*60*60) (3600000) für
			// die stunden
			data.addProperty("speed",
					(double) Math.round((double) logs.get(0).getProduziert()
							/ (logs.get(0).getTimestamp().getTime() - logs.get(1).getTimestamp().getTime()) * 3600000* 100) / 100);
			data.addProperty("name", maschine.getName());
			data.addProperty("job", logs.get(0).getAuftragBean().getIdauftrag());
			data.addProperty("status", logs.get(0).getZustandBean().getIdzustand());
			data.addProperty("maxSpeed", maschine.getMaximumspeed());	
			//TODO dont works right (mysql limit 1000)
			data.addProperty("totalProduced", total);
			data.addProperty("location", maschine.getStandort());
		} catch (final ArrayIndexOutOfBoundsException e) {
			// TODO optimize
			data.addProperty("speed", 0);
			data.addProperty("name", maschine.getName());
			data.addProperty("job", 0);
			data.addProperty("maxSpeed", 0);
			data.addProperty("totalProduced", 0);
			data.addProperty("status", 2);
			data.addProperty("location", maschine.getStandort());
		}

		return data;
	}

}
