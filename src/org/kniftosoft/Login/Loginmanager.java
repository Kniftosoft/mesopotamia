package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.User;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.SHA256Generator;
import org.kniftosoft.util.packet.answer.AUTHPacket;
import org.kniftosoft.util.packet.answer.NACKPackage;
import org.kniftosoft.util.packet.answer.REAUTHPackage;
import org.kniftosoft.util.packet.recived.RecivedPacket;


public class Loginmanager {
	
	public static void login(RecivedPacket rp){
		String email = rp.getData().get("username").getAsString().toLowerCase();
		String pass = rp.getData().get("passwordHash").getAsString();
		try
		{
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
		    	AUTHPacket ap = new AUTHPacket(rp.getUid(), rp.getPeer());
		    	ap.send();
		    }
		    else
		    {
		    	NACKPackage ap = new NACKPackage(rp.getUid(), rp.getPeer());
		    	ap.send();
		    }
		}catch(NoResultException e)
		{
			System.out.println("No Results:"+e.toString());
			return;
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
			REAUTHPackage ap = new REAUTHPackage(rp.getUid(), rp.getPeer());
	    	//TODO add user config
	    	ap.addDataProperty("newSessionID", rp.getPeer().getSession().getId());
	    	ap.addDataProperty("userConfig", "");
	    	ap.send();
		}
		else
		{			
			NACKPackage ap = new NACKPackage(rp.getUid(), rp.getPeer());
			ap.send();
		}

		
	}
}
