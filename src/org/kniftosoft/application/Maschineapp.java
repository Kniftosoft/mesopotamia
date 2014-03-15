/**
 * 
 */
package org.kniftosoft.application;

import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kniftosoft.entity.Log;
import org.kniftosoft.entity.Subbedmaschine;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class Maschineapp extends Application {

	
	JsonArray datas = new JsonArray();
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		// TODO Auto-generated method stub
		for(Iterator<Subbedmaschine> iterator = sub.getSubbedmaschines().iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next().getMaschineBean().getIdmaschine()));
		 }

		
		return datas;
	}
	
	private JsonObject getsingledataset(int id)
	{
		List<Log> logs;
		JsonObject data = new JsonObject();
		
		
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    logs = em.createQuery("Select l FROM log l WHERE l.maschine ='"+id+"' ORDER BY l.timestamp DESC ", Log.class).setMaxResults(2).getResultList();
	    em.getTransaction().commit();
	    em.close();
	    
		
		data.addProperty("id", id);
		//Differenz zwischen timestamps in ms /(1000*60*60) (3600000) für die stunden
		data.addProperty("speed", (logs.get(0).getProduziert()/(logs.get(0).getTimestamp().getTime()-logs.get(1).getTimestamp().getTime()))/3600000);
		data.addProperty("name", logs.get(0).getMaschineBean().getName());
		return data;
	}

}
