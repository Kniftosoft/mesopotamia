package org.kniftosoft.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-01-29T10:32:10.203+0100")
@StaticMetamodel(Maschine.class)
public class Maschine_ {
	public static volatile SingularAttribute<Maschine, Integer> ID;
	public static volatile SingularAttribute<Maschine, String> Name;
	public static volatile SingularAttribute<Maschine, String> Standort;
	public static volatile SingularAttribute<Maschine, Integer> Auftragsnr;
	public static volatile SingularAttribute<Maschine, Integer> Auftragsgrosse;
	public static volatile SingularAttribute<Maschine, Integer> Zustand;
}
