package models;

import java.util.Date;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;

import securesocial.core.java.Token;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;

/**
 * AccessToken used by SecureSocial
 * 
 * @author markus
 */
@Entity("access_tokens")
public class AccessToken {

	@Id
	private ObjectId id;

	@Reference
	private Player player;
	private Date expirationTime;
	private Date creationTime;
	private boolean signUp;
	private String email;
	private String uuid;

	public AccessToken() {
		
	}
	
	public AccessToken(Player player, Date creationTime,
			Date expirationTime, boolean signUp, String uuid, String email) {
		this.player = player;
		this.creationTime = creationTime;
		this.expirationTime = expirationTime;
		this.signUp = signUp;
		this.uuid = uuid;
		this.email = email;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Date getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(Date expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public boolean isSignUp() {
		return signUp;
	}

	public void setSignUp(boolean signUp) {
		this.signUp = signUp;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Token toToken() {
		Token token = new Token();
		token.setCreationTime(new DateTime(creationTime));
		token.setExpirationTime(new DateTime(expirationTime));
		token.setIsSignUp(isSignUp());
		token.setUuid(uuid);
		token.setEmail(email);

		return token;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("player", player)
				.add("creationTime", creationTime)
				.add("expirationTime", expirationTime)
				.add("signUp", signUp)
				.add("email", email)
				.add("uuid", uuid).toString();
	}

}
