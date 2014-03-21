package org.kniftosoft.util.packet;

import javax.websocket.DecodeException;

import org.kniftosoft.application.Application;
import org.kniftosoft.application.ApplicationType;
import org.kniftosoft.util.ErrorType;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class QUERY extends Packet {
	private Application app;
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		if (category != 0) {
			try {
				final ApplicationType apptype = ApplicationType.byID(category);
				try {

					app = (Application) apptype.getAppClass().newInstance();
					final DATA response = new DATA();
					response.setPeer(peer);
					response.setUID(uid);
					response.setResult(app.getdata(peer.getUser(), ""));
					response.setCategory(app.getid());
					response.send();
				} catch (final InstantiationException e) {
					throw new DecodeException(
							"Could not instantiate Application class of Application type ",
							apptype.name());
				} catch (final IllegalAccessException e) {
					throw new DecodeException(
							"Could not instantiate Application class of Application type ",
							apptype.name());
				}
			} catch (final DecodeException e) {
				System.out.println(e.toString());
			} catch (final Exception e) {
				e.printStackTrace();
				final ERROR er = new ERROR();
				er.setPeer(peer);
				er.setUID(uid);
				er.setError(ErrorType.INTERNAL_EXCEPTION);
				er.send();
			}
		} else {
			final ERROR er = new ERROR();
			er.setPeer(peer);
			er.setUID(uid);
			er.setError(ErrorType.NOT_ALLOWED);
			er.setErrorMessage("Category 0 ist not allowed for QUERY-Packets");
			er.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.QUERY;
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

	/**
	 * @return category
	 */
	public int getCategory() {
		return category;
	}

	/**
	 * @return id
	 */
	public String getID() {
		return id;
	}

	/**
	 * @param category
	 */
	public void setCategory(int category) {
		this.category = category;
	}

	/**
	 * @param id
	 */
	public void setID(String id) {
		this.id = id;
	}

}
