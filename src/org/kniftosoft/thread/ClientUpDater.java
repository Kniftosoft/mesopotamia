/**
 * 
 */
package org.kniftosoft.thread;

import javax.websocket.Session;

import org.kniftosoft.endpoint.MesopotamiaEndpoint;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class ClientUpDater extends Thread {

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	private boolean connected = false;
	Session peer;
	
	/**
	 * 
	 * @param peer Session which will be updated by the Thread
	 */
	public ClientUpDater(Session peer )
	{
		this.peer = peer;
		System.out.println("Create Thread");
		connected = true;
	}
	
	
	@Override
	public void run() {
		
		while(connected) {
	          int sleepTime = (int)(1000);
	          try {
	        	System.out.println("update" +Thread.currentThread().getName());
		        JsonObject jo= new JsonObject();
		        jo.addProperty("tee", Thread.currentThread().getName());
				Thread.currentThread();
				Thread.sleep(sleepTime);
				MesopotamiaEndpoint.send(jo, peer);
			} catch (InterruptedException e) {
				connected = false;
				interrupt();
			}
		}
	}
}