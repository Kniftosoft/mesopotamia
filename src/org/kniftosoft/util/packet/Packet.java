/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;


/**
 * @author julian
 *
 */
public class Packet  {
		protected int uid;
		protected EuphratisSession peer;
		protected JsonObject data = new JsonObject();
		protected int typeID;
		/**
		 * @return the typeID
		 */
		public int getTypeID() {
			return typeID;
		}

		/**
		 * @param typeID the typeID to set
		 */
		public void setTypeID(int typeID) {
			this.typeID = typeID;
		}

		/**
		 * @return the uid
		 */
		public int getUid() {
			return uid;
		}

		/**
		 * @param uid the uid to set
		 */
		public void setUid(int uid) {
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
			System.out.println("Packet Construktor");
			try
			{
				JsonParser parser = new JsonParser();
				JsonObject jmessage = (JsonObject) parser.parse(message);
				if(jmessage.has("typeID")&&jmessage.has("data")&&jmessage.has("uid"))
				{
					this.typeID = jmessage.get("typeID").getAsInt();
					this.uid = jmessage.get("uid").getAsInt();
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
		public Packet(int typeID,int uid, EuphratisSession peer)
		{
			this.typeID = typeID;
			this.uid = uid;
			this.peer = peer;
		}
		
		public Packet(int uid,JsonObject data,EuphratisSession peer)
		{
			this.uid = uid;
			this.peer = peer;
			this.data = data;
		}
}
