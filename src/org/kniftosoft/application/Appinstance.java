/**
 * 
 */
package org.kniftosoft.application;

import javax.websocket.DecodeException;

import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.EuphratisSession;
import org.kniftosoft.util.packet.DATA;

/**
 * @author julian
 *
 */
public class Appinstance {
	
	Application app;
	Subscribe sub;
	EuphratisSession peer;
	public void update()
	{
		DATA update = new DATA();
		update.setResult(app.getdata(sub));
		update.setPeer(peer);
		update.setUID(0);
		update.send();
	}
	private void getapp() throws DecodeException
	{
		 ApplicationType apptype = ApplicationType.byID(sub.getAppBean().getIdapp());
		 try
		 {

			 app = (Application) apptype.getAppClass().newInstance();
		 }catch (InstantiationException e) 
		 {
			throw new DecodeException("Could not instantiate Application class of Application type " , apptype.name());
			
		 }catch (IllegalAccessException e) 
		 {
			throw new DecodeException("Could not instantiate Application class of Application type " , apptype.name());
		 }

	}
	
	public Appinstance(Subscribe sub, EuphratisSession peer)
	{
		this.peer = peer;
		this.sub = sub;
		try
		{
			getapp();
		}catch(DecodeException e)
		{
			System.out.println(e.toString());
		}	
	}

	
}
