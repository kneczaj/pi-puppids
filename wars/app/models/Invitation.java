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
	
	// ---------------------------------------
	
	public Invitation() {
		state = State.CREATED;
	}
	
	public Invitation(Player sender, Player recipient) {
		state = State.CREATED;
		this.sender = sender;
		this.recipient = recipient;
		this.creationDate = System.currentTimeMillis();
	}

	public ObjectId getId() {
		return id;
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
