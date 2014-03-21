/**
 * 
 */
package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.App;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.thread.ClientUpDater;
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
			EntityManager em = Constants.factory.createEntityManager();
		    em.getTransaction().begin();
		    Subscribe sub = new Subscribe();
			sub.setAppBean(em.find(App.class, category));
			sub.setUserBean(peer.getUser());
			sub.setObjektID(id);
			sub.getUserBean().addSubscribe(sub);
			peer.setUser(sub.getUserBean());
			ClientUpDater.updatepeer(peer);
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

	/**
	 * @return category
	 */
	public int getCategory() {
		return category;
	}

	/**
	 * @param category
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * @return id
	 */
	public int getIdent() {
		return id;
	}

	/**
	 * @param ident
	 */
	public void setIdent(int ident) {
		this.id = ident;
	}

}
