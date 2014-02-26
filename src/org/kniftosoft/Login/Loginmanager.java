package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.User;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.AnswerPacket;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.RecivedPacket;
import org.kniftosoft.util.SHA256Generator;


public class Loginmanager {
	
	public static void login(RecivedPacket rp){
		String email = rp.getData().get("username").getAsString().toLowerCase();
		String pass = rp.getData().get("passwordHash").getAsString();
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.getPersistenceUnitName());
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
	    em.getTransaction().commit();
	    User user= userquery.getSingleResult();
	    em.close();

        String password = user.getPassword()+ClientUpDater.getpeer(rp.getPeer()).getSalt();
	   
	    if(email.toLowerCase().equals(user.getEmail().toLowerCase())&&pass.equals(SHA256Generator.StringTOSHA256(password))){
	    	rp.getPeer().setLoginverified(true);
	    	rp.getPeer().setUser(user);
	    	ClientUpDater.updatepeer(rp.getPeer());	
	    	AnswerPacket ap = new AnswerPacket(11, rp.getUid(), rp.getPeer());
	    	//TODO add user config
	    	ap.addDataProperty("sessionID", rp.getPeer().getSession().getId());
	    	ap.addDataProperty("userConfig", "");
	    	ap.send();
	    }
	    else
	    {
	    	AnswerPacket ap = new AnswerPacket(201, rp.getUid(), rp.getPeer());
	    	ap.send();
	    }
	}
	
	public static void Logout(RecivedPacket rp)
	{
		if(rp.getPeer().getSession().getId().equals(rp.getData().get("sessionID").getAsString()))
		{
			//TODO remove sys out if checkt and handle different codes
			System.out.println("logg out"+rp.getData().get("sessionID").getAsString());
			rp.getPeer().setLoginverified(false);
			rp.getPeer().setUser(null);
			ClientUpDater.updatepeer(rp.getPeer());	
		}
	}

	public static void relog(RecivedPacket rp)
	{
		if(ClientUpDater.getpeer(rp.getData().get("sessionID").getAsString()).isLoginverified())
		{
			AnswerPacket ap = new AnswerPacket(13, rp.getUid(), rp.getPeer());
	    	//TODO add user config
	    	ap.addDataProperty("newSessionID", rp.getPeer().getSession().getId());
	    	ap.addDataProperty("userConfig", "");
	    	ap.send();
		}
		else
		{			
			AnswerPacket ap = new AnswerPacket(201, rp.getUid(), rp.getPeer());
			ap.send();
		}

		
	}
}
