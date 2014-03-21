package org.kniftosoft.entity;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

/**
 * The persistent class for the subscribe database table.
 * 
 */
@Entity
@NamedQuery(name = "Subscribe.findAll", query = "SELECT s FROM Subscribe s")
public class Subscribe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idSubscribe;

	private int objektID;

	// bi-directional many-to-one association to App
	@ManyToOne
	@JoinColumn(name = "app")
	private App appBean;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "user")
	private User userBean;

	public Subscribe() {
	}

	public App getAppBean() {
		return appBean;
	}

	public int getIdSubscribe() {
		return idSubscribe;
	}

	public int getObjektID() {
		return objektID;
	}

	public User getUserBean() {
		return userBean;
	}

	public void setAppBean(App appBean) {
		this.appBean = appBean;
	}

	public void setIdSubscribe(int idSubscribe) {
		this.idSubscribe = idSubscribe;
	}

	public void setObjektID(int objektID) {
		this.objektID = objektID;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

	@Override
	public String toString() {
		return "Subscribe [idSubscribe=" + idSubscribe + ", objektID="
				+ objektID + ", appBean=" + appBean + ", userBean=" + userBean
				+ "]";
	}

}