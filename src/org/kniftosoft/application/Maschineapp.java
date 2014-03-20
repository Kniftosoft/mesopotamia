/**
 * 
 */
package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
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
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(Subscribe sub) 
	{
		JsonArray datas = new JsonArray();
	    System.out.println("found"+sub.toString());
	    EntityManager em = Constants.factory.createEntityManager();
	    datas.add(getsingledataset(em.find(Maschine.class, sub.getObjektID())));
	    em.close();

		
		return datas;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(User user,String id) 
	{
		JsonArray datas = new JsonArray();
		//TODO don't return all specify by id
		for(Iterator<Maschine> iterator = getids(user).iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next()));
		 }

		
		return datas;
	}
	private List<Maschine> getids(User user)
	{
		List<Maschine> maschines = new ArrayList<Maschine>();
		EntityManager em = Constants.factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<Useraccess> acc = em.createQuery("Select u FROM Useraccess u WHERE u.userBean=:user", Useraccess.class).setParameter("user", user);
	    em.getTransaction().commit();
		for(Iterator<Useraccess> iterator = acc.getResultList().iterator(); iterator.hasNext();)
		{		
			//TODO add id check
			maschines.add(iterator.next().getMaschineBean());
		}
	    em.close();
	    return maschines;
	}
	private JsonObject getsingledataset(Maschine maschine)
	{
		List<Log> logs;
		JsonObject data = new JsonObject();
	    System.out.println("found"+maschine.toString());
	    EntityManager em = Constants.factory.createEntityManager();
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
			System.out.println("got data from logid: "+logs.get(0).getIdlog()+" "+logs.get(1).getIdlog());
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
	@Override
	public int getid() {
		// TODO Auto-generated method stub
		return ApplicationType.Maschineapp.getTypeID();
	}

}
