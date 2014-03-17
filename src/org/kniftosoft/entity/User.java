package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the user database table.
 * 
 */
@Entity
@NamedQuery(name="User.findAll", query="SELECT u FROM User u")
public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="USER_ID")
	private int userId;

	@Column(name="ACC_TYPE")
	private int accType;

	@Column(name="CONFIG_ID")
	private int configId;

	private String email;

	private String nachname;

	private String ort;

	private String password;

	private String plz;

	private String strasse;

	private String vorname;

	//bi-directional many-to-one association to Session
	@OneToMany(mappedBy="userBean", fetch=FetchType.EAGER)
	private List<Session> sessions;

	//bi-directional many-to-one association to Subscribe
	@OneToMany(mappedBy="userBean", fetch=FetchType.EAGER)
	private List<Subscribe> subscribes;

	//bi-directional many-to-one association to Useraccess
	@OneToMany(mappedBy="userBean", fetch=FetchType.EAGER)
	private List<Useraccess> useraccesses;

	//bi-directional many-to-one association to Userconfig
	@OneToMany(mappedBy="userBean", fetch=FetchType.EAGER)
	private List<Userconfig> userconfigs;

	public User() {
	}

	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getAccType() {
		return this.accType;
	}

	public void setAccType(int accType) {
		this.accType = accType;
	}

	public int getConfigId() {
		return this.configId;
	}

	public void setConfigId(int configId) {
		this.configId = configId;
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

	public List<Session> getSessions() {
		return this.sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public Session addSession(Session session) {
		getSessions().add(session);
		session.setUserBean(this);

		return session;
	}

	public Session removeSession(Session session) {
		getSessions().remove(session);
		session.setUserBean(null);

		return session;
	}

	public List<Subscribe> getSubscribes() {
		return this.subscribes;
	}

	public void setSubscribes(List<Subscribe> subscribes) {
		this.subscribes = subscribes;
	}

	public Subscribe addSubscribe(Subscribe subscribe) {
		getSubscribes().add(subscribe);
		subscribe.setUserBean(this);

		return subscribe;
	}

	public Subscribe removeSubscribe(Subscribe subscribe) {
		getSubscribes().remove(subscribe);
		subscribe.setUserBean(null);

		return subscribe;
	}

	public List<Useraccess> getUseraccesses() {
		return this.useraccesses;
	}

	public void setUseraccesses(List<Useraccess> useraccesses) {
		this.useraccesses = useraccesses;
	}

	public Useraccess addUseraccess(Useraccess useraccess) {
		getUseraccesses().add(useraccess);
		useraccess.setUserBean(this);

		return useraccess;
	}

	public Useraccess removeUseraccess(Useraccess useraccess) {
		getUseraccesses().remove(useraccess);
		useraccess.setUserBean(null);

		return useraccess;
	}

	public List<Userconfig> getUserconfigs() {
		return this.userconfigs;
	}

	public void setUserconfigs(List<Userconfig> userconfigs) {
		this.userconfigs = userconfigs;
	}

	public Userconfig addUserconfig(Userconfig userconfig) {
		getUserconfigs().add(userconfig);
		userconfig.setUserBean(this);

		return userconfig;
	}

	public Userconfig removeUserconfig(Userconfig userconfig) {
		getUserconfigs().remove(userconfig);
		userconfig.setUserBean(null);

		return userconfig;
	}

}