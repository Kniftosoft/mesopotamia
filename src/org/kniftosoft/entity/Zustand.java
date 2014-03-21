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
 * The persistent class for the zustand database table.
 * 
 */
@Entity
@NamedQuery(name = "Zustand.findAll", query = "SELECT z FROM Zustand z")
public class Zustand implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idzustand;

	private String beschreibung;

	// bi-directional many-to-one association to Log
	@OneToMany(mappedBy = "zustandBean")
	private List<Log> logs;

	public Zustand() {
	}

	public Log addLog(Log log) {
		getLogs().add(log);
		log.setZustandBean(this);

		return log;
	}

	public String getBeschreibung() {
		return beschreibung;
	}

	public int getIdzustand() {
		return idzustand;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public Log removeLog(Log log) {
		getLogs().remove(log);
		log.setZustandBean(null);

		return log;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public void setIdzustand(int idzustand) {
		this.idzustand = idzustand;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

}