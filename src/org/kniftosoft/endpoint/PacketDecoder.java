package org.kniftosoft.endpoint;

import javax.websocket.DecodeException;
import javax.websocket.Decoder;
import javax.websocket.EndpointConfig;

import org.kniftosoft.util.Constants;
import org.kniftosoft.util.packet.Packet;
import org.kniftosoft.util.packet.PacketType;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * @author julian
 *
 */
public class PacketDecoder implements Decoder.Text<Packet> {
	private JsonParser parser;

	/* (non-Javadoc)
	 * @see javax.websocket.Decoder.Text#decode(java.lang.String)
	 */
	@Override
	public Packet decode(String msg) throws DecodeException {
		try {
			parser = new JsonParser();
			final JsonObject jsonPacket = (JsonObject) parser.parse(msg);

			final int packetTypeID = jsonPacket.get("typeID").getAsInt();

			final PacketType type = PacketType.byID(packetTypeID);

			if (type == null || type.getDirection() == Constants.outgoing) {
				throw new DecodeException(msg, "Invalid packet type ID: "
						+ packetTypeID);
			}

			try {
				System.out.println("create packet: " + msg);
				final Packet packet = (Packet) type.getPacketClass()
						.newInstance();
				packet.setUID(jsonPacket.get("uid").getAsInt());
				packet.createFromJSON(jsonPacket.get("data").getAsJsonObject());

				return packet;

			} catch (final InstantiationException e) {
				e.printStackTrace();
				throw new DecodeException(msg,
						"Could not instantiate packet class of packet type "
								+ type.name());
			} catch (final IllegalAccessException e) {
				e.printStackTrace();
				throw new DecodeException(msg,
						"Could not instantiate packet class of packet type "
								+ type.name());
			}
		} catch (final Exception e) {
			e.printStackTrace();
			throw new DecodeException(msg, "unknown");
		}
	}

	/* (non-Javadoc)
	 * @see javax.websocket.Decoder#destroy()
	 */
	@Override
	public void destroy() {

	}

	/* (non-Javadoc)
	 * @see javax.websocket.Decoder#init(javax.websocket.EndpointConfig)
	 */
	@Override
	public void init(EndpointConfig ec) {

	}

	/* (non-Javadoc)
	 * @see javax.websocket.Decoder.Text#willDecode(java.lang.String)
	 */
	@Override
	public boolean willDecode(String msg) {
		parser = new JsonParser();
		final JsonObject jsonPacket = (JsonObject) parser.parse(msg);
		if (jsonPacket.has("typeID") && jsonPacket.has("uid")
				&& jsonPacket.has("data")) {
			return true;
		} else {
			return false;
		}
	}
}
