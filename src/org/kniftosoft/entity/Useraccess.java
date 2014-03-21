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
 * The persistent class for the useraccess database table.
 * 
 */
@Entity
@NamedQuery(name = "Useraccess.findAll", query = "SELECT u FROM Useraccess u")
public class Useraccess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int iduseraccess;

	// bi-directional many-to-one association to Maschine
	@ManyToOne
	@JoinColumn(name = "maschine")
	private Maschine maschineBean;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "user")
	private User userBean;

	public Useraccess() {
	}

	public int getIduseraccess() {
		return iduseraccess;
	}

	public Maschine getMaschineBean() {
		return maschineBean;
	}

	public User getUserBean() {
		return userBean;
	}

	public void setIduseraccess(int iduseraccess) {
		this.iduseraccess = iduseraccess;
	}

	public void setMaschineBean(Maschine maschineBean) {
		this.maschineBean = maschineBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

}