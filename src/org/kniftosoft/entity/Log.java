package org.kniftosoft.entity;

import java.io.Serializable;

import javax.persistence.*;

import org.kniftosoft.update.Databaselistener;

import java.sql.Timestamp;


/**
 * The persistent class for the log database table.
 * 
 */
@Entity
@EntityListeners(Databaselistener.class)
@NamedQuery(name="Log.findAll", query="SELECT l FROM Log l")
public class Log implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int idlog;

	private int produziert;

	private Timestamp timestamp;

	//bi-directional many-to-one association to Auftrag
	@ManyToOne
	@JoinColumn(name="auftrag")
	private Auftrag auftragBean;

	//bi-directional many-to-one association to Maschine
	@ManyToOne
	@JoinColumn(name="maschine")
	private Maschine maschineBean;

	//bi-directional many-to-one association to Zustand
	@ManyToOne
	@JoinColumn(name="zustand")
	private Zustand zustandBean;

	public Log() {
	}

	public int getIdlog() {
		return this.idlog;
	}

	public void setIdlog(int idlog) {
		this.idlog = idlog;
	}

	public int getProduziert() {
		return this.produziert;
	}

	public void setProduziert(int produziert) {
		this.produziert = produziert;
	}

	public Timestamp getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public Auftrag getAuftragBean() {
		return this.auftragBean;
	}

	public void setAuftragBean(Auftrag auftragBean) {
		this.auftragBean = auftragBean;
	}

	public Maschine getMaschineBean() {
		return this.maschineBean;
	}

	public void setMaschineBean(Maschine maschineBean) {
		this.maschineBean = maschineBean;
	}

	public Zustand getZustandBean() {
		return this.zustandBean;
	}

	public void setZustandBean(Zustand zustandBean) {
		this.zustandBean = zustandBean;
	}

}