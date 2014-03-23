package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

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
	 * 
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.Subscribe)
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		user = sub.getUserBean();
		final JsonArray datas = new JsonArray();
		readconfig();
		for (final Userconfig userconfig : configs) {
			datas.add(getsingeledataset(userconfig));
		}

		return datas;
	}

	//TODO id not used
	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.User, java.lang.String)
	 */
	@Override
	public JsonArray getdata(User user, String id) {
		final JsonArray datas = new JsonArray();
		readconfig();
		for (final Userconfig userconfig : configs) {
			datas.add(getsingeledataset(userconfig));
		}

		return datas;
	}

	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.application.Application#getid()
	 */
	@Override
	public int getid() {
		return ApplicationType.Configapp.getTypeID();
	}

	/**
	 * Collect all data for a single config dataset
	 * @param config
	 * @return data 
	 */
	private JsonObject getsingeledataset(Userconfig config) {
		final JsonObject data = new JsonObject();
		data.addProperty("id", config.getIduserconfig());
		data.addProperty("value", config.getValue());
		return data;
	}

	/**
	 * get all configs for a user
	 */
	private void readconfig() {
		final EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		configs = em
				.createQuery(
						"Select c FROM Userconfig c WHERE c.userBean =:user",
						Userconfig.class).setParameter("user", user)
				.getResultList();
		em.getTransaction().commit();
		em.close();
	}
}
