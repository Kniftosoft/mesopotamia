package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.entity.User;
import org.kniftosoft.thread.ClientUpDater;

import com.google.gson.JsonObject;

public class Loginmanager {
	static final String PERSISTENCE_UNIT_NAME = "Euphratis";
	public static JsonObject login(EuphratisSession es, String email, String pass){
		JsonObject answer = new JsonObject();
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
	    em.getTransaction().commit();
	    User user= userquery.getSingleResult();
	    System.out.println(email.toLowerCase());
	    System.out.println(user.getEmail().toLowerCase());
	    System.out.println(pass);
	    System.out.println(user.getPassword());
	    if(email.toLowerCase().equals(user.getEmail().toLowerCase())&&pass.equals(user.getPassword())){
	    	es.setLoginverified(true);
	    	es.setUser(user);
	    	ClientUpDater.updatepeer(es);
	    	
	    	
	    	answer.addProperty("typeID", "11");
	    	JsonObject data = new JsonObject();
	    	data.addProperty("sessionID", es.getSession().getId());
	    	data.addProperty("Struct", "");
	    	answer.addProperty("data", data.toString());
	    }
	    else
	    {
	    	answer.addProperty("typeID", "201");
	    }
		em.close();
		return answer;
	}
}
