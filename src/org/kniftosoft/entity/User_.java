package org.kniftosoft.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-02-13T17:07:46.991+0100")
@StaticMetamodel(User.class)
public class User_ {
	public static volatile SingularAttribute<User, Integer> user_ID;
	public static volatile SingularAttribute<User, Integer> acc_type;
	public static volatile SingularAttribute<User, Integer> config_ID;
	public static volatile SingularAttribute<User, String> email;
	public static volatile SingularAttribute<User, String> nachname;
	public static volatile SingularAttribute<User, String> ort;
	public static volatile SingularAttribute<User, String> password;
	public static volatile SingularAttribute<User, String> plz;
	public static volatile SingularAttribute<User, String> strasse;
	public static volatile SingularAttribute<User, String> vorname;
	public static volatile SingularAttribute<User, EuphratisSession> euphratisSession;
}
