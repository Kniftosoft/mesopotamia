/**
 * 
 */
package org.kniftosoft.application;

import javax.websocket.DecodeException;

import org.kniftosoft.entity.Subscribe;

/**
 * @author julian
 *
 */
public class Appinstance {
	
	Application app;
	Subscribe sub;
	public void update()
	{
		
	}
	private void getapp() throws DecodeException
	{
		 ApplicationType apptype = ApplicationType.byID(sub.getAppBean().getIdapp());
		 try
		 {

			 app = (Application) apptype.getAppClass().newInstance();
		 }catch (InstantiationException e) 
		 {
			System.out.println("error: "+e.toString());
			throw new DecodeException("Could not instantiate Application class of Application type " , apptype.name());
			
		 }catch (IllegalAccessException e) 
		 {
			System.out.println("error: "+e.toString());
			throw new DecodeException("Could not instantiate Application class of Application type " , apptype.name());
		 }

	}
	
	public Appinstance(Subscribe sub)
	{
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
