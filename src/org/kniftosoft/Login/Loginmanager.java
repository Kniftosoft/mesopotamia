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
import org.kniftosoft.util.packet.AUTH;
import org.kniftosoft.util.packet.LOGIN;
import org.kniftosoft.util.packet.LOGOUT;
import org.kniftosoft.util.packet.NACK;
import org.kniftosoft.util.packet.REAUTH;
import org.kniftosoft.util.packet.RELOG;


public class Loginmanager {
	
	public static void login(LOGIN rp){
		if(rp.getPeer().getSalt()!=null&&rp.getPeer().isSaltused()==false)
		{
			String email = rp.getUsername().toLowerCase();
			String pass = rp.getPasswordHash();
			try
			{
				EntityManagerFactory factory;
				factory = Persistence.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);
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
			    	rp.getPeer().setSaltused(true);
			    	ClientUpDater.updatepeer(rp.getPeer());	
			    	//TODO add userconfig
			    	AUTH ap = new AUTH();
			    	ap.setSessionID(rp.getPeer().getSession().getId());
			    	ap.setPeer(rp.getPeer());
			    	ap.setUID(rp.getUID());
			    	ap.setUserconfig(null);
			    	ap.send();
			    }
			    else
			    {
			    	new NACK(rp.getUID(), rp.getPeer()).send();
			    }
			}catch(NoResultException e)
			{
				new NACK(rp.getUID(), rp.getPeer()).send();
				return;
			}catch(Exception e)
			{
				System.err.println(e.toString());
			}
		}
	}
	
	public static void Logout(LOGOUT rp)
	{
		if(rp.getSessionID().equals(rp.getPeer().getSession().getId()))
		{
			//TODO remove sys out if checkt and handle different codes
			System.out.println("logg out"+rp.getSessionID());
			rp.getPeer().setLoginverified(false);
			rp.getPeer().setUser(null);
			rp.getPeer().setSalt(null);
			ClientUpDater.updatepeer(rp.getPeer());	
		}
	}

	public static void relog(RELOG rp)
	{
		try{
		if(ClientUpDater.getpeer(rp.getSessionID()).isLoginverified())
		{
			System.out.println("reauth");
			//TODO add user config
			new REAUTH(rp.getUID(), rp.getPeer(),rp.getPeer().getSession().getId(),null).send();
		}
		else
		{		
			System.out.println("nack");
			new NACK(rp.getUID(), rp.getPeer()).send();
		}
		}
		catch(Exception e)
		{
			System.out.println(e.toString());
			System.out.println("nack");
			new NACK(rp.getUID(), rp.getPeer()).send();
		}
		
	}
}
