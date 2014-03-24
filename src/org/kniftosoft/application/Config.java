package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Configtype;
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
	List<Configtype> types  = new ArrayList<Configtype>();

	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.Subscribe)
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		gettypes();
		user = sub.getUserBean();
		final JsonArray datas = new JsonArray();
		for (final Configtype type : types) {
			datas.add(getsingeledataset(sub.getUserBean().getUserconfigs(),type));
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
		gettypes();
		final JsonArray datas = new JsonArray();
		for (final Configtype type : types) {
			JsonObject data = getsingeledataset(user.getUserconfigs(),type);
			datas.add(data);			
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
	private JsonObject getsingeledataset(List<Userconfig> config,Configtype type) {
		JsonObject data = null;
		final JsonArray values = new JsonArray();
		for(Userconfig conf : config)
		{
			if(conf.getConfigtype().getIdConfigtypes() == type.getIdConfigtypes())
			{
				JsonObject value = new JsonObject();
				value.addProperty("1", conf.getValue());
				values.add(value);
			}
		}

		data = new JsonObject();
		data.addProperty("id", type.getIdConfigtypes());
		data.add("value",values);	
		return data;
	}
	
	private void gettypes()
	{
		final EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		types = em.createNamedQuery("Configtype.findAll",Configtype.class).getResultList();
		em.getTransaction().commit();
		em.close();
	}
	
}
