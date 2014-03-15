package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the zustand database table.
 * 
 */
@Entity
@NamedQuery(name="Zustand.findAll", query="SELECT z FROM Zustand z")
public class Zustand implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int idzustand;

	private String beschreibung;

	//bi-directional many-to-one association to Log
	@OneToMany(mappedBy="zustandBean")
	private List<Log> logs;

	public Zustand() {
	}

	public int getIdzustand() {
		return this.idzustand;
	}

	public void setIdzustand(int idzustand) {
		this.idzustand = idzustand;
	}

	public String getBeschreibung() {
		return this.beschreibung;
	}

	public void setBeschreibung(String beschreibung) {
		this.beschreibung = beschreibung;
	}

	public List<Log> getLogs() {
		return this.logs;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public Log addLog(Log log) {
		getLogs().add(log);
		log.setZustandBean(this);

		return log;
	}

	public Log removeLog(Log log) {
		getLogs().remove(log);
		log.setZustandBean(null);

		return log;
	}

}