package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

import java.lang.System;

@Entity("invitations")
public class Invitation {
	
	public enum State {
		CREATED, SENT, CONFIRMED
	}

	@Id
	private ObjectId id;
	
	@Reference
	private Player sender;
	
	@Reference
	private Player recipient;
	
	@Reference 
	private Team team;
	
	private State state;
	private long creationDate;
	private long expirationDate;
	private String token;
	private String email;
	
	// ---------------------------------------
	
	public Invitation() {
		state = State.CREATED;
		this.creationDate = System.currentTimeMillis();
	}
	
	public Invitation(Player sender, Player recipient) {
		this();
		this.sender = sender;
		this.recipient = recipient;
	}
	
	public Invitation(Player sender, String email) {
		this();
		this.sender = sender;
		this.email = email;
	}

	public ObjectId getId() {
		return id;
	}
	
	public String getEmail() {
		if (email.isEmpty())
			return recipient.getEmail();
		return email;
	}

	public Player getSender() {
		return sender;
	}
	
	public String getToken() {
		return token;
	}
	
	public void setToken(String token) {
		this.token = token;
	}
	
	public Player getRecipient() {
		return recipient;
	}
	
	public Team getTeam() {
		return sender.getTeam();
	}
	
	public long getCreationDate() {
		return creationDate;
	}
	
	public long getExpirationDate() {
		return expirationDate;
	}

	public State getState() {
		return state;
	}
	
	public Boolean setSent() {
		if (state == State.CREATED)
			state = State.SENT;
		return (state == State.SENT);
	}
	
	public Boolean setConfimed() {
		if (state == State.SENT)
			state = State.CONFIRMED;
		return (state == State.CONFIRMED);
	}

}
