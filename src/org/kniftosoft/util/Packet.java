/**
 * 
 */
package org.kniftosoft.util;

import com.google.gson.JsonObject;


/**
 * @author julian
 *
 */
public class Packet  {

		private JsonObject packet = new JsonObject();

		/**
		 * 
		 * @return returns the packet
		 */
		public JsonObject getPacket() {
			return packet;
		}
		/**
		 * 
		 */
		public void setuid(String uid)
		{
			this.packet.addProperty("uid", uid);
		}
		/**
		 * @param typeID 
		 * @param data 
		 * @param packet
		 */
		public Packet(int typeID, JsonObject data) {
			
			this.packet.addProperty("typeID", typeID);
			this.packet.addProperty("data", data.toString());

		}

		
}
