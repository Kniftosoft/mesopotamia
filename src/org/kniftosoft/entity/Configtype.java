package org.kniftosoft.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the configtypes database table.
 * 
 */
@Entity
@Table(name="configtypes")
@NamedQuery(name="Configtype.findAll", query="SELECT c FROM Configtype c")
public class Configtype implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int idConfigtypes;

	private String description;

	private String name;

	//bi-directional many-to-one association to Userconfig
	@OneToMany(mappedBy="configtype", fetch=FetchType.EAGER)
	private List<Userconfig> userconfigs;

	public Configtype() {
	}

	public int getIdConfigtypes() {
		return this.idConfigtypes;
	}

	public void setIdConfigtypes(int idConfigtypes) {
		this.idConfigtypes = idConfigtypes;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Userconfig> getUserconfigs() {
		return this.userconfigs;
	}

	public void setUserconfigs(List<Userconfig> userconfigs) {
		this.userconfigs = userconfigs;
	}

	public Userconfig addUserconfig(Userconfig userconfig) {
		getUserconfigs().add(userconfig);
		userconfig.setConfigtype(this);

		return userconfig;
	}

	public Userconfig removeUserconfig(Userconfig userconfig) {
		getUserconfigs().remove(userconfig);
		userconfig.setConfigtype(null);

		return userconfig;
	}

}