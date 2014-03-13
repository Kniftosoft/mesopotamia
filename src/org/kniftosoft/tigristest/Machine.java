package org.kniftosoft.tigristest;

import com.google.gson.JsonObject;

public class Machine
{

	public Machine(int id, String name, int job, double speed, int status)
	{
		this.id = id;
		this.name = name;
		this.job = job;
		this.speed = speed;
		this.status = status;
	}
	
	public JsonObject toJson()
	{
		JsonObject jo = new JsonObject();
		
		jo.addProperty("id", id);
		jo.addProperty("name", name);
		jo.addProperty("job", job);
		jo.addProperty("speed", speed);
		jo.addProperty("status", status);
		
		return jo;
	}
	
	private int id;
	private String name;
	private int job;
	private double speed;
	private int status;
	
}
