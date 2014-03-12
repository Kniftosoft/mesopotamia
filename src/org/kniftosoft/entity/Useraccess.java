package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the useraccess database table.
 * 
 */
@Entity
@NamedQuery(name="Useraccess.findAll", query="SELECT u FROM Useraccess u")
public class Useraccess implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int iduseraccess;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user")
	private User userBean;

	//bi-directional many-to-one association to Maschine
	@ManyToOne
	@JoinColumn(name="maschine")
	private Maschine maschineBean;

	public Useraccess() {
	}

	public int getIduseraccess() {
		return this.iduseraccess;
	}

	public void setIduseraccess(int iduseraccess) {
		this.iduseraccess = iduseraccess;
	}

	public User getUserBean() {
		return this.userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

	public Maschine getMaschineBean() {
		return this.maschineBean;
	}

	public void setMaschineBean(Maschine maschineBean) {
		this.maschineBean = maschineBean;
	}

}