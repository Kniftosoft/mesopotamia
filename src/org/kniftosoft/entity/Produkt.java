package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the produkt database table.
 * 
 */
@Entity
@NamedQuery(name="Produkt.findAll", query="SELECT p FROM Produkt p")
public class Produkt implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idprodukt;

	private String beschreibung;

	//bi-directional many-to-one association to Auftrag
	@OneToMany(mappedBy="produktBean", fetch=FetchType.EAGER)
	private List<Auftrag> auftrags;

	public Produkt() {
	}

	public int getIdprodukt() {
		return this.idprodukt;
	}

	public void setIdprodukt(int idprodukt) {
		this.idprodukt = idprodukt;
	}

	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public List<Auftrag> getAuftrags() {
		return this.auftrags;
	}

	public void setAuftrags(List<Auftrag> auftrags) {
		this.auftrags = auftrags;
	}

	public Auftrag addAuftrag(Auftrag auftrag) {
		getAuftrags().add(auftrag);
		auftrag.setProduktBean(this);

		return auftrag;
	}

	public Auftrag removeAuftrag(Auftrag auftrag) {
		getAuftrags().remove(auftrag);
		auftrag.setProduktBean(null);

		return auftrag;
	}

}