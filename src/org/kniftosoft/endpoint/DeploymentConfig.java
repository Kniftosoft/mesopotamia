/**
 * 
 */
package org.kniftosoft.endpoint;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.kniftosoft.thread.ClientUpDater;

/**
 * @author julian
 *
 */
public class DeploymentConfig implements ServletContextListener {
	ClientUpDater updater;
	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		updater.interrupt();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		updater = new ClientUpDater();
		updater.setDaemon(true);
		updater.setName("Update Thread");
		updater.start();
		

	}

}
