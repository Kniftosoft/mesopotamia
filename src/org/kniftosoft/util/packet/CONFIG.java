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

	private String id;
	private String value;


	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		id = o.get("id").getAsString();
		value = o.get("value").getAsString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		final Userconfig conf = new Userconfig();
		conf.setUserBean(peer.getUser());
		conf.setValue(value);
		final EntityManager em = Constants.factory.createEntityManager();
		conf.setConfigtype(em.find(Configtype.class, Integer.parseInt(id)));
		em.persist(conf);
		em.close();
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
