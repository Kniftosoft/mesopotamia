package org.kniftosoft.Login;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.websocket.Session;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.entity.User;

public class Loginmanager {
	static final String PERSISTENCE_UNIT_NAME = "Euphratis";
	public static void login(Session peer, String email, String pass){
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
	    em.getTransaction().begin();
	    System.out.println("11111111111111111");
	    TypedQuery<User> userquery=em.createQuery("Select u FROM User u WHERE u.email = '"+email+"'", User.class).setMaxResults(1);
	    em.getTransaction().commit();
	    User user= userquery.getSingleResult();
	    if(email.equals(user.getEmail())&&pass.equals(user.getPassword())){
	    	System.out.println("hello "+user.getVorname());
	    	//TypedQuery<EuphratisSession> sessionquery =em.createQuery("SELECT es FROM EuphratisSession es WHERE es.peer_ID = '"+peer.getId()+"'", EuphratisSession.class).setMaxResults(1);
	    	//em.getTransaction().commit();
	    	//EuphratisSession session = sessionquery.getSingleResult();
	    	em.createQuery("UPDATE EuphratisSession es SET es.login_verified = 1 , es.user = "+user+" WHERE es.peer_ID = '"+peer.getId()+"'").executeUpdate();
	    	System.out.println("hello 2");
	    	
	    }
		em.close();
	}
}
