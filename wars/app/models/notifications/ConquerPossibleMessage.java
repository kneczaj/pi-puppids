package models.notifications;

import models.Player;
import models.conquer.ConqueringAttempt;

import org.codehaus.jackson.node.ObjectNode;

public class ConquerPossibleMessage extends Notification {
	
	public ConqueringAttempt conqueringAttempt;
	
	public ObjectNode toJson() {
		ObjectNode conquerPossibleMessage = getInitialJson();
		
		Player initiator = conqueringAttempt.getInitiator();
		String uuid = conqueringAttempt.getUuid();
		
		conquerPossibleMessage.put("messageType", "ConquerPossible");
		conquerPossibleMessage.put("conqueringAttemptId", conqueringAttempt.getId().toString());
		conquerPossibleMessage.put("start", conqueringAttempt.getStartDate().toString());
		conquerPossibleMessage.put("initiatorId", initiator.getId().toString());
		conquerPossibleMessage.put("placeName", conqueringAttempt.getPlaceName());
		conquerPossibleMessage.put("placeId", uuid);
		
		return conquerPossibleMessage;
	}

}
