package org.kniftosoft.util.packet;

import java.util.Iterator;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.Configtype;
import org.kniftosoft.entity.Userconfig;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class CONFIG extends Packet {

	private int id;
	private JsonArray values;


	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		id = o.get("id").getAsInt();
		values = o.get("value").getAsJsonArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		try
		{
			final EntityManager em = Constants.factory.createEntityManager();
			em.getTransaction().begin();
			final TypedQuery<Userconfig> delconf = em.createQuery(
					"Select c FROM Userconfig c WHERE c.userBean=:user AND c.configtype =:type",
					Userconfig.class).setParameter("user", peer.getUser()).setParameter("type", em.find(Configtype.class, id));
			for (Iterator<Userconfig> iterator = delconf.getResultList().iterator();iterator.hasNext();) {
				em.remove(iterator.next());
			}
			em.getTransaction().commit();
			for(Iterator<JsonElement> iterator = values.iterator(); iterator.hasNext();)
			{
				final Userconfig conf = new Userconfig();
				conf.setUserBean(peer.getUser());
				conf.setValue(iterator.next().getAsString());	
				conf.setConfigtype(em.find(Configtype.class, id));
				em.getTransaction().begin();
				em.persist(conf);
				em.getTransaction().commit();
			}
			em.close();
			ACK ack = new ACK();
			ack.setPeer(peer);
			ack.setUID(uid);
			ack.send();
		}catch(Exception e)
		{
			e.printStackTrace();
			NACK nack = new NACK();
			nack.setPeer(peer);
			nack.setUID(uid);
			nack.send();
		}
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.CONFIG;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("id", id);
		data.add("value", values);
		return data;
	}

}
