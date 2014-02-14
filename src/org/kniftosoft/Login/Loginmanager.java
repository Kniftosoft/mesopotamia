package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.websocket.Session;

import org.kniftosoft.entity.User;

public class Loginmanager {
	static final String PERSISTENCE_UNIT_NAME = "Euphratis";
	public static void login(Session peer, String user, String pass){
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin(); 
	    TypedQuery<User> query =em.createQuery("Select user FROM User u WHERE u.email = '"+peer.getId()+"'", User.class);
		query.setMaxResults(1);
	    em.getTransaction().commit();
	    User userdb = query.getSingleResult();
	    if(user == userdb.getEmail()&&pass == userdb.getPassword()){
	    	System.out.println("hello "+userdb.getVorname());
	    }
		em.close();
	}
}
