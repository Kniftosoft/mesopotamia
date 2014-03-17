package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the userconfig database table.
 * 
 */
@Entity
@NamedQuery(name="Userconfig.findAll", query="SELECT u FROM Userconfig u")
public class Userconfig implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int iduserconfig;

	private String value;

	//bi-directional many-to-one association to Configtype
	@ManyToOne
	@JoinColumn(name="config")
	private Configtype configtype;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user")
	private User userBean;

	public Userconfig() {
	}

	public int getIduserconfig() {
		return this.iduserconfig;
	}

	public void setIduserconfig(int iduserconfig) {
		this.iduserconfig = iduserconfig;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Configtype getConfigtype() {
		return this.configtype;
	}

	public void setConfigtype(Configtype configtype) {
		this.configtype = configtype;
	}

	public User getUserBean() {
		return this.userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

}