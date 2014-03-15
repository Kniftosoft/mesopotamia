package org.kniftosoft.endpoint;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.util.EuphratisSession;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.packet.Packet;

@ServerEndpoint(value = "/TIG_TEST_END",
configurator=Mesoendconfigurator.class,
encoders = {PacketEncoder.class},
decoders = {PacketDecoder.class})
/**
 * 
 * @author julian
 *
 */
public class MesopotamiaEndpoint {
	/**
	 * 
	 * @param packet Received message from client
	 * @param peer Client who sends message
	 */
	@OnMessage
	public void onMessage(Packet packet,Session peer)
	{
		packet.setPeer(ClientUpDater.getpeer(peer));
		//TODO remove before publishing
		System.out.println("recived Packet:"+packet.toString());
		packet.executerequest();
			
		
	}
	/**
	 * 
	 * @param peer adds the new session to the session set and starts a updating Thread
	 */
	@OnOpen
	public void onOpen (Session peer)
	{
		System.out.println("new peer: "+peer);
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
		System.out.println("remove peer: "+peer);
		try
		{
			EuphratisSession es = new EuphratisSession(peer);
			ClientUpDater.removepeer(es);
		}catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

}
