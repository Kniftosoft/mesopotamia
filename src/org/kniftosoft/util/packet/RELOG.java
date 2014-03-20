/**
 * 
 */
package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.kniftosoft.entity.Session;
import org.kniftosoft.thread.ClientUpDater;
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
				EntityManager em = Constants.factory.createEntityManager();
			    Session session = em.find(Session.class, sessionID);
			    em.close();
			    //TODO add user and a crytic key to relog for security reasons if(session.getUserBean().equals(rp.getuser()))
			    if(session != null)
			    {
			    	System.out.println("relog");
			    	peer.setUser(session.getUserBean());
			    	peer.setLoginverified(true);
			    	ClientUpDater.updatepeer(peer);
			    	new REAUTH(uid, peer,session.getIdSessions(),session.getUserBean()).send();
			    }
			    else
			    {
			    	System.out.println("no session");
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
			sessionID = o.get("sessionID").getAsInt();
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
