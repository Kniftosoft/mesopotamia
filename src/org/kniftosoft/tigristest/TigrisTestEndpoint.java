package org.kniftosoft.tigristest;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(
		value = "/TIG_TEST_END",
		encoders = {PacketEncoder.class},
		decoders = {PacketDecoder.class})
public class TigrisTestEndpoint 
{

	@OnOpen
	public void onOpen (Session peer)
	{
		System.out.println("Peer connected.");
	}
	
	@OnClose
	public void onClose (Session peer)
	{
		System.out.println("Peer disconnected.");
	}
	
	@OnMessage
	public void onMessage(Packet packet, Session peer)
	{
		System.out.println("Got packet: " + packet.getType().name());
		
		if(packet instanceof PacketHandshake)
		{
			PacketHandshake pk = (PacketHandshake) packet;
			
			if(EUPHRATES_VERSION.equals(pk.getClientVersion()))
			{
				//Client has the correct version -> allow him to connect
				PacketAck resp = new PacketAck();
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
				
			}else
			{
				//Client is using too old or too new version -> throw him out
				PacketQuit resp = new PacketQuit();
				resp.setUID(pk.getUID());
				resp.setReasonMessage("Client version(" + pk.getClientVersion() + ") does not match server version(" + EUPHRATES_VERSION + ")");
				resp.setReasonCode(42);
				
				peer.getAsyncRemote().sendObject(resp);
			}
			
		}else if(packet instanceof PacketLogin)
		{
			PacketLogin pk = (PacketLogin) packet;
			
			if(pk.getUsername().equals(EXAMPLE_USER) && pk.getPasswordHash().equals(EXAMPLE_PASSWORD_HASH))
			{
				PacketAuth resp = new PacketAuth();
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
			}else
			{
				PacketNack resp = new PacketNack();
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
			}
		}
	}
	
	public static final String EUPHRATES_VERSION = "0.0.4";
	
	public static final String EXAMPLE_USER = "heinz";
	public static final String EXAMPLE_PASSWORD_HASH = "f0e4c2f76c58916ec258f246851bea091d14d4247a2fc3e18694461b1816e13b";
}
