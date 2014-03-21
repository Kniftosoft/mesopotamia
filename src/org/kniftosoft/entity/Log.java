package org.kniftosoft.entity;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

import org.kniftosoft.update.Databaselistener;

/**
 * The persistent class for the log database table.
 * 
 */
@Entity
@EntityListeners(Databaselistener.class)
@NamedQuery(name = "Log.findAll", query = "SELECT l FROM Log l")
public class Log implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idlog;

	private int produziert;

	private Timestamp timestamp;

	// bi-directional many-to-one association to Auftrag
	@ManyToOne
	@JoinColumn(name = "auftrag")
	private Auftrag auftragBean;

	// bi-directional many-to-one association to Maschine
	@ManyToOne
	@JoinColumn(name = "maschine")
	private Maschine maschineBean;

	// bi-directional many-to-one association to Zustand
	@ManyToOne
	@JoinColumn(name = "zustand")
	private Zustand zustandBean;

	public Log() {
	}

	public Auftrag getAuftragBean() {
		return auftragBean;
	}

	public int getIdlog() {
		return idlog;
	}

	public Maschine getMaschineBean() {
		return maschineBean;
	}

	public int getProduziert() {
		return produziert;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public Zustand getZustandBean() {
		return zustandBean;
	}

	public void setAuftragBean(Auftrag auftragBean) {
		this.auftragBean = auftragBean;
	}

	public void setIdlog(int idlog) {
		this.idlog = idlog;
	}

	public void setMaschineBean(Maschine maschineBean) {
		this.maschineBean = maschineBean;
	}

	public void setProduziert(int produziert) {
		this.produziert = produziert;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public void setZustandBean(Zustand zustandBean) {
		this.zustandBean = zustandBean;
	}

}