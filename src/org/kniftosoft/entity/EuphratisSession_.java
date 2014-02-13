package org.kniftosoft.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-02-13T17:42:01.063+0100")
@StaticMetamodel(EuphratisSession.class)
public class EuphratisSession_ {
	public static volatile SingularAttribute<EuphratisSession, Integer> session_ID;
	public static volatile SingularAttribute<EuphratisSession, Boolean> login_verified;
	public static volatile SingularAttribute<EuphratisSession, String> peer_ID;
	public static volatile SingularAttribute<EuphratisSession, User> user;
}
