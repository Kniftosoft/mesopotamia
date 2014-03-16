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
	private int id;

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		try{
			EntityManagerFactory factory;
			factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
		    EntityManager em = factory.createEntityManager();
		    em.getTransaction().begin();
		    Subscribe sub = new Subscribe();
			sub.setAppBean(em.find(App.class, category));
			sub.setUserBean(peer.getUser());
		    em.persist(sub);
		    em.getTransaction().commit();
		    em.close();
		    ACK ak = new ACK();
		    ak.setUID(uid);
		    ak.setPeer(peer);
		    ak.send();
		}
		catch(Exception e)
		{
			ERROR er = new ERROR(uid, peer, 7, "something went wrong while subscribing");
			er.send();
			e.printStackTrace();
		}
		

	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		category = o.get("category").getAsInt();
		id = o.get("id").getAsInt();

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
		data.addProperty("ident", id);
		return data;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public int getIdent() {
		return id;
	}

	public void setIdent(int ident) {
		this.id = ident;
	}

}
