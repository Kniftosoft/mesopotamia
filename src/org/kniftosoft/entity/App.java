package org.kniftosoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * The persistent class for the app database table.
 * 
 */
@Entity
@NamedQuery(name = "App.findAll", query = "SELECT a FROM App a")
public class App implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idapp;

	private String beschreibung;

	// bi-directional many-to-one association to Subscribe
	@OneToMany(mappedBy = "appBean")
	private List<Subscribe> subscribes;

	public App() {
	}

	public Subscribe addSubscribe(Subscribe subscribe) {
		getSubscribes().add(subscribe);
		subscribe.setAppBean(this);

		return subscribe;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public int getIdapp() {
		return idapp;
	}

	public List<Subscribe> getSubscribes() {
		return subscribes;
	}

	public Subscribe removeSubscribe(Subscribe subscribe) {
		getSubscribes().remove(subscribe);
		subscribe.setAppBean(null);

		return subscribe;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public void setIdapp(int idapp) {
		this.idapp = idapp;
	}

	public void setSubscribes(List<Subscribe> subscribes) {
		this.subscribes = subscribes;
	}

}