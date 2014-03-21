package org.kniftosoft.util.packet;

import org.kniftosoft.util.Constants;
import org.kniftosoft.util.ErrorType;
import org.kniftosoft.util.Peerholder;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class HANDSHAKE extends Packet {

	private String clientVersion;


	/* (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		clientVersion = o.get("clientVersion").getAsString();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		if (clientVersion.equals(Constants.Clientversion)) {
			peer.setSalt(Long.toHexString(Double.doubleToLongBits(Math.random())));
			peer.setSaltused(false);
			Peerholder.updatepeer(peer);
			final ACCEPT accept = new ACCEPT();
			accept.setPeer(peer);
			accept.setUID(uid);
			accept.setSalt(peer.getSalt());
			accept.send();
		} else {

			final ERROR er = new ERROR();
			er.setPeer(peer);
			er.setUID(uid);
			System.out.print(er.getUID());
			er.setError(ErrorType.WRONG_VERSION);
			er.send();
			Peerholder.removepeer(peer);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.HANDSHAKE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("clientVersion", clientVersion);
		return data;
	}

}
