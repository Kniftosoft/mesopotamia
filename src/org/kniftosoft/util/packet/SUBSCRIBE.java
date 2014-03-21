package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.App;
import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.ErrorType;
import org.kniftosoft.util.Peerholder;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class SUBSCRIBE extends Packet {

	private int category;
	private int id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject data) {
		category = data.get("category").getAsInt();
		id = data.get("id").getAsInt();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		try {
			final EntityManager em = Constants.factory.createEntityManager();
			em.getTransaction().begin();
			final Subscribe sub = new Subscribe();
			sub.setAppBean(em.find(App.class, category));
			sub.setUserBean(peer.getUser());
			sub.setObjektID(id);
			sub.getUserBean().addSubscribe(sub);
			peer.setUser(sub.getUserBean());
			Peerholder.updatepeer(peer);
			em.persist(sub);
			em.getTransaction().commit();
			em.close();
			final ACK ak = new ACK();
			ak.setUID(uid);
			ak.setPeer(peer);
			ak.send();
		} catch (final Exception e) {
			final ERROR er = new ERROR();
			er.setPeer(peer);
			er.setUID(id);
			er.setError(ErrorType.BAD_QUERY);
			er.setErrorMessage("something went wrong while subscribing");
			er.send();
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.SUBSCRIBE;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
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
	 * @return id
	 */
	public int getIdent() {
		return id;
	}

	/**
	 * @param category
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * @param ident
	 */
	public void setIdent(int ident) {
		id = ident;
	}
	
}
