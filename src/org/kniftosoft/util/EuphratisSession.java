package org.kniftosoft.util;

import javax.websocket.Session;

import org.kniftosoft.entity.User;

/**
 * @author julian
 * 
 */
public class EuphratisSession {

	private Session session;
	private User user;
	private String salt;
	private boolean saltused;
	private boolean loginverified;

	/**
	 * @param session
	 */
	public EuphratisSession(Session session) {
		super();
		this.session = session;
	}

	/**
	 * @return salt
	 */
	public String getSalt() {
		return salt;
	}

	/**
	 * @return session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * @return user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @return loginverified
	 */
	public boolean isLoginverified() {
		return loginverified;
	}

	/**
	 * @return saltused
	 */
	public boolean isSaltused() {
		return saltused;
	}

	/**
	 * @param loginverified
	 */
	public void setLoginverified(boolean loginverified) {
		this.loginverified = loginverified;
	}

	/**
	 * @param salt
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * @param saltused
	 */
	public void setSaltused(boolean saltused) {
		this.saltused = saltused;
	}

	/**
	 * @param session
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
}