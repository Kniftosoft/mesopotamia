/**
 * 
 */
package org.kniftosoft.application;

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

	

	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.Subscribe)
	 */
	@Override
	public JsonArray getdata(Subscribe sub) {
		final JsonArray datas = new JsonArray();
		final EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		List<Produkt> produkts = em.createNamedQuery("Produkt.findAll", Produkt.class).getResultList();
		em.getTransaction().commit();
		em.close();
		for (final Produkt produkt : produkts) {
			datas.add(getsingeledataset(produkt));
		}

		return datas;
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getdata(org.kniftosoft.entity.User, java.lang.String)
	 */
	@Override
	public JsonArray getdata(User user, String ident) {
		final JsonArray datas = new JsonArray();
		final EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		List<Produkt> produkts = em.createNamedQuery("Produkt.findAll", Produkt.class).getResultList();
		em.getTransaction().commit();
		em.close();
		for (final Produkt produkt : produkts) {
			datas.add(getsingeledataset(produkt));
		}

		return datas;
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.application.Application#getid()
	 */
	@Override
	public int getid() {
		return ApplicationType.produktapp.getTypeID();
	}

	/**
	 * Collects all data for a single Produkt
	 * @param produkt
	 * @return data
	 */
	private JsonObject getsingeledataset(Produkt produkt) {
		final JsonObject data = new JsonObject();
		data.addProperty("id", produkt.getIdprodukt());
		data.addProperty("name", produkt.getBeschreibung());
		return data;
	}
}
