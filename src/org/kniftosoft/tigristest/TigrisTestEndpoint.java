package org.kniftosoft.tigristest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
				PacketAccept resp = new PacketAccept();
				resp.setUID(pk.getUID());
				resp.setSalt(EXAMPLE_SALT);
				
				peer.getAsyncRemote().sendObject(resp);
				
			}else
			{
				//Client is using too old or too new version -> throw him out
				PacketError resp = new PacketError();
				resp.setErrorType(ErrorType.WRONG_VERSION);
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
			}
			
		}else if(packet instanceof PacketLogin)
		{
			PacketLogin pk = (PacketLogin) packet;
			
			String exampleHash = "";
			
			try 
			{
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				
				//We act like the are only password hashes in the database, as suggested by MCP 1.2.1
				md.update((EXAMPLE_PASSWORD_HASH + EXAMPLE_SALT).getBytes());
				
				exampleHash = bytesToHex(md.digest());
				
			}catch (NoSuchAlgorithmException e) 
			{
				
				
				e.printStackTrace();
			}
			
			if(pk.getUsername().equals(EXAMPLE_USER) && pk.getPasswordHash().toLowerCase().equals(exampleHash))
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
	
	
	final protected static char[] hexArray = "0123456789abcdef".toCharArray();
	public static String bytesToHex(byte[] bytes)
	{
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) 
	    {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static final String EUPHRATES_VERSION = "0.2.0";
	
	public static final String EXAMPLE_USER = "otto";
	public static final String EXAMPLE_PASSWORD_HASH = "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"; //hash of "foobar"
	public static final String EXAMPLE_SALT = "IamDaveYognautAndIhaveTheBalls";
}
