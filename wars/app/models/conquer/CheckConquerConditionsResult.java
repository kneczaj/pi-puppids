package models.conquer;

import java.util.List;

import models.Player;

import services.api.ConqueringService.ConqueringStatus;

import com.google.common.collect.Lists;

public class CheckConquerConditionsResult {
	
	private ConqueringStatus conqueringStatus;
	private List<Player> participants = Lists.newArrayList();
	private ConqueringAttempt conqueringAttempt;
	
	public CheckConquerConditionsResult(ConqueringAttempt ca) {
		this.conqueringAttempt = ca;
	}

	public ConqueringStatus getConqueringStatus() {
		return conqueringStatus;
	}

	public void setConqueringStatus(ConqueringStatus conqueringStatus) {
		this.conqueringStatus = conqueringStatus;
	}

	public List<Player> getParticipants() {
		return participants;
	}

	public void setParticipants(List<Player> participants) {
		this.participants = participants;
	}

	public void setConqueringAttempt(ConqueringAttempt ca) {
		this.conqueringAttempt  = ca;
	}
	
	public ConqueringAttempt getConqueringAttempt() {
		return conqueringAttempt;
	}

}
