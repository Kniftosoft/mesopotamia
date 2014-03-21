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
 * The persistent class for the userconfig database table.
 * 
 */
@Entity
@NamedQuery(name = "Userconfig.findAll", query = "SELECT u FROM Userconfig u")
public class Userconfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int iduserconfig;

	private String value;

	// bi-directional many-to-one association to Configtype
	@ManyToOne
	@JoinColumn(name = "config")
	private Configtype configtype;

	// bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name = "user")
	private User userBean;

	public Userconfig() {
	}

	public Configtype getConfigtype() {
		return configtype;
	}

	public int getIduserconfig() {
		return iduserconfig;
	}

	public User getUserBean() {
		return userBean;
	}

	public String getValue() {
		return value;
	}

	public void setConfigtype(Configtype configtype) {
		this.configtype = configtype;
	}

	public void setIduserconfig(int iduserconfig) {
		this.iduserconfig = iduserconfig;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

	public void setValue(String value) {
		this.value = value;
	}

}