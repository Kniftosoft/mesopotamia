package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;

import org.kniftosoft.entity.Configtype;
import org.kniftosoft.entity.Userconfig;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class CONFIG extends Packet {

	private int id;
	private String value;


	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		id = o.get("id").getAsInt();
		value = o.get("value").getAsString();
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
			final Userconfig conf = new Userconfig();
			conf.setUserBean(peer.getUser());
			conf.setValue(value);	
			conf.setConfigtype(em.find(Configtype.class, id));
			em.getTransaction().begin();
			em.persist(conf);
			em.getTransaction().commit();
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
		data.addProperty("value", value);
		return data;
	}

}
