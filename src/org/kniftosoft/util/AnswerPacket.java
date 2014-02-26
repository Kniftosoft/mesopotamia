/**
 * 
 */
package org.kniftosoft.util;

import java.io.IOException;

import org.kniftosoft.entity.EuphratisSession;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class AnswerPacket extends Packet {

	
	public void send(){
		if(this.typeID != 0 && this.uid != null && this.peer != null)
		{
			try
			{
				peer.getSession().getBasicRemote().sendText(toJSON().getAsString());
			}catch(IOException e){
				System.out.println("Failed to send message to peer: "+ peer.getSession().getId()+" JSON MEssage: "+toJSON().toString()+" IOExeption: "+ e.toString());
			}
		}
		else
		{
			
		}
		
	}
	private JsonObject toJSON()
	{
		JsonObject json = new JsonObject();
		json.addProperty("typeID", typeID);
		json.addProperty("uid", uid);
		json.add("data", data);
		return json;
		
	}
	/**
	 * @param message
	 * @param peer
	 */
	public AnswerPacket(String message, EuphratisSession peer) {
		super(message, peer);
	}
	/**
	 * @param typeID
	 * @param uid
	 * @param peer
	 */
	public AnswerPacket(int typeID, String uid, EuphratisSession peer) {
		super(typeID, uid, peer);
	}
	/**
	 * @param typeID
	 * @param uid
	 * @param data
	 * @param peer
	 */
	public AnswerPacket(int typeID, String uid, JsonObject data,EuphratisSession peer) {
		super(typeID, uid, data, peer);
	}

}
