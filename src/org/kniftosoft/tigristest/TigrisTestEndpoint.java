package org.kniftosoft.tigristest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

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
				resp.setSessionID(EXAMPLE_SESSION);
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
			}else
			{
				PacketNack resp = new PacketNack();
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
			}
		}else if(packet instanceof PacketRelog)
		{
			PacketRelog pk = (PacketRelog) packet;
			
			if(pk.getSessionID().equals(EXAMPLE_SESSION))
			{
				PacketReauth resp = new PacketReauth();
				resp.setUID(pk.getUID());
				
				resp.setSessionID(EXAMPLE_SESSION);
				resp.setUsername(EXAMPLE_USER);
				
				peer.getAsyncRemote().sendObject(resp);
			}else
			{
				PacketNack resp = new PacketNack();
				resp.setUID(pk.getUID());
				
				peer.getAsyncRemote().sendObject(resp);
			}
		}else if(packet instanceof PacketLogout)
		{
			PacketLogout pk = (PacketLogout) packet;
			
			System.out.println("Peer logged out. Reason: " + pk.getReasonCode());
			
			PacketAck resp = new PacketAck();
			resp.setUID(pk.getUID());
			
			peer.getAsyncRemote().sendObject(resp);
			
		}else if(packet instanceof PacketQuery)
		{
			PacketQuery pk = (PacketQuery) packet;
			
			PacketData resp = new PacketData();
			resp.setUID(packet.getUID());
			
			JsonArray result = new JsonArray();
			
			if(pk.getCategory() == 1)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader((PacketData.class.getResourceAsStream("exampleMachines.csv"))));
				
				try
				{
					String line;
					
					while((line = in.readLine()) != null)
					{
						if(line.startsWith("#") || line.isEmpty())
						{
							continue;
						}
						
						String[] set = line.split(";");
						
						int id = Integer.parseInt(set[0]);
						String name = set[1];
						int job = Integer.parseInt(set[2]);
						double speed = Double.parseDouble(set[3]) + (Math.random() * 30);
						int status = Integer.parseInt(set[4]);
						
						Machine m = new Machine(id,name,job,speed,status);
						result.add(m.toJson());
					}
					
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					System.out.println(e.toString());
				}
			}else if(pk.getCategory() == 2) //jobs
			{
				
				BufferedReader in = new BufferedReader(new InputStreamReader((PacketData.class.getResourceAsStream("exampleJobs.csv"))));
				
				try
				{
					String line;
					
					while((line = in.readLine()) != null)
					{
						if(line.startsWith("#") || line.isEmpty())
						{
							continue;
						}
						
						String[] set = line.split(";");
						
						JsonObject jobob = new JsonObject();
						
						jobob.addProperty("id",Integer.parseInt(set[0]));
						jobob.addProperty("target",Integer.parseInt(set[1]));
						jobob.addProperty("startTime",Long.parseLong(set[2]));
						jobob.addProperty("productType",Integer.parseInt(set[3]));
						
						result.add(jobob);
					}
					
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
					
					System.out.println(e.toString());
				}
				
				
			}else if(pk.getCategory() == 11) //Product
			{
				JsonObject o = new JsonObject();
				
				o.addProperty("id", 1);
				o.addProperty("name", "M14 screws");
				
				result.add(o);
			}else if(pk.getCategory() == 20) //Config
			{
				JsonObject o = new JsonObject();
				
				JsonArray ja = new JsonArray();
				ja.add(new JsonPrimitive("{\"id\":0,\"category\":1,\"column\":0}"));
				ja.add(new JsonPrimitive("{\"id\":1,\"category\":1,\"column\":1}"));
				ja.add(new JsonPrimitive("{\"id\":2,\"category\":1,\"column\":2}"));
				
				o.addProperty("id", 4);
				o.add("value", ja);
				
				
				result.add(o);
			}
				
			resp.setResult(result);
			resp.setCategory(pk.getCategory());
			peer.getAsyncRemote().sendObject(resp);
		}else if(packet.getType() == PacketType.SUBSCRIBE)
		{
			PacketAck resp = new PacketAck();
			resp.setUID(packet.getUID());
			
			peer.getAsyncRemote().sendObject(resp);
			
		}else if(packet.getType() == PacketType.CONFIG)
		{
			PacketAck resp = new PacketAck();
			resp.setUID(packet.getUID());
			
			peer.getAsyncRemote().sendObject(resp);
			
		}else if(packet.getType() == PacketType.ERROR)
		{
			//Ack
			
		}else
		{
			PacketError resp = new PacketError();
			resp.setUID(packet.getUID());
			resp.setErrorType(ErrorType.INVALID_PACKET);
			resp.setErrorMessage("The sent packet (" + packet.getType().name() + ") was not recognized.");
			
			peer.getAsyncRemote().sendObject(resp);
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
	
	public static final String EUPHRATES_VERSION = "0.3.8";
	
	public static final String EXAMPLE_USER = "otto";
	public static final String EXAMPLE_PASSWORD_HASH = "c3ab8ff13720e8ad9047dd39466b3c8974e592c2fa383d4a3960714caef0c4f2"; //hash of "foobar"
	public static final String EXAMPLE_SALT = "IamDaveYognautAndIhaveTheBalls";
	public static final String EXAMPLE_SESSION = "sessionWooOhMyGodILoveChiptune";
}
