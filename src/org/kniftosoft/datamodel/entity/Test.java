package org.kniftosoft.datamodel.entity;

import java.io.Serializable;

import javax.persistence.*;


/**
 * The persistent class for the test database table.
 * 
 */
@Entity
@NamedQuery(name="Test.findAll", query="SELECT t FROM Test t")
public class Test implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer idTest = null;

	private String value;

	public Test() {
	}

	public int getIdTest() {
		return this.idTest;
	}

	public void setIdTest(int idTest) {
		this.idTest = idTest;
	}

	public String getValue() {
		return this.value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	@Override
	public String toString(){
		return "ID = " +idTest+" Value = "+value;
	}

}