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
		Packet answer;
		
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(Constants.getPersistenceUnitName());
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
	    em.getTransaction().commit();
	    User user= userquery.getSingleResult();
	    
	    if(email.toLowerCase().equals(user.getEmail().toLowerCase())&&pass.equals(user.getPassword())){
	    	es.setLoginverified(true);
	    	es.setUser(user);
	    	ClientUpDater.updatepeer(es);	
	    	
	    	JsonObject data = new JsonObject();
	    	data.addProperty("sessionID", es.getSession().getId());
	    	data.addProperty("Struct", "");
	    	answer = new Packet(11, data);
	    }
	    else
	    {
	    	JsonObject data = new JsonObject();
	    	answer = new Packet(201, data);
	    }
		em.close();
		return answer;
	}
}
