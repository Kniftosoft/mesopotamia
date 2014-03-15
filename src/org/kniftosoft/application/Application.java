/**
 * 
 */
package org.kniftosoft.application;

import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.entity.User;

import com.google.gson.JsonArray;

/**
 * @author julian
 *
 */
public abstract  class Application {

	//TODO think about somethin intelligent about user rights
	public abstract JsonArray getdata(Subscribe sub);
	
	public abstract JsonArray getdata(User user, String ident);
	/**
	 * 
	 */
	public Application() {
		// TODO Auto-generated constructor stub
	}
}
