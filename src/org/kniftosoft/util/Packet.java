/**
 * 
 */
package org.kniftosoft.util;

import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * @author julian
 *
 */
public class Packet  {
		protected String uid;
		protected EuphratisSession peer;
		protected JsonObject data = new JsonObject();
		protected String typeID;
		/**
		 * @return the typeID
		 */
		public String getTypeID() {
			return typeID;
		}

		/**
		 * @param typeID the typeID to set
		 */
		public void setTypeID(String typeID) {
			this.typeID = typeID;
		}

		/**
		 * @return the uid
		 */
		public String getUid() {
			return uid;
		}

		/**
		 * @param uid the uid to set
		 */
		public void setUid(String uid) {
			this.uid = uid;
		}

		/**
		 * @return the peer
		 */
		public EuphratisSession getPeer() {
			return peer;
		}

		/**
		 * @param es the peer to set
		 */
		public void setPeer(EuphratisSession peer) {
			this.peer = peer;
		}

		/**
		 * @return the data
		 */
		public JsonObject getData() {
			return data;
		}

		/**
		 * @param data the data to set
		 */
		public void setData(JsonObject data) {
			this.data = data;
		}
		
		
		public void addDataProperty(String property, String value) {
			this.data.addProperty(property, value);
		}
		public void addDataProperty(String property, Number value) {
			this.data.addProperty(property, value);
		}
		public void addDataProperty(String property, Boolean value) {
			this.data.addProperty(property, value);
		}
		public void addDataProperty(String property, Character value) {
			this.data.addProperty(property, value);
		}






		public Packet(String message, EuphratisSession peer) {
			this.peer = peer;
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
		public Packet(String typeID, String uid,EuphratisSession peer)
		{
			this.typeID = typeID;
			this.uid = uid;
			this.peer = peer;
		}
		
		public Packet(String typeID, String uid,JsonObject data,EuphratisSession peer)
		{
			this.typeID = typeID;
			this.uid = uid;
			this.peer = peer;
			this.data = data;
		}
}
