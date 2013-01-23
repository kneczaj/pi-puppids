package communication.messages;

import java.util.List;

import models.ConqueringAttempt;
import models.Player;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class ConqueringInvitationMessage {
	
	public ConqueringAttempt conqueringAttempt;
	public List<Player> playersToInvite;
	
	public ObjectNode toJson() {
		ObjectNode invitation = Json.newObject();
		
		Player initiator = conqueringAttempt.getInitiator();
		String uuid = conqueringAttempt.getUuid();
		
		invitation.put("messageType", "ConqueringInvitation");
		invitation.put("conqueringAttemptId", conqueringAttempt.getId().toString());
		invitation.put("start", conqueringAttempt.getStartDate().toString());
		invitation.put("initiatorId", initiator.getId().toString());
		invitation.put("placeId", uuid);
		
		return invitation;
	}
	
}