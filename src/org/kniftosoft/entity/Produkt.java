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
 * The persistent class for the produkt database table.
 * 
 */
@Entity
@NamedQuery(name = "Produkt.findAll", query = "SELECT p FROM Produkt p")
public class Produkt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idprodukt;

	private String beschreibung;

	// bi-directional many-to-one association to Auftrag
	@OneToMany(mappedBy = "produktBean")
	private List<Auftrag> auftrags;

	public Produkt() {
	}

	public Auftrag addAuftrag(Auftrag auftrag) {
		getAuftrags().add(auftrag);
		auftrag.setProduktBean(this);

		return auftrag;
	}

	public List<Auftrag> getAuftrags() {
		return auftrags;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public int getIdprodukt() {
		return idprodukt;
	}

	public Auftrag removeAuftrag(Auftrag auftrag) {
		getAuftrags().remove(auftrag);
		auftrag.setProduktBean(null);

		return auftrag;
	}

	public void setAuftrags(List<Auftrag> auftrags) {
		this.auftrags = auftrags;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public void setIdprodukt(int idprodukt) {
		this.idprodukt = idprodukt;
	}

}