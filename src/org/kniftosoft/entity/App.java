package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the app database table.
 * 
 */
@Entity
@NamedQuery(name="App.findAll", query="SELECT a FROM App a")
public class App implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idapp;

	private String beschreibung;

	//bi-directional many-to-one association to Subscribe
	@OneToMany(mappedBy="appBean", fetch=FetchType.EAGER)
	private List<Subscribe> subscribes;

	public App() {
	}

	public int getIdapp() {
		return this.idapp;
	}

	public void setIdapp(int idapp) {
		this.idapp = idapp;
	}

	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public List<Subscribe> getSubscribes() {
		return this.subscribes;
	}

	public void setSubscribes(List<Subscribe> subscribes) {
		this.subscribes = subscribes;
	}

	public Subscribe addSubscribe(Subscribe subscribe) {
		getSubscribes().add(subscribe);
		subscribe.setAppBean(this);

		return subscribe;
	}

	public Subscribe removeSubscribe(Subscribe subscribe) {
		getSubscribes().remove(subscribe);
		subscribe.setAppBean(null);

		return subscribe;
	}

}