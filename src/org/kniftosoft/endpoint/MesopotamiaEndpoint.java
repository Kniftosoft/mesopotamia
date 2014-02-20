package org.kniftosoft.endpoint;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.entity.EuphratisSession;
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
	/**
	 * 
	 * @param message Received message from client
	 * @param peer Client who sends message
	 */
	@OnMessage
	public void onMessage(String message,Session peer)
	{
		EuphratisSession es = new EuphratisSession(peer);
		System.out.println("recive:"+message);
		JsonObject answer = new JsonObject();
		try
		{
			try
			{
				JsonParser parser = new JsonParser();
				JsonObject jmessage = (JsonObject) parser.parse(message);
				if(jmessage.has("typeID")&&jmessage.has("data")&&jmessage.has("uid"))
				{
					switch(jmessage.getAsJsonPrimitive("typeID").getAsString())
					{				
					case "10": answer = MethodProvider.login(jmessage.getAsJsonObject("data"),es);
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
				answer.addProperty("uid", jmessage.get("uid").getAsString());
			}		
			catch(JsonSyntaxException e)
			{
				System.out.println("Could not parse message to Json /n JsonSyntaxException :/n"+e.toString());
			}
			
		}catch(Exception e){
			System.out.println("Unbekannter Fehler:/n"+e.toString());
		}
		
		send(answer, es);
	}
	/**
	 * 
	 * @param peer adds the new session to the session set and starts a updating Thread
	 */
	@OnOpen
	public void onOpen (Session peer)
	{
		try{
			EuphratisSession es = new EuphratisSession(peer);
			ClientUpDater.addpeer(es);
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
			EuphratisSession es = new EuphratisSession(peer);
			ClientUpDater.removepeer(es);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param json JSON Message which should send to the connected peer
	 * @param peer A connected Peer which should receive the message
	 */
	public static void send(JsonObject json, EuphratisSession peer){
		try
		{
			peer.getSession().getBasicRemote().sendText(json.toString());
		}catch(IOException e){
			System.out.println("Failed to send message to peer: "+ peer.getSession().getId()+" JSON MEssage: "+json.toString()+" IOExeption: "+ e.toString());
		}
	}
	

}
