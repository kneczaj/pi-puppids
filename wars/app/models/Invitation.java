package models;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;

@Entity("invitations")
public class Invitation extends TimeStampedModel {
	
	public enum State {
		CREATED, SENT, CONFIRMED
	}

	// reference to sender to inform an invited player who invites them
	@Reference
	private Player sender;
	
	@Reference
	private Player recipient;
	
	// additional reference to the team in case the sender will be removed
	// from the db in the meantime - resign from playing 
	@Reference 
	private Team team;
	
	private State state;
	private long expirationDate;
	private String token;
	private String email;
	
	// ---------------------------------------
	
	public Invitation() {
		super();
		state = State.CREATED;
	}
	
	public Invitation(Player sender, Player recipient) {
		this();
		this.team = sender.getTeam();
		this.email = recipient.getEmail();
		this.sender = sender;
		this.recipient = recipient;
	}
	
	public Invitation(Player sender, String email) {
		this();
		this.team = sender.getTeam();
		this.sender = sender;
		this.email = email;
	}
	
	public String getEmail() {
		if (email == null)
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
