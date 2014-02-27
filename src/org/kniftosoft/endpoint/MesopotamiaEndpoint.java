package org.kniftosoft.endpoint;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.packet.recived.HANDSHAKEPackage;
import org.kniftosoft.util.packet.recived.RecivedPacket;

@ServerEndpoint(value = "/TIG_TEST_END",configurator=Mesoendconfigurator.class)
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
		//TODO remove before publishing
		System.out.println("recive:"+message);
		EuphratisSession es = ClientUpDater.getpeer(peer);
		//RecivedPacket packet = new RecivedPacket(message, es);
		//packet.executerequest();
		RecivedPacket hp = new RecivedPacket(message, es);
		hp.executerequest();
			
		
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
			es.setSalt(Long.toHexString(Double.doubleToLongBits(Math.random())));
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

}
