/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.endpoint.MesopotamiaEndpoint;
import org.kniftosoft.entity.EuphratisSession;
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
		if(clientVersion.equals(Constants.getClientversion()))
		{
			ACCEPT ap = new ACCEPT(uid, peer);
			MesopotamiaEndpoint.send(ap);
		}
		else
		{
			ERROR ap = new ERROR(uid, peer,4,"Connection Refused because of a wrong Client Version");
			MesopotamiaEndpoint.send(ap);
			ClientUpDater.removepeer(peer);
		}
	}
	/**
	 * @param message
	 * @param peer
	 */
	public HANDSHAKE(int uid, EuphratisSession peer, String clientVersion) {
		System.out.println("doing hs");
		this.uid = uid;
		this.peer = peer;
		this.clientVersion = clientVersion;
	}
	@Override
	public void createFromJSON(JsonObject o) {
		clientVersion = o.get("clientVersion").getAsString();
		
	}
	@Override
	protected JsonObject storeData() {
		JsonObject data = new JsonObject();
		data.addProperty("clientVersion", clientVersion);
		return data;
	}
	@Override
	public PacketType getType() {
		return PacketType.HANDSHAKE;
	}

}
