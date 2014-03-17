package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the sessions database table.
 * 
 */
@Entity
@Table(name="sessions")
@NamedQuery(name="Session.findAll", query="SELECT s FROM Session s")
public class Session implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idSessions;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user")
	private User userBean;

	public Session() {
	}

	public int getIdSessions() {
		return this.idSessions;
	}

	public void setIdSessions(int idSessions) {
		this.idSessions = idSessions;
	}

	public User getUserBean() {
		return this.userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

}