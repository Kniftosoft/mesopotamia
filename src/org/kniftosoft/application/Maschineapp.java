/**
 * 
 */
package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.Log;
import org.kniftosoft.entity.Maschine;
import org.kniftosoft.entity.Subbedmaschine;
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
	private User user;
	private List<Useraccess> access;
	private List<Maschine> maschines = new ArrayList<Maschine>();
	JsonArray datas = new JsonArray();
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		// TODO Auto-generated method stub
		this.user = sub.getUserBean();
		getids();
	    System.out.println("found"+sub.toString());
		for(Iterator<Subbedmaschine> iterator = sub.getSubbedmaschines().iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next().getMaschineBean()));
		 }

		
		return datas;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(User user,String ident) {
		// TODO Auto-generated method stub
		this.user = user;
		getids();
		for(Iterator<Maschine> iterator = maschines.iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next()));
		 }

		
		return datas;
	}
	private void getids()
	{
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<Useraccess> acc = em.createQuery("Select u FROM Useraccess u WHERE u.userBean=:user", Useraccess.class).setParameter("user", user);
	    em.getTransaction().commit();
	    access = acc.getResultList();
	    em.close();
		for(Iterator<Useraccess> iterator = access.iterator(); iterator.hasNext();)
		{		
			//TODO add itent check
			maschines.add(iterator.next().getMaschineBean());
		}
	}
	private JsonObject getsingledataset(Maschine maschine)
	{
		List<Log> logs;
		JsonObject data = new JsonObject();
	    System.out.println("found"+maschine.toString());
	    System.out.println("found"+maschine.toString());
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    logs = em.createQuery("Select l FROM Log l WHERE l.maschineBean =:maschine ORDER BY l.timestamp DESC ", Log.class).setParameter("maschine", maschine).setMaxResults(2).getResultList();
	    em.getTransaction().commit();
	    em.close();
	    //TODO Array index out of range
	  	data.addProperty("id", maschine.getIdmaschine());
	  	try
	  	{
	  		//Differenz zwischen timestamps in ms /(1000*60*60) (3600000) für die stunden
			data.addProperty("speed", (double)Math.round((double)logs.get(0).getProduziert()/(logs.get(0).getTimestamp().getTime()-logs.get(1).getTimestamp().getTime())*3600000*100)/100);
			data.addProperty("name", maschine.getName());
			data.addProperty("job", logs.get(0).getAuftragBean().getIdauftrag());
			data.addProperty("status", logs.get(0).getZustandBean().getIdzustand());
	  	}
	  	catch(ArrayIndexOutOfBoundsException e)
	  	{
	  		//TODO optimize
	  		data.addProperty("speed", 0);
			data.addProperty("name", maschine.getName());
			data.addProperty("job", 0);
			data.addProperty("status", 2);
	  	}
		
		return data;
	}

}
