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

import org.kniftosoft.entity.Auftrag;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.entity.User;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class JobApp extends Application {

	private List<Auftrag> jobs = new ArrayList<Auftrag>();
	JsonArray datas = new JsonArray();
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		// TODO Auto-generated method stub
		readjobs();
		for(Iterator<Auftrag> iterator = jobs.iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next()));
		 }

		
		return datas;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(User user,String ident) {
		// TODO Auto-generated method stub
		readjobs();
		for(Iterator<Auftrag> iterator = jobs.iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next()));
		 }

		
		return datas;
	}
	private void readjobs()
	{
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	   	jobs = em.createQuery("Select j FROM Auftrag j ", Auftrag.class).getResultList();
	    em.getTransaction().commit();
	    em.close();
	}
	private JsonObject getsingledataset(Auftrag job)
	{
		JsonObject data = new JsonObject();
	  	data.addProperty("id", job.getIdauftrag());
		data.addProperty("target",job.getGroesse());
		if(job.getStartzeit() != null)
		{
			data.addProperty("startTime", job.getStartzeit().getTime());
		}
		else
		{
			data.addProperty("startTime", "");
		}
		
		data.addProperty("productType", job.getProduktBean().getIdprodukt());
		return data;
	}

}
