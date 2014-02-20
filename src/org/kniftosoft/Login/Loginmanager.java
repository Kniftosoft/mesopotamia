package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.entity.User;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.Packet;

import com.google.gson.JsonObject;

public class Loginmanager {
	public static Packet login(EuphratisSession es, String email, String pass){
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.getPersistenceUnitName());
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
	    em.getTransaction().commit();
	    User user= userquery.getSingleResult();
	    em.close();
	    if(email.toLowerCase().equals(user.getEmail().toLowerCase())&&pass.equals(user.getPassword())){
	    	es.setLoginverified(true);
	    	es.setUser(user);
	    	ClientUpDater.updatepeer(es);	
	    	
	    	JsonObject data = new JsonObject();
	    	data.addProperty("sessionID", es.getSession().getId());
	    	//TODO add user config
	    	data.addProperty("userConfig", "");
	    	return new Packet(11, data);
	    }
	    else
	    {
	    	return new Packet(201,null);
	    }
	}
	
	public static void Logout(EuphratisSession es ,JsonObject data)
	{
		if(es.getSession().getId().equals(data.get("sessionID").getAsString()))
		{
			//TODO remove sys out if checkt and handle different codes
			System.out.println("logg out"+data.get("sessionID").getAsString());
			es.setLoginverified(false);
			es.setUser(null);
			ClientUpDater.updatepeer(es);	
		}
	}

	public static Packet relog(EuphratisSession es ,JsonObject data)
	{
		if(ClientUpDater.getpeer(data.get("sessionID").getAsString()).isLoginverified())
		{
			JsonObject packetdata = new JsonObject();
			packetdata.addProperty("newSessionID", es.getSession().getId());
			//TODO add user config
			packetdata.addProperty("userConfig", "");
			return new Packet(13, packetdata);
		}
		else
		{			
			return new Packet(201, null);	
		}

		
	}
}
