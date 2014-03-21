package org.kniftosoft.LogBot;

/**
 * Thread Calls the LOGGen periodical
 * 
 * @author julian
 * 
 */
public class Logbot extends Thread {
	int sleepTime = 10000;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		try {
			Thread.currentThread();
			Thread.sleep(sleepTime);
			while (true) {
				new LogGen().genlogs();
				Thread.currentThread();
				Thread.sleep(sleepTime);

			}
		} catch (final InterruptedException e) {

		}
	}

}