package org.kniftosoft.util.packet;

import java.util.Iterator;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonObject;

//TODO check class funktion
/**
 * @author julian
 * 
 */
public class UNSUBSCRIBE extends Packet {
	private int category;
	private String id;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		category = o.get("category").getAsInt();
		id = o.get("id").getAsString();

	}

	//TODO add error response
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		boolean found = false;
		// check for wildcard
		if (id == "*") {
			for (final Iterator<Subscribe> iterator = peer.getUser()
					.getSubscribes().iterator(); iterator.hasNext()
					|| found == false;) {
				final Subscribe sub = iterator.next();
				// delete all subscribes for the user with this category
				if (sub.getAppBean().getIdapp() == category) {
					found = true;
					final EntityManager em = Constants.factory
							.createEntityManager();
					Subscribe remsub = em.merge(sub);
					em.remove(remsub);
					em.remove(sub);
					em.close();
					ACK ack = new ACK();
					ack.setPeer(peer);
					ack.setUID(uid);
					ack.send();
				}
			}
		} else {
			for (final Iterator<Subscribe> iterator = peer.getUser()
					.getSubscribes().iterator(); iterator.hasNext()
					|| found == false;) {
				final Subscribe sub = iterator.next();
				// delete all subscribes for the user with this category and
				// objekt
				if (sub.getObjektID() == Integer.parseInt(id)
						&& sub.getAppBean().getIdapp() == category) {
					found = true;
					final EntityManager em = Constants.factory
							.createEntityManager();
					Subscribe remsub = em.merge(sub);
					em.remove(remsub);
					em.close();
					ACK ack = new ACK();
					ack.setPeer(peer);
					ack.setUID(uid);
					ack.send();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.UNSUBSCRIBE;
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
		data.addProperty("id", id);
		return data;
	}

}
