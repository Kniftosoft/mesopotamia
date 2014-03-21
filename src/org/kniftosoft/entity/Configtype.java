package org.kniftosoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the configtypes database table.
 * 
 */
@Entity
@Table(name = "configtypes")
@NamedQuery(name = "Configtype.findAll", query = "SELECT c FROM Configtype c")
public class Configtype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int idConfigtypes;

	private String description;

	private String name;

	// bi-directional many-to-one association to Userconfig
	@OneToMany(mappedBy = "configtype")
	private List<Userconfig> userconfigs;

	public Configtype() {
	}

	public Userconfig addUserconfig(Userconfig userconfig) {
		getUserconfigs().add(userconfig);
		userconfig.setConfigtype(this);

		return userconfig;
	}

	public String getDescription() {
		return description;
	}

	public int getIdConfigtypes() {
		return idConfigtypes;
	}

	public String getName() {
		return name;
	}

	public List<Userconfig> getUserconfigs() {
		return userconfigs;
	}

	public Userconfig removeUserconfig(Userconfig userconfig) {
		getUserconfigs().remove(userconfig);
		userconfig.setConfigtype(null);

		return userconfig;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setIdConfigtypes(int idConfigtypes) {
		this.idConfigtypes = idConfigtypes;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setUserconfigs(List<Userconfig> userconfigs) {
		this.userconfigs = userconfigs;
	}

}