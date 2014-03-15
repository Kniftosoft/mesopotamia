package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the subbedmaschine database table.
 * 
 */
@Entity
@NamedQuery(name="Subbedmaschine.findAll", query="SELECT s FROM Subbedmaschine s")
public class Subbedmaschine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idSubbedMaschine;

	//bi-directional many-to-one association to Maschine
	@ManyToOne
	@JoinColumn(name="Maschine")
	private Maschine maschineBean;

	//bi-directional many-to-one association to Subscribe
	@ManyToOne
	@JoinColumn(name="Subscribe")
	private Subscribe subscribeBean;

	public Subbedmaschine() {
	}

	public int getIdSubbedMaschine() {
		return this.idSubbedMaschine;
	}

	public void setIdSubbedMaschine(int idSubbedMaschine) {
		this.idSubbedMaschine = idSubbedMaschine;
	}

	public Maschine getMaschineBean() {
		return this.maschineBean;
	}

	public void setMaschineBean(Maschine maschineBean) {
		this.maschineBean = maschineBean;
	}

	public Subscribe getSubscribeBean() {
		return this.subscribeBean;
	}

	public void setSubscribeBean(Subscribe subscribeBean) {
		this.subscribeBean = subscribeBean;
	}

}