package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@Table(name="user")
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(unique=true, nullable=false)
	private int user_ID;

	@Column(nullable=false)
	private int acc_type;

	private int config_ID;

	@Column(nullable=false, length=45)
	private String email;

	@Column(length=45)
	private String nachname;

	@Column(length=30)
	private String ort;

	@Column(nullable=false, length=45)
	private String password;

	@Column(length=7)
	private String plz;

	@Column(length=45)
	private String strasse;

	@Column(length=45)
	private String vorname;

	//bi-directional one-to-one association to EuphratisSession
	@OneToOne(mappedBy="user")
	private EuphratisSession euphratisSession;

	public User() {
	}

	public int getUser_ID() {
		return this.user_ID;
	}

	public void setUser_ID(int user_ID) {
		this.user_ID = user_ID;
	}

	public int getAcc_type() {
		return this.acc_type;
	}

	public void setAcc_type(int acc_type) {
		this.acc_type = acc_type;
	}

	public int getConfig_ID() {
		return this.config_ID;
	}

	public void setConfig_ID(int config_ID) {
		this.config_ID = config_ID;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getNachname() {
		return this.nachname;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public String getOrt() {
		return this.ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPlz() {
		return this.plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return this.strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getVorname() {
		return this.vorname;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

	public EuphratisSession getEuphratisSession() {
		return this.euphratisSession;
	}

	public void setEuphratisSession(EuphratisSession euphratisSession) {
		this.euphratisSession = euphratisSession;
	}

}