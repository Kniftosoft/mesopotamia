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
 * The persistent class for the maschine database table.
 * 
 */
@Entity
@NamedQuery(name = "Maschine.findAll", query = "SELECT m FROM Maschine m")
public class Maschine implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idmaschine;

	private String name;

	private String standort;

	// bi-directional many-to-one association to Log
	@OneToMany(mappedBy = "maschineBean")
	private List<Log> logs;

	// bi-directional many-to-one association to Useraccess
	@OneToMany(mappedBy = "maschineBean")
	private List<Useraccess> useraccesses;

	public Maschine() {
	}

	public Log addLog(Log log) {
		getLogs().add(log);
		log.setMaschineBean(this);

		return log;
	}

	public Useraccess addUseraccess(Useraccess useraccess) {
		getUseraccesses().add(useraccess);
		useraccess.setMaschineBean(this);

		return useraccess;
	}

	public int getIdmaschine() {
		return idmaschine;
	}

	public List<Log> getLogs() {
		return logs;
	}

	public String getName() {
		return name;
	}

	public String getStandort() {
		return standort;
	}

	public List<Useraccess> getUseraccesses() {
		return useraccesses;
	}

	public Log removeLog(Log log) {
		getLogs().remove(log);
		log.setMaschineBean(null);

		return log;
	}

	public Useraccess removeUseraccess(Useraccess useraccess) {
		getUseraccesses().remove(useraccess);
		useraccess.setMaschineBean(null);

		return useraccess;
	}

	public void setIdmaschine(int idmaschine) {
		this.idmaschine = idmaschine;
	}

	public void setLogs(List<Log> logs) {
		this.logs = logs;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setStandort(String standort) {
		this.standort = standort;
	}

	public void setUseraccesses(List<Useraccess> useraccesses) {
		this.useraccesses = useraccesses;
	}

}