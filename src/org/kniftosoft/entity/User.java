package org.kniftosoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@NamedQuery(name = "User.findAll", query = "SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "USER_ID")
	private int userId;

	@Column(name = "ACC_TYPE")
	private int accType;

	@Column(name = "CONFIG_ID")
	private int configId;

	private String email;

	private String nachname;

	private String ort;

	private String password;

	private String plz;

	private String strasse;

	private String vorname;

	// bi-directional many-to-one association to Session
	@OneToMany(mappedBy = "userBean")
	private List<Session> sessions;

	// bi-directional many-to-one association to Subscribe
	@OneToMany(mappedBy = "userBean")
	private List<Subscribe> subscribes;

	// bi-directional many-to-one association to Useraccess
	@OneToMany(mappedBy = "userBean")
	private List<Useraccess> useraccesses;

	// bi-directional many-to-one association to Userconfig
	@OneToMany(mappedBy = "userBean")
	private List<Userconfig> userconfigs;

	public User() {
	}

	public Session addSession(Session session) {
		getSessions().add(session);
		session.setUserBean(this);

		return session;
	}

	public Subscribe addSubscribe(Subscribe subscribe) {
		getSubscribes().add(subscribe);
		subscribe.setUserBean(this);

		return subscribe;
	}

	public Useraccess addUseraccess(Useraccess useraccess) {
		getUseraccesses().add(useraccess);
		useraccess.setUserBean(this);

		return useraccess;
	}

	public Userconfig addUserconfig(Userconfig userconfig) {
		getUserconfigs().add(userconfig);
		userconfig.setUserBean(this);

		return userconfig;
	}

	public int getAccType() {
		return accType;
	}

	public int getConfigId() {
		return configId;
	}

	public String getEmail() {
		return email;
	}

	public String getNachname() {
		return nachname;
	}

	public String getOrt() {
		return ort;
	}

	public String getPassword() {
		return password;
	}

	public String getPlz() {
		return plz;
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public String getStrasse() {
		return strasse;
	}

	public List<Subscribe> getSubscribes() {
		return subscribes;
	}

	public List<Useraccess> getUseraccesses() {
		return useraccesses;
	}

	public List<Userconfig> getUserconfigs() {
		return userconfigs;
	}

	public int getUserId() {
		return userId;
	}

	public String getVorname() {
		return vorname;
	}

	public Session removeSession(Session session) {
		getSessions().remove(session);
		session.setUserBean(null);

		return session;
	}

	public Subscribe removeSubscribe(Subscribe subscribe) {
		getSubscribes().remove(subscribe);
		subscribe.setUserBean(null);

		return subscribe;
	}

	public Useraccess removeUseraccess(Useraccess useraccess) {
		getUseraccesses().remove(useraccess);
		useraccess.setUserBean(null);

		return useraccess;
	}

	public Userconfig removeUserconfig(Userconfig userconfig) {
		getUserconfigs().remove(userconfig);
		userconfig.setUserBean(null);

		return userconfig;
	}

	public void setAccType(int accType) {
		this.accType = accType;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setNachname(String nachname) {
		this.nachname = nachname;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public void setSubscribes(List<Subscribe> subscribes) {
		this.subscribes = subscribes;
	}

	public void setUseraccesses(List<Useraccess> useraccesses) {
		this.useraccesses = useraccesses;
	}

	public void setUserconfigs(List<Userconfig> userconfigs) {
		this.userconfigs = userconfigs;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setVorname(String vorname) {
		this.vorname = vorname;
	}

}