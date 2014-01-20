package org.kniftosoft.Endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.datamodel.entity.Test;

import com.google.gson.JsonObject;

@ServerEndpoint(value = "/MESOEND")

public class Endpoint {
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnMessage
	public String onMessage(String message)
	{
		try
		{
		final String PERSISTENCE_UNIT_NAME = "mesopotamia";
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
		System.out.println("new Message: "+message);
		 em.getTransaction().begin();
		 
		    Test todo = new Test();
		    todo.setValue(message);
		    em.persist(todo);
		    em.getTransaction().commit();

		    Test test1 = em.find(Test.class , 12);
		    
		   
		   System.err.println("Found"+test1.toString());
		   
		   
		   Query q = em.createQuery("select t from Test t where t.value = 'Hello Socket'");
		    List<Test> todoList = q.getResultList();
		    for (Test tes : todoList) {
		      System.out.println("found this :"+tes);
		    }
		    em.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	@OnOpen
	public void onOpen (Session peer)
	{
		peers.add(peer);
		try {
			peer.getBasicRemote().sendText("Hallo1");
		} catch (IOException e) {
			e.printStackTrace();
		}
		peer.getAsyncRemote().sendText("hallo2");
		System.out.println("New Peer: "+peer.getId());
	}
	@OnClose
	public void onClose (Session peer)
	{
		System.out.println("Delete Peer: "+peer.getId());
		peers.remove(peer);
	}
	public void send(JsonObject json, Session peer){
		try
		{
			peer.getBasicRemote().sendText(json.toString());
		}catch(IOException e){
			System.out.println("Failed to send message to peer: "+ peer.getId()+" JSON MEssage: "+json.toString()+" IOExeption: "+ e.toString());
			e.printStackTrace();
		}
	}
	

}
