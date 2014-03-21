/**
 * 
 */
package org.kniftosoft.endpoint;

import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.kniftosoft.LogBot.Logbot;
import org.kniftosoft.util.Constants;

/**
 * @author julian
 * 
 */
public class DeploymentConfig implements ServletContextListener {
	
	Logbot updater;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		updater.interrupt();
		Constants.factory.close();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		updater = new Logbot();
		updater.setDaemon(true);
		updater.setName("Update Thread");
		updater.start();
		Constants.factory = Persistence
				.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);

	}

}
