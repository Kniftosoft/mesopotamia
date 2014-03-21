package org.kniftosoft.util.packet;

import org.kniftosoft.util.EuphratisSession;
import org.kniftosoft.util.UIDGen;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
@SuppressWarnings("deprecation")
public abstract class Packet {
	protected EuphratisSession peer;
	protected int typeID;
	protected int uid = UIDGen.instance().generateUID();

	/**
	 * @param data
	 */
	public abstract void createFromJSON(JsonObject data);

	/**
	 * execute the received request associated with the packet
	 */
	public abstract void executerequest();

	/**
	 * @return peer
	 */
	public EuphratisSession getPeer() {
		return peer;
	}

	/**
	 * @return PacketType 
	 */
	public abstract PacketType getType();

	/**
	 * @return uid
	 */
	public int getUID() {
		return uid;
	}

	/**
	 * sends the packet to the client
	 */
	public void send() {
		peer.getSession().getAsyncRemote().sendObject(this);
	}

	/**
	 * @param peer
	 */
	public void setPeer(EuphratisSession peer) {
		this.peer = peer;
	}

	/**
	 * @param uid
	 */
	public void setUID(int uid) {
		this.uid = uid;
	}

	/**
	 * returns the data fields of the Package stored in a JSONObject
	 * @return data 
	 */
	public abstract JsonObject storeData();

}