/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.Constants;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class HANDSHAKE extends Packet {

	private String clientVersion;
	@Override
	public void executerequest()
	{
		System.out.println("hp");
		if(clientVersion.equals(Constants.Clientversion))
		{
			new ACCEPT(uid, peer).send();
		}
		else
		{
			new ERROR(uid, peer,5,"Connection Refused because of a wrong Client Version").send();
			ClientUpDater.removepeer(peer);
		}
	}

	@Override
	public void createFromJSON(JsonObject o) {
		clientVersion = o.get("clientVersion").getAsString();
		
	}
	@Override
	public JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("clientVersion", clientVersion);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.HANDSHAKE;
	}

}
