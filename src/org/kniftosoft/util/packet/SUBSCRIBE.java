/**
 * 
 */
package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.kniftosoft.entity.App;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class SUBSCRIBE extends Packet {

	private int category;
	private String ident;

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		Subscribe sub = new Subscribe();
		sub.setAppBean(App.getbyID(category));
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    em.persist(sub);
	    em.getTransaction().commit();
	    em.close();

	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		category = o.get("category").getAsInt();
		ident = o.get("ident").getAsString();

	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.SUBSCRIBE;
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("category", category);
		data.addProperty("ident", ident);
		return data;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public String getIdent() {
		return ident;
	}

	public void setIdent(String ident) {
		this.ident = ident;
	}

}
