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

import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.entity.User;
import org.kniftosoft.entity.Userconfig;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class Config extends Application {

	User user;
	List<Userconfig> configs = new ArrayList<Userconfig>();
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.Subscribe)
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		user = sub.getUserBean();
		JsonArray datas = new JsonArray();
		readconfig();
		for(Iterator<Userconfig> iterator = configs.iterator(); iterator.hasNext();)
		 {	
			datas.add(getsingeledataset(iterator.next()));
		 }

		return datas;
	}

	@Override
	public JsonArray getdata(User user, String ident) {
		JsonArray datas = new JsonArray();
		readconfig();
		for(Iterator<Userconfig> iterator = configs.iterator(); iterator.hasNext();)
		 {	
			datas.add(getsingeledataset(iterator.next()));
		 }

		return datas;
	}

	private JsonObject getsingeledataset(Userconfig config)
	{
		JsonObject data = new JsonObject();
		data.addProperty("id", config.getIduserconfig());
		data.addProperty("value", config.getValue());
		return data;
	}


	private void readconfig()
	{
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    configs = em.createQuery("Select c FROM Userconfig c WHERE c.userBean =:user", Userconfig.class).setParameter("user", user).getResultList();
	    em.getTransaction().commit();
	    em.close();
	}
}
