package org.kniftosoft.endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

@ServerEndpoint(value = "/MESOEND",configurator=Mesoendconfigurator.class)
/**
 * 
 * @author julian
 *
 */
public class MesopotamiaEndpoint {
	final String PERSISTENCE_UNIT_NAME = "Euphratis";
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	Thread updater;
	/**
	 * 
	 * @param message Received message from client
	 * @param peer Client who sends message
	 */
	@OnMessage
	public void onMessage(String message,Session peer)
	{
		JsonObject answer = new JsonObject();
		answer.addProperty("data", message);
		try
		{
			try
			{
				JsonParser parser = new JsonParser();
				JsonObject jmessage = (JsonObject)parser.parse(message);
				if(jmessage.has("method")&&jmessage.has("data"))
				{
					switch(jmessage.getAsJsonPrimitive("method").getAsString())
					{				
					case "test1": answer = MethodProvider.test1(jmessage.getAsJsonObject("data"));
						break;
					default : answer = MethodProvider._default(jmessage.getAsJsonObject("data"));
							 System.out.println("Keine Gültige Methode  Json-String: "+message);
						break;
					}
				}
				else
				{
					System.out.println("No Valid JSON");
				}
			}
			catch(JsonSyntaxException e)
			{
				System.out.println("Could not parse message to Json /n JsonSyntaxException :/n"+e.toString());
			}
			
		}catch(Exception e){
			System.out.println("Unbekannter Fehler:/n"+e.toString());
		}
		send(answer, peer);
	}
	/**
	 * 
	 * @param peer adds the new session to the session set and starts a updating Thread
	 */
	@OnOpen
	public void onOpen (Session peer)
	{
		try{
			
		peers.add(peer);
		//updater = new Thread(new ClientUpDater(peer), "UpdateThread"+peer.getId());
		//updater.start();
		
		
		EntityManagerFactory factory;
		factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
	    EntityManager em = factory.createEntityManager();
		em.getTransaction().begin();
		 
		EuphratisSession es = new EuphratisSession();
		es.setLogin_verified(false);
		es.setPeer_ID(peer.getId());
		es.setUser(null);
		em.persist(es);
		em.getTransaction().commit();
		em.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param peer removes the session from the session set and stops the updating Thread
	 */
	@OnClose
	public void onClose (Session peer)
	{
		
		try
		{
			EntityManagerFactory factory;
			factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
		    EntityManager em = factory.createEntityManager();
		    em.getTransaction().begin(); 
		    em.createQuery("DELETE FROM EuphratisSession es WHERE es.peer_ID = '"+peer.getId()+"'", EuphratisSession.class).executeUpdate();
			em.getTransaction().commit();
			em.close();
			
		}catch(Exception e)
		{
			e.printStackTrace();
		}
		peers.remove(peer);
		//updater.interrupt();
	}
	
	/**
	 * 
	 * @param json JSON Message which should send to the connected peer
	 * @param peer A connected Peer which should receive the message
	 */
	public static void send(JsonObject json, Session peer){
		try
		{
			peer.getBasicRemote().sendText(json.toString());
		}catch(IOException e){
			System.out.println("Failed to send message to peer: "+ peer.getId()+" JSON MEssage: "+json.toString()+" IOExeption: "+ e.toString());
		}
	}
	

}
