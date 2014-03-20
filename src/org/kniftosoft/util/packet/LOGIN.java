/**
 * 
 */
package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.Session;
import org.kniftosoft.entity.User;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.SHA256Generator;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class LOGIN extends Packet {

	private String username;
	private String passwordHash;
	private boolean persist;
	
	@Override
	public void executerequest(){
		login();
	}

	@Override
	public void createFromJSON(JsonObject o) {
		if(o.has("username")&&o.has("passwordHash")&&o.has("persist"))
		{
			username = o.get("username").getAsString();
			passwordHash = o.get("passwordHash").getAsString();
			persist = o.get("persist").getAsBoolean();
		}
		else
		{
			ERROR er = new ERROR(uid, peer, 8, "missing data fields");
			er.send();
		}
		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("username", username);
		data.addProperty("passwordHash", passwordHash);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.LOGIN;
	}
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the passwordHash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}
	/**
	 * @param passwordHash the passwordHash to set
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	private void login(){
		if(peer.getSalt()!=null&&peer.isSaltused()==false)
		{
			String email =username.toLowerCase();
			String pass = passwordHash;
			try
			{
				EntityManager em = Constants.factory.createEntityManager();
			    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
			    //TODO find classcast bug fix
			    User user= userquery.getSingleResult();
			    String password = user.getPassword()+ClientUpDater.getpeer(peer).getSalt();
			    if(email.toLowerCase().equals(user.getEmail().toLowerCase())&&pass.equals(SHA256Generator.StringTOSHA256(password))){
			    	peer.setLoginverified(true);
			    	peer.setUser(user);
			    	peer.setSaltused(true);
			    	ClientUpDater.updatepeer(peer);
			    	Session session=new Session();
			    	session.setUserBean(user);
			    	if(persist == true)
			    	{
				    	em.getTransaction().begin();
				    	em.persist(session);
				    	System.out.println("storing session= "+session.getIdSessions());
				    	em.getTransaction().commit();
			    	}
			    	//TODO add userconfig
			    	AUTH ap = new AUTH();
			    	ap.setSessionID(session.getIdSessions());
			    	ap.setPeer(peer);
			    	ap.setUID(uid);
			    	ap.setUserconfig(null);
			    	ap.send();
			    }
			    else
			    {
			    	System.out.println("logini fail");
			    	new NACK(uid, peer).send();
			    }
			    
			    em.close();
			
		        
			}catch(NoResultException e)
			{
				e.printStackTrace();
				new NACK(uid,peer).send();
				return;
			}
			catch(ClassCastException e)
			{
				e.printStackTrace();
				new NACK(uid,peer).send();
				return;
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}

}
