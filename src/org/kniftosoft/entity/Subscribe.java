package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the subscribe database table.
 * 
 */
@Entity
@NamedQuery(name="Subscribe.findAll", query="SELECT s FROM Subscribe s")
public class Subscribe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idSubscribe;

	private int objektID;

	//bi-directional many-to-one association to App
	@ManyToOne
	@JoinColumn(name="app")
	private App appBean;

	//bi-directional many-to-one association to User
	@ManyToOne
	@JoinColumn(name="user")
	private User userBean;

	public Subscribe() {
	}

	public int getIdSubscribe() {
		return this.idSubscribe;
	}

	public void setIdSubscribe(int idSubscribe) {
		this.idSubscribe = idSubscribe;
	}

	public int getObjektID() {
		return this.objektID;
	}

	public void setObjektID(int objektID) {
		this.objektID = objektID;
	}

	public App getAppBean() {
		return this.appBean;
	}

	public void setAppBean(App appBean) {
		this.appBean = appBean;
	}

	public User getUserBean() {
		return this.userBean;
	}

	public void setUserBean(User userBean) {
		this.userBean = userBean;
	}

}