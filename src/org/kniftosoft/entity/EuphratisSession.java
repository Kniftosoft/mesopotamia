package org.kniftosoft.entity;

import javax.websocket.Session;

public class EuphratisSession {

	private Session session;
	private User user;
	private String salt;
	private boolean loginverified;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "EuphratisSession ["
				+ (session != null ? "session=" + session + ", " : "")
				+ (user != null ? "user=" + user + ", " : "")
				+ "loginverified=" + loginverified + "]";
	}
	/**
	 * @return the session
	 */
	public Session getSession() {
		return session;
	}
	/**
	 * @param session the session to set
	 */
	public void setSession(Session session) {
		this.session = session;
	}
	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}
	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}
	public String getSalt() {
		return salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}
	/**
	 * @return the loginverified
	 */
	public boolean isLoginverified() {
		return loginverified;
	}
	/**
	 * @param loginverified the loginverified to set
	 */
	public void setLoginverified(boolean loginverified) {
		this.loginverified = loginverified;
	}
	/**
	 * @param session
	 */
	public EuphratisSession(Session session) {
		super();
		this.session = session;
	}
}
