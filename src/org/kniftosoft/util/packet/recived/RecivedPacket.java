/**
 * 
 */
package org.kniftosoft.util.packet.recived;

import org.kniftosoft.Login.Loginmanager;
import org.kniftosoft.entity.EuphratisSession;
import org.kniftosoft.thread.ClientUpDater;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.packet.Packet;
import org.kniftosoft.util.packet.answer.ACCEPTPacket;
import org.kniftosoft.util.packet.answer.ERRORPacket;

/**
 * @author julian
 *
 */
public class RecivedPacket extends Packet {
	RecivedPacket rp;
	public void executerequest()
	{
		rp.executerequest();
		System.out.println("rp");

		switch(typeID)
		{	
		
		
		case	1:		//Handshake
					rp = new HANDSHAKEPackage(uid, peer);
		case 	10:		//login	
					rp = new LOGINPackage(uid, peer, data.get("username").getAsString(), data.get("passwordHash").getAsString());
		break;
		
		
		case 	12: 	//relog
					Loginmanager.relog(this);
		
		break;
		
		
		case 	14:		//logout
					rp = new LOGOUTPackage(uid, peer, data.get("sessionID").getAsString(),data.get("reasonCode").getAsInt(), data.get("reasonMessage").getAsString());
		break;
		
		
		case	20:	//query
					//TODO implement
		break;
		
		
		case	200:	//ack
					//TODO implement
		break;
		
		
		case	201:	//nack
					//TODO implement
		break;
		
		
		case	242:	//error
					//TODO implement
		break;
		
		default :	//No valid type ID
					ERRORPacket ap = new ERRORPacket(uid, peer,4,"Unknown/Unexecutable TypeID");
					ap.send();
			break;
		}
	}
	/**
	 * @param message
	 * @param peer
	 */
	public RecivedPacket(String message, EuphratisSession peer) {
		super(message, peer);
		System.out.println("RecivedPacket Construktor");
		
	}
	public RecivedPacket(int typeID, int uid ,EuphratisSession peer) {
		super(typeID,uid,peer);

		// TODO Auto-generated constructor stub
	}

}
