package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the subscribe database table.
 * 
 */
@Entity
@NamedQuery(name="Subscribe.findAll", query="SELECT s FROM Subscribe s")
public class Subscribe implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idSubscribe;

	//bi-directional many-to-one association to Subbedmaschine
	@OneToMany(mappedBy="subscribeBean")
	private List<Subbedmaschine> subbedmaschines;

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

	public List<Subbedmaschine> getSubbedmaschines() {
		return this.subbedmaschines;
	}

	public void setSubbedmaschines(List<Subbedmaschine> subbedmaschines) {
		this.subbedmaschines = subbedmaschines;
	}

	public Subbedmaschine addSubbedmaschine(Subbedmaschine subbedmaschine) {
		getSubbedmaschines().add(subbedmaschine);
		subbedmaschine.setSubscribeBean(this);

		return subbedmaschine;
	}

	public Subbedmaschine removeSubbedmaschine(Subbedmaschine subbedmaschine) {
		getSubbedmaschines().remove(subbedmaschine);
		subbedmaschine.setSubscribeBean(null);

		return subbedmaschine;
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