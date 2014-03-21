package org.kniftosoft.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the auftrag database table.
 * 
 */
@Entity
@NamedQuery(name = "Auftrag.findAll", query = "SELECT a FROM Auftrag a")
public class Auftrag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idauftrag;

	private int groesse;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startzeit;

	// bi-directional many-to-one association to Produkt
	@ManyToOne
	@JoinColumn(name = "produkt")
	private Produkt produktBean;

	// bi-directional many-to-one association to Log
	@OneToMany(mappedBy = "auftragBean")
	private List<Log> logs;

	public Auftrag() {
	}

	public Log addLog(Log log) {
		getLogs().add(log);
		log.setAuftragBean(this);

		return log;
	}

	public int getGroesse() {
		return groesse;
	}

	public int getIdauftrag() {
		return idauftrag;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public Produkt getProduktBean() {
		return produktBean;
	}

	public Date getStartzeit() {
		return startzeit;
	}

	public Log removeLog(Log log) {
		getLogs().remove(log);
		log.setAuftragBean(null);

		return log;
	}

	public void setGroesse(int groesse) {
		this.groesse = groesse;
	}

	public void setIdauftrag(int idauftrag) {
		this.idauftrag = idauftrag;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public void setProduktBean(Produkt produktBean) {
		this.produktBean = produktBean;
	}

	public void setStartzeit(Date startzeit) {
		this.startzeit = startzeit;
	}

}