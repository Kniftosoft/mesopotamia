package org.kniftosoft.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the sessions database table.
 * 
 */
@Entity
@Table(name = "sessions")
@NamedQuery(name = "Session.findAll", query = "SELECT s FROM Session s")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idSessions;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "user")
	private User userBean;

	public Session() {
	}

	public int getIdSessions() {
		return idSessions;
	}

	public User getUserBean() {
		return userBean;
	}

	public void setIdSessions(int idSessions) {
		this.idSessions = idSessions;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

}