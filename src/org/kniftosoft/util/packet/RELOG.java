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

	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
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
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
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
			    	REAUTH reauth = new REAUTH();
			    	reauth.setPeer(peer);
			    	reauth.setUID(uid);
			    	//TODO give a real new id
			    	reauth.setNewSessionID(session.getIdSessions());
			    	reauth.setUser(session.getUserBean());
			    	reauth.send();
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
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("sessionID", sessionID);
		return data;
	}
	/* (non-Javadoc)
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		// TODO Auto-generated method stub
		return PacketType.RELOG;
	}
	/**
	 * @return sessionID
	 */
	public int getSessionID() {
		return sessionID;
	}
	/**
	 * @param sessionID
	 */
	public void setSessionID(int sessionID) {
		this.sessionID = sessionID;
	}

}
