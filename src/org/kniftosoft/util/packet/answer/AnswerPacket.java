/**
 * 
 */
package org.kniftosoft.util.packet.answer;

import java.io.IOException;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.util.packet.Packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class AnswerPacket extends Packet {

	
	public void send(){
		if(this.typeID != 0 && this.uid != 0 && this.peer != null)
		{
			try
			{
				peer.getSession().getBasicRemote().sendText(toJSON().toString());
			}catch(IOException e){
				System.out.println("Failed to send message to peer: "+ peer.getSession().getId()+" JSON MEssage: "+toJSON().toString()+" IOExeption: "+ e.toString());
			}
		}
		else
		{
			System.out.println("no valid answer:"+this.toString());
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
	 * @param uid
	 * @param peer
	 */
	public AnswerPacket(int typeID,int uid, EuphratisSession peer) {
		super(typeID,uid, peer);
	}

}
