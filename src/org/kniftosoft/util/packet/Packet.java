/**
 * 
 */
package org.kniftosoft.util.packet;

import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.util.UIDGen;

import com.google.gson.JsonObject;


/**
 * @author julian
 *
 */
public abstract class Packet 
{
	protected int typeID;
	protected int uid = UIDGen.instance().generateUID();
	protected EuphratisSession peer;
	
	public abstract void  executerequest();
	public abstract void createFromJSON(JsonObject o);
	public abstract PacketType getType();
	public abstract JsonObject storeData();
	
	public void send()
	{
		peer.getSession().getAsyncRemote().sendObject(this);
	}
	
	public void setPeer(EuphratisSession peer)
	{
		this.peer = peer;
	}
	
	public EuphratisSession getPeer()
	{
		return peer;
	}
		
	public int getUID()
	{
		return uid;
	}
	
	public void setUID(int uid)
	{
		this.uid = uid; 
	}
	
	
}