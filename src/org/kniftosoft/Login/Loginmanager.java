package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.entity.User;
import org.kniftosoft.thread.ClientUpDater;

public class Loginmanager {
	static final String PERSISTENCE_UNIT_NAME = "Euphratis";
	public static void login(EuphratisSession es, String email, String pass){
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
	    em.getTransaction().commit();
	    User user= userquery.getSingleResult();
	    if(email.toLowerCase().equals(user.getEmail().toLowerCase())&&pass.equals(user.getPassword())){
	    	System.out.println("hello "+user.getVorname());
	    	es.setLoginverified(true);
	    	es.setUser(user);
	    	ClientUpDater.updatepeer(es);
	    	System.out.println("hello 2");
	    	
	    }
		em.close();
	}
}
