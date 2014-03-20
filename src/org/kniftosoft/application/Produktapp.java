/**
 * 
 */
package org.kniftosoft.application;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Produkt;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.entity.User;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class Produktapp extends Application {

	
	List<Produkt> produkts = new ArrayList<Produkt>();
	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.Subscribe)
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		JsonArray datas = new JsonArray();
		readprodukt();
		for(Iterator<Produkt> iterator = produkts.iterator(); iterator.hasNext();)
		 {	
			datas.add(getsingeledataset(iterator.next()));
		 }

		return datas;
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.User, java.lang.String)
	 */
	@Override
	public JsonArray getdata(User user, String ident) {
		JsonArray datas = new JsonArray();
		readprodukt();
		for(Iterator<Produkt> iterator = produkts.iterator(); iterator.hasNext();)
		 {	
			datas.add(getsingeledataset(iterator.next()));
		 }

		return datas;
	}
	private JsonObject getsingeledataset(Produkt produkt)
	{
		JsonObject data = new JsonObject();
		data.addProperty("id", produkt.getIdprodukt());
		data.addProperty("name", produkt.getBeschreibung());
		return data;
	}
	private void readprodukt()
	{
		EntityManager em = Constants.factory.createEntityManager();
	    em.getTransaction().begin();
	    produkts = em.createNamedQuery("Produkt.findAll",Produkt.class).getResultList();
	    em.getTransaction().commit();
	    em.close();
	}

	@Override
	public int getid() {
		// TODO Auto-generated method stub
		return ApplicationType.produktapp.getTypeID();
	}

}
