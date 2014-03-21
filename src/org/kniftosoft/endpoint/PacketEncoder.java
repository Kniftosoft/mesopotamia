package org.kniftosoft.endpoint;

import javax.websocket.EncodeException;
import javax.websocket.Encoder;
import javax.websocket.EndpointConfig;

import org.kniftosoft.util.packet.Packet;

import com.google.gson.JsonObject;

/**
 * @author julian
 *
 */
public class PacketEncoder implements Encoder.Text<Packet> {

	/* (non-Javadoc)
	 * @see javax.websocket.Encoder#destroy()
	 */
	@Override
	public void destroy() {

	}

	/* (non-Javadoc)
	 * @see javax.websocket.Encoder.Text#encode(java.lang.Object)
	 */
	@Override
	public String encode(Packet packet) throws EncodeException {
		try {
			final JsonObject jo = packet.storeData();

			if (jo == null) {
				throw new EncodeException(packet, "The packet of type "
						+ packet.getType().name()
						+ " could not be stored to JSON.");
			}

			final JsonObject fullPacket = new JsonObject();
			fullPacket.addProperty("typeID", packet.getType().getTypeID());
			fullPacket.addProperty("uid", packet.getUID()); // TODO: Implement
															// UID generator
			fullPacket.add("data", jo);
			System.out.println("I have send this" + fullPacket.toString());
			return fullPacket.toString();
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see javax.websocket.Encoder#init(javax.websocket.EndpointConfig)
	 */
	@Override
	public void init(EndpointConfig ec) {

	}

}
