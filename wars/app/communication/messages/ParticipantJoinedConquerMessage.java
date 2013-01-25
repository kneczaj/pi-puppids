package communication.messages;

import models.ConqueringAttempt;
import models.Player;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class ParticipantJoinedConquerMessage {
	
	public Player participant;
	public ConqueringAttempt conqueringAttempt;
	
	public ObjectNode toJson() {
		ObjectNode joinMessage = Json.newObject();
		
		joinMessage.put("messageType", "ParticipantJoinedConquer");
		joinMessage.put("conqueringAttemptId", conqueringAttempt.getId().toString());
		joinMessage.put("participantId", participant.getId().toString());
		joinMessage.put("participantName", participant.getUsername());
		
		return joinMessage;
	}

}
