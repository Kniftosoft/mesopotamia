/**
 * 
 */
package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

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
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(Subscribe sub) 
	{
		JsonArray datas = new JsonArray();
	    System.out.println("found"+sub.toString());
	    EntityManager em = Constants.factory.createEntityManager();
	    datas.add(getsingledataset(em.find(Auftrag.class, sub.getObjektID())));
	    em.close();		
		return datas;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata()
	 */
	@Override
	public JsonArray getdata(User user,String ident) {
		JsonArray datas = new JsonArray();
		for(Iterator<Auftrag> iterator = readjobs().iterator(); iterator.hasNext();)
		 {
			datas.add(getsingledataset(iterator.next()));
		 }
		return datas;
	}
	private List<Auftrag> readjobs()
	{
		List<Auftrag> jobs = new ArrayList<Auftrag>();
		EntityManager em = Constants.factory.createEntityManager();
	    em.getTransaction().begin();
	   	jobs = em.createQuery("Select j FROM Auftrag j ", Auftrag.class).getResultList();
	    em.getTransaction().commit();
	    em.close();
	    return jobs;
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
	@Override
	public int getid() {
		// TODO Auto-generated method stub
		return ApplicationType.jobapp.getTypeID();
	}

}
