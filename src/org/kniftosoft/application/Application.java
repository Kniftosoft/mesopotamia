/**
 * 
 */
package org.kniftosoft.application;

import org.kniftosoft.entity.Subscribe;

import com.google.gson.JsonArray;

/**
 * @author julian
 *
 */
public abstract  class Application {

	//TODO think about somethin intelligent about user rights
	public abstract JsonArray getdata(Subscribe sub);
	/**
	 * 
	 */
	public Application() {
		// TODO Auto-generated constructor stub
	}
}
