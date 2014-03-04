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
	
	public abstract void createFromJSON(JsonObject o);
	
	public abstract void executerequest();
	
	protected abstract JsonObject storeData();
	
	public abstract PacketType getType();
	
	public JsonObject storeToJSON()
	{
		JsonObject jo = new JsonObject();
		jo.addProperty("typeID", this.typeID);
		jo.addProperty("uid", this.uid);
		jo.add("data", storeData());
		return jo;
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