package org.kniftosoft.endpoint;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.kniftosoft.util.ErrorType;
import org.kniftosoft.util.EuphratisSession;
import org.kniftosoft.util.Peerholder;
import org.kniftosoft.util.packet.ERROR;
import org.kniftosoft.util.packet.Packet;

@ServerEndpoint(value = "/TIG_TEST_END", configurator = Mesoendconfigurator.class, encoders = { PacketEncoder.class }, decoders = { PacketDecoder.class })
/**
 * 
 * @author julian
 *
 */
public class MesopotamiaEndpoint {
	/**
	 * removes the session from the session set
	 * @param peer
	 */
	@OnClose
	public void onClose(Session peer) {
		try {
			Peerholder.removepeer(peer);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}


	/**
	 * Handles Messages
	 * @param packet
	 * @param peer
	 */
	@OnMessage
	public void onMessage(Packet packet, Session peer) {
		try {
			packet.setPeer(Peerholder.getpeer(peer));
			packet.executerequest();
		} catch (final Exception e) {
			final ERROR er = new ERROR();
			er.setPeer(packet.getPeer());
			er.setUID(packet.getUID());
			er.setError(ErrorType.UNKNOWN);
			er.send();
			e.printStackTrace();
		}

	}


	/**
	 * add the Peer to the Peerholder
	 * @param peer
	 */
	@OnOpen
	public void onOpen(Session peer) {
		try {
			final EuphratisSession es = new EuphratisSession(peer);
			Peerholder.addpeer(es);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
