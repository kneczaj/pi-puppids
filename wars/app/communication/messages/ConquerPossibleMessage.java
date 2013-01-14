package communication.messages;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import models.ConqueringAttempt;
import models.Place;
import models.Player;

public class ConquerPossibleMessage {
	
	public ConqueringAttempt conqueringAttempt;
	
	public ObjectNode toJson() {
		ObjectNode invitation = Json.newObject();
		
		Player initiator = conqueringAttempt.getInitiator();
		Place place = conqueringAttempt.getPlace();
		
		invitation.put("messageType", "ConquerPossible");
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
