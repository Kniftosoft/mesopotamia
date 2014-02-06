package org.kniftosoft.entity;

import java.io.Serializable;
import java.lang.String;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Machine
 *
 */
@Entity

public class Maschine implements Serializable {

	   
	@Id
	private int ID;
	private String Name;
	private String Standort;
	private int Auftragsnr;
	private int Auftragsgrosse;
	private int Zustand;
	private static final long serialVersionUID = 1L;

	public Maschine() {
		super();
	}   
	public int getID() {
		return this.ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}   
	public String getName() {
		return this.Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}   
	public String getStandort() {
		return this.Standort;
	}

	public void setStandort(String Standort) {
		this.Standort = Standort;
	}   
	public int getAuftragsnr() {
		return this.Auftragsnr;
	}

	public void setAuftragsnr(int Auftragsnr) {
		this.Auftragsnr = Auftragsnr;
	}   
	public int getAuftragsgrosse() {
		return this.Auftragsgrosse;
	}

	public void setAuftragsgrosse(int Auftragsgrosse) {
		this.Auftragsgrosse = Auftragsgrosse;
	}   
	public int getZustand() {
		return this.Zustand;
	}

	public void setZustand(int Zustand) {
		this.Zustand = Zustand;
	}
   
}
