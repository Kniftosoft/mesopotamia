package org.kniftosoft.application;

import javax.websocket.DecodeException;

import org.kniftosoft.entity.Subscribe;
import org.kniftosoft.util.EuphratisSession;
import org.kniftosoft.util.UIDGen;
import org.kniftosoft.util.packet.DATA;

/**
 * @author julian
 * 
 */
public class Appinstance {

	Application app;
	Subscribe sub;
	EuphratisSession peer;

	/**
	 * @param sub
	 * @param peer
	 */
	public Appinstance(Subscribe sub, EuphratisSession peer) {
		this.peer = peer;
		this.sub = sub;
		try {
			getapp();
		} catch (final DecodeException e) {
			System.err.println(e.toString());
		}
	}

	/**
	 * instantiate the requred application
	 * @throws DecodeException
	 */
	private void getapp() throws DecodeException {
		final ApplicationType apptype = ApplicationType.byID(sub.getAppBean()
				.getIdapp());
		try {
			app = (Application) apptype.getAppClass().newInstance();
		} catch (final InstantiationException e) {
			throw new DecodeException(
					"Could not instantiate Application class of Application type ",
					apptype.name());

		} catch (final IllegalAccessException e) {
			throw new DecodeException(
					"Could not instantiate Application class of Application type ",
					apptype.name());
		}

	}

	/**
	 *  perform a update
	 */
	public void update() {
		final DATA update = new DATA();
		update.setResult(app.getdata(sub));
		update.setPeer(peer);
		update.setUID(UIDGen.instance().generateUID());
		update.setCategory(app.getid());
		update.send();
	}

}
