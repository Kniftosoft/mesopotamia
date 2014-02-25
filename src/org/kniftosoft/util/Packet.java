/**
 * 
 */
package org.kniftosoft.util;

import javax.websocket.Session;

import org.kniftosoft.endpoint.MethodProvider;
import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * @author julian
 *
 */
public class Packet  {
		private String typeID;
		private String uid;
		private EuphratisSession es;
		private JsonObject data = new JsonObject();

		/**
		 * @param typeID 
		 * @param data 
		 * @param data
		 */
		void executerequest()
		{
			switch(typeID)
			{	
			
			
			case "1":
						if(data.get("clientVersion").getAsString().equals(Constants.getClientversion()))
						{
							typeID = "200";
							data = null;
							break;
						}
						else
						{
							// TODO A Refused Connection is not removed from sessions
							typeID = "255";
							data = new JsonObject();
							data.addProperty("reasonCode", 4);
							data.addProperty("reasonMessage", "Connection Refused because of a wrong Client Version");
							break;
						}
			break;
			
			
			case "10": MethodProvider.login(jmessage.getAsJsonObject("data"),es);
			break;
			case "12": MethodProvider.relog(jmessage.getAsJsonObject("data"),es);
			break;
			case "14": MethodProvider.logout(jmessage.getAsJsonObject("data"),es);
			break;
			case "20": MethodProvider.query(jmessage.getAsJsonObject("data"),es);
			break;
			case "200": MethodProvider.ack(jmessage.getAsJsonObject("data"),es);
			break;
			case "201": MethodProvider.nack(jmessage.getAsJsonObject("data"),es);
			break;
			case "242": MethodProvider.error(jmessage.getAsJsonObject("data"),es);
			break;
			case "255": MethodProvider.quit(jmessage.getAsJsonObject("data"),es);
				break;
			default : MethodProvider._default(jmessage.getAsJsonObject("data"),es);
					 System.out.println("Keine Gültige Methode  Json-String: "+message);
				break;
			}
		}
		public Packet(String message, Session peer) {
			es = new EuphratisSession(peer);
			try
			{
				JsonParser parser = new JsonParser();
				JsonObject jmessage = (JsonObject) parser.parse(message);
				if(jmessage.has("typeID")&&jmessage.has("data")&&jmessage.has("uid"))
				{
					this.typeID = jmessage.get("typeID").getAsString();
					this.uid = jmessage.get("uid").getAsString();
					this.data = jmessage.getAsJsonObject("data");
				}
				else
				{
					System.out.println("No Valid JSON");
				}				
			}		
			catch(JsonSyntaxException e)
			{
				System.out.println("Could not parse message to Json /n JsonSyntaxException :/n"+e.toString());
			}			

		}	
}
