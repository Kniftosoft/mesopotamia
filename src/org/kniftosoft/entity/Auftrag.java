package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the auftrag database table.
 * 
 */
@Entity
@NamedQuery(name="Auftrag.findAll", query="SELECT a FROM Auftrag a")
public class Auftrag implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idauftrag;

	private int groesse;

	@Temporal(TemporalType.TIMESTAMP)
	private Date startzeit;

	//bi-directional many-to-one association to Produkt
	@ManyToOne
	@JoinColumn(name="produkt")
	private Produkt produktBean;

	//bi-directional many-to-one association to Log
	@OneToMany(mappedBy="auftragBean")
	private List<Log> logs;

	public Auftrag() {
	}

	public int getIdauftrag() {
		return this.idauftrag;
	}

	public void setIdauftrag(int idauftrag) {
		this.idauftrag = idauftrag;
	}

	public int getGroesse() {
		return this.groesse;
	}

	public void setGroesse(int groesse) {
		this.groesse = groesse;
	}

	public Date getStartzeit() {
		return this.startzeit;
	}

	public void setStartzeit(Date startzeit) {
		this.startzeit = startzeit;
	}

	public Produkt getProduktBean() {
		return this.produktBean;
	}

	public void setProduktBean(Produkt produktBean) {
		this.produktBean = produktBean;
	}

	public List<Log> getLogs() {
		return this.logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public Log addLog(Log log) {
		getLogs().add(log);
		log.setAuftragBean(this);

		return log;
	}

	public Log removeLog(Log log) {
		getLogs().remove(log);
		log.setAuftragBean(null);

		return log;
	}

}