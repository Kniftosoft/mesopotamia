package org.kniftosoft.Endpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.json.Json;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/MESOEND")

public class Endpoint {
	private static Set<Session> peers = Collections.synchronizedSet(new HashSet<Session>());
	
	@OnMessage
	public String onMessage(String message)
	{
		System.out.println("new Message: "+message);
		return null;
	}
	@OnOpen
	public void onOpen (Session peer)
	{
		peers.add(peer);
		try {
			peer.getBasicRemote().sendText("Hallo1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
	public void send(Json json, Session peer){
		try
		{
			peer.getBasicRemote().sendText(json.toString());
		}catch(IOException e){
			System.out.println("Failed to send message to peer: "+ peer.getId()+" JSON MEssage: "+json.toString()+" IOExeption: "+ e.toString());
			e.printStackTrace();
		}
	}
	

}
