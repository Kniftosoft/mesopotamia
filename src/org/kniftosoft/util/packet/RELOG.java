/**
 * 
 */
package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

import org.kniftosoft.entity.Session;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class RELOG extends Packet{

	private int sessionID;
	private void relog()
	{
		if(sessionID != 0)
		{
			try {
				EntityManagerFactory factory;
				factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
			    EntityManager em = factory.createEntityManager();
			    Session session = em.find(Session.class, sessionID);
			    em.close();
			    //TODO add user to relog for security reasons if(session.getUserBean().equals(rp.getuser()))
			    if(session != null)
			    {
			    	System.out.println("relog");
			    	new REAUTH(uid, peer,session.getIdSessions(),null).send();
			    }
			    else
			    {
			    	new NACK(uid, peer).send();
			    }
			} catch (NoResultException nr) {
				nr.printStackTrace(); 
				new NACK(uid, peer).send();
			}
			catch(Exception e)
			{
				e.printStackTrace();
				new NACK(uid, peer).send();
			}
		}
		else
		{
			new NACK(uid, peer).send();
		}
	}

	@Override
	public void createFromJSON(JsonObject o) {
		try{
			//sessionID = o.get("sessionID").getAsInt();
			sessionID =0;
		}
		catch(NumberFormatException e)
		{
			sessionID = 0;
		}
		
	}
	@Override
	public void executerequest() {
		relog();
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		return data;
	}
	@Override
	public PacketType getType() {
		// TODO Auto-generated method stub
		return PacketType.RELOG;
	}
	public int getSessionID() {
		return sessionID;
	}
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

}
