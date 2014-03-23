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
	
	Logbot logbot;

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		logbot.interrupt();
		Constants.factory.close();
	}

	/* (non-Javadoc)
	 * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		logbot = new Logbot();
		logbot.setDaemon(true);
		logbot.setName("Update Thread");
		logbot.start();
		Constants.factory = Persistence
				.createEntityManagerFactory(Constants.PERSISTENCE_UNIT_NAME);

	}

}
