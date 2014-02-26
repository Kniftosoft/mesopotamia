/**
 * 
 */
package org.kniftosoft.util;

import org.kniftosoft.Login.Loginmanager;
import org.kniftosoft.entity.EuphratisSession;

/**
 * @author julian
 *
 */
public class RecivedPacket extends Packet {

	public void executerequest()
	{

		switch(typeID)
		{	
		
		
		case	1:	//Handshake
					if(data.get("clientVersion").getAsString().equals(Constants.getClientversion()))
					{
						AnswerPacket ap = new AnswerPacket(2, uid, peer);
						ap.addDataProperty("salt", Long.toHexString(Double.doubleToLongBits(Math.random())));
						ap.send();
						break;
					}
					else
					{
						// TODO A Refused Connection is not removed from sessions
						AnswerPacket ap = new AnswerPacket(255, uid, peer);
						ap.addDataProperty("reasonCode", 4);
						ap.addDataProperty("reasonMessage", "Connection Refused because of a wrong Client Version");
						ap.send();
						break;
					}
		case 	10:		//login	
					Loginmanager.login(this);
		break;
		
		
		case 	12: 	//relog
					Loginmanager.relog(this);
		
		break;
		
		
		case 	14:		//logout
					Loginmanager.Logout(this);
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
		
		
		case	255:	//quit
					//TODO implement
			break;
		default :	//No valid type ID
					AnswerPacket ap = new  AnswerPacket(242, uid, peer);
					ap.addDataProperty("reasonCode", 4);
					ap.addDataProperty("reasonMessage", "Unknown/Unexecutable TypeID");
			break;
		}
	}
	/**
	 * @param message
	 * @param peer
	 */
	public RecivedPacket(String message, EuphratisSession peer) {
		super(message, peer);
	}

}
