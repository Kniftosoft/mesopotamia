package org.kniftosoft.endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.thread.ClientUpDater;

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
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	Thread updater;
	/**
	 * 
	 * @param message Received message from client
	 * @param peer Client who sends message
	 */
	@OnMessage
	public void onMessage(String message, Session peer)
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
		peers.add(peer);
		updater = new Thread(new ClientUpDater(peer), "UpdateThread"+peer.getId());
		updater.start();

	}
	/**
	 * 
	 * @param peer removes the session from the session set and stops the updating Thread
	 */
	@OnClose
	public void onClose (Session peer)
	{
		peers.remove(peer);
		updater.interrupt();
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
