package org.kniftosoft.util.packet;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.kniftosoft.entity.Session;
import org.kniftosoft.entity.User;
import org.kniftosoft.util.Constants;
import org.kniftosoft.util.ErrorType;
import org.kniftosoft.util.Peerholder;
import org.kniftosoft.util.SHA256Generator;

import com.google.gson.JsonObject;

/**
 * @author julian
 * 
 */
public class LOGIN extends Packet {

	private String username;
	private String passwordHash;
	private boolean persist;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#createFromJSON(com.google.gson.JsonObject)
	 */
	@Override
	public void createFromJSON(JsonObject o) {
		if (o.has("username") && o.has("passwordHash") && o.has("persist")) {
			username = o.get("username").getAsString();
			passwordHash = o.get("passwordHash").getAsString();
			persist = o.get("persist").getAsBoolean();
		} else {
			final ERROR er = new ERROR();
			er.setPeer(peer);
			er.setUID(uid);
			er.setError(ErrorType.BAD_PACKET);
			er.send();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#executerequest()
	 */
	@Override
	public void executerequest() {
		if (peer.getSalt() != null && peer.isSaltused() == false) {
			final String email = username.toLowerCase();
			final String pass = passwordHash;
			final EntityManager em = Constants.factory.createEntityManager();
			try {
				final TypedQuery<User> userquery = em.createQuery(
						"Select u FROM User u WHERE u.email = '" + email + "'",
						User.class).setMaxResults(1);
				final User user = userquery.getSingleResult();
				final String password = user.getPassword()
						+ Peerholder.getpeer(peer).getSalt();
				if (email.toLowerCase().equals(user.getEmail().toLowerCase())
						&& pass.equals(SHA256Generator.StringTOSHA256(password))) {
					Session session = new Session();
					session.setUserBean(user);
					if (persist == true) {
						session = storesession(session);
					} else {
						session.setIdSessions(-1);
					}
					peer.setLoginverified(true);
					peer.setUser(user);
					peer.setSaltused(true);
					Peerholder.updatepeer(peer);
					// TODO add userconfig
					final AUTH ap = new AUTH();
					ap.setSessionID(session);
					ap.setPeer(peer);
					ap.setUID(uid);
					ap.setUserconfig(null);
					ap.send();
				} else {
					final NACK nack = new NACK();
					nack.setPeer(peer);
					nack.setUID(uid);
					nack.send();
				}

			} catch (final NoResultException e) {
				final NACK nack = new NACK();
				nack.setPeer(peer);
				nack.setUID(uid);
				nack.send();
			} catch (final Exception e) {
				final ERROR er = new ERROR();
				er.setPeer(peer);
				er.setUID(uid);
				er.setError(ErrorType.UNKNOWN);
				er.setErrorMessage(e.toString());
				er.send();
				e.printStackTrace();
			} finally {
				em.close();
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#getType()
	 */
	@Override
	public PacketType getType() {
		return PacketType.LOGIN;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.kniftosoft.util.packet.Packet#storeData()
	 */
	@Override
	public JsonObject storeData() {
		final JsonObject data = new JsonObject();
		data.addProperty("username", username);
		data.addProperty("passwordHash", passwordHash);
		return data;
	}

	/**
	 * @return passwordHash
	 */
	public String getPasswordHash() {
		return passwordHash;
	}
	
	/**
	 * @return username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @param passwordHash
	 *            the passwordHash to set
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	/**
	 * @param username
	 *            the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * stores session in Database and returns session with new id
	 * @param session
	 * @return session
	 */
	private Session storesession(Session session) {
		EntityManager em = Constants.factory.createEntityManager();
		em.getTransaction().begin();
		em.persist(session);
		System.out.println("storing session= " + session);
		em.getTransaction().commit();
		em.close();
		return session;
	}

}
