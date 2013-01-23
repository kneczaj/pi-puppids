package communication.messages;

import java.util.List;

import models.ConqueringAttempt;
import models.Place;
import models.Player;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class ConqueringInvitationMessage {
	
	public ConqueringAttempt conqueringAttempt;
	public List<Player> playersToInvite;
	
	public ObjectNode toJson() {
		ObjectNode invitation = Json.newObject();
		
		Player initiator = conqueringAttempt.getInitiator();
		Place place = conqueringAttempt.getPlace();
		
		invitation.put("messageType", "ConqueringInvitation");
		invitation.put("conqueringAttemptId", conqueringAttempt.getId().toString());
		invitation.put("start", conqueringAttempt.getStartDate().toString());
		invitation.put("initiatorId", initiator.getId().toString());
		invitation.put("placeId", place.getIdString());
		invitation.put("placeName", place.getName());
		invitation.put("placeLat", place.getLat());
		invitation.put("placeLng", place.getLng());
		
		return invitation;
	}
	
}