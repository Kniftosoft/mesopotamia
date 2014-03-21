package org.kniftosoft.application;

import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.entity.User;

import com.google.gson.JsonArray;

/**
 * @author julian
 * 
 */
public abstract class Application {

	// TODO think about somethin intelligent about user rights
	/**
	 * get all data for the subscribe
	 * @param sub
	 * @return datas
	 */
	public abstract JsonArray getdata(Subscribe sub);

	/**
	 * get all data for the user and the id
	 * @param user
	 * @param id
	 * @return datas
	 */
	public abstract JsonArray getdata(User user, String id);

	/**
	 * returns the id of the application
	 * @return id
	 */
	public abstract int getid();

}
