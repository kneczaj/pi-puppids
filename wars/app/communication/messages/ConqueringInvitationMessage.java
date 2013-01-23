package communication.messages;

//import java.util.List;

import models.ConqueringAttempt;
import models.Player;
import models.notifications.Notification;

import org.codehaus.jackson.node.ObjectNode;

//import play.libs.Json;

public class ConqueringInvitationMessage extends Notification {
	
public int numberOfConquerors;
	
//	public ConqueringInvitationMessage() {
//		super();
//		payload = Payload.ConqeringInvitation;
//	}
	
	public ConqueringAttempt conqueringAttempt;
//	public List<Player> playersToInvite;
	
	public ObjectNode toJson() {
		ObjectNode invitation = super.toJson();
		
		Player initiator = conqueringAttempt.getInitiator();
		String uuid = conqueringAttempt.getUuid();
		
		invitation.put("messageType", "ConqueringInvitation");
		invitation.put("conqueringAttemptId", conqueringAttempt.getId().toString());
		invitation.put("start", conqueringAttempt.getStartDate().toString());
		invitation.put("initiatorId", initiator.getId().toString());
		invitation.put("placeId", uuid);
		
		return invitation;
	}
	
	public String getMessage() {
		return "You are invited to conquer " + conqueringAttempt.getPlace() + "\n";
	}
	
}