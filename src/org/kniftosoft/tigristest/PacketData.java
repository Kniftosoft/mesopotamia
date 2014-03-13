package org.kniftosoft.tigristest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class PacketData extends Packet
{

	@Override
	public void createFromJSON(JsonObject o)
	{
		

	}

	@Override
	public JsonObject storeToJSON()
	{
		JsonObject jo = new JsonObject();
		JsonArray machines = new JsonArray();
		
		BufferedReader in = new BufferedReader(new InputStreamReader((PacketData.class.getResourceAsStream("exampleMachines.csv"))));
		
		try
		{
			String line;
			
			while((line = in.readLine()) != null)
			{
				if(line.startsWith("#") || line.isEmpty())
				{
					continue;
				}
				
				String[] set = line.split(";");
				
				int id = Integer.parseInt(set[0]);
				String name = set[1];
				int job = Integer.parseInt(set[2]);
				double speed = Double.parseDouble(set[3]) + (Math.random() * 30);
				int status = Integer.parseInt(set[4]);
				
				Machine m = new Machine(id,name,job,speed,status);
				machines.add(m.toJson());
			}
			
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			
			System.out.println(e.toString());
		}
		
		jo.addProperty("category", 1); //MACHINE
		jo.add("result", machines);
		
		return jo;
	}

	@Override
	public PacketType getType()
	{
		return PacketType.DATA;
	}

}
