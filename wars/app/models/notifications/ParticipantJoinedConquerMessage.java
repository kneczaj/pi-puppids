package models.notifications;

import models.Player;
import models.conquer.ConqueringAttempt;

import org.codehaus.jackson.node.ObjectNode;

public class ParticipantJoinedConquerMessage extends Notification {
	
	public Player participant;
	public ConqueringAttempt conqueringAttempt;
	
	public ObjectNode toJson() {
		ObjectNode joinMessage = getInitialJson();
		
		joinMessage.put("messageType", "ParticipantJoinedConquer");
		joinMessage.put("conqueringAttemptId", conqueringAttempt.getId().toString());
		joinMessage.put("participantId", participant.getId().toString());
		joinMessage.put("participantName", participant.getUsername());
		
		return joinMessage;
	}

}
