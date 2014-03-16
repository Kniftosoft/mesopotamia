/**
 * 
 */
package org.kniftosoft.util.packet;

import javax.websocket.DecodeException;

import org.kniftosoft.application.Application;
import org.kniftosoft.application.ApplicationType;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class QUERY extends Packet {
	private int category;
	private String ident;
	private Application app;
	
	

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		// TODO execute query
		try
		{
			
			ApplicationType apptype = ApplicationType.byID(category);
			try 
			{	
				try
				{
					
					app = (Application) apptype.getAppClass().newInstance();
					DATA response = new DATA();
					response.setPeer(peer);
					response.setUID(uid);
					response.setResult(app.getdata(peer.getUser(),""));
					response.send();
				}catch (InstantiationException e) 
				{
					throw new DecodeException("Could not instantiate Application class of Application type " , apptype.name());	
				}catch (IllegalAccessException e) 
				{
					throw new DecodeException("Could not instantiate Application class of Application type " , apptype.name());
				}
			}catch(DecodeException e)
			{
				System.out.println(e.toString());
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.err.println("query"+e.toString());
			ERROR response = new ERROR(uid, peer,3, " internal problems");
			response.send();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		category = o.get("category").getAsInt();
		ident = o.get("id").getAsString();
	}

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.QUERY;
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
