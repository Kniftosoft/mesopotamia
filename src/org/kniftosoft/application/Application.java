/**
 * 
 */
package org.kniftosoft.application;

import java.util.List;

import org.kniftosoft.entity.Maschine;
import org.kniftosoft.entity.Subscribe;

import com.google.gson.JsonArray;

/**
 * @author julian
 *
 */
public abstract  class Application {

	//TODO think about somethin intelligent about user rights
	public abstract JsonArray getdata(Subscribe sub);
	
	public abstract JsonArray getdata(List<Maschine> maschines);
	/**
	 * 
	 */
	public Application() {
		// TODO Auto-generated constructor stub
	}
}
