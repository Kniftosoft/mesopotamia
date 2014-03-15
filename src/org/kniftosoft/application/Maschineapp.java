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
import org.kniftosoft.entity.Maschine;
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
			datas.add(getsingledataset(iterator.next().getMaschineBean()));
		 }

		
		return datas;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(List<Maschine> maschines) {
		// TODO Auto-generated method stub
		for(Iterator<Maschine> iterator = maschines.iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next()));
		 }

		
		return datas;
	}
	private JsonObject getsingledataset(Maschine maschine)
	{
		List<Log> logs;
		JsonObject data = new JsonObject();
		
		System.out.println("search for id: "+maschine);
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    logs = em.createQuery("Select l FROM Log l WHERE l.maschineBean =:maschine ORDER BY l.timestamp DESC ", Log.class).setParameter("maschine", maschine).setMaxResults(2).getResultList();
	    em.getTransaction().commit();
	    em.close();
	    
		
		data.addProperty("id", maschine.getIdmaschine());
		//Differenz zwischen timestamps in ms /(1000*60*60) (3600000) für die stunden
		data.addProperty("speed", (double)Math.round((double)logs.get(0).getProduziert()/(logs.get(0).getTimestamp().getTime()-logs.get(1).getTimestamp().getTime())*3600000*100)/100);
		data.addProperty("name", logs.get(0).getMaschineBean().getName());
		data.addProperty("job", logs.get(0).getAuftragBean().getIdauftrag());
		System.out.println("got single data set"+data.toString());
		return data;
	}

}
