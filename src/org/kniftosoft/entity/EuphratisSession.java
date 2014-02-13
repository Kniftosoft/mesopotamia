package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the euphratis_session database table.
 * 
 */
@Entity
@Table(name="euphratis_session")
@NamedQuery(name="EuphratisSession.findAll", query="SELECT e FROM EuphratisSession e")
public class EuphratisSession implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int session_ID;

	@Column(nullable=false)
	private Boolean login_verified;

	@Column(nullable=false, length=45)
	private String peer_ID;

	//bi-directional one-to-one association to User
	@JoinColumn(name="User_ID", nullable=true)
	private User user;

	public EuphratisSession() {
	}

	public int getSession_ID() {
		return this.session_ID;
	}

	public void setSession_ID(int session_ID) {
		this.session_ID = session_ID;
	}

	public Boolean getLogin_verified() {
		return this.login_verified;
	}

	public void setLogin_verified(Boolean login_verified) {
		this.login_verified = login_verified;
	}

	public String getPeer_ID() {
		return this.peer_ID;
	}

	public void setPeer_ID(String peer_ID) {
		this.peer_ID = peer_ID;
	}

	public User getUser() {
		return this.user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}