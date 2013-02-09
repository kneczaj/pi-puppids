package models.notifications;

import models.Player;
import models.conquer.ConqueringAttempt;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

public class ConqueringInvitationMessage extends Notification {

	public int numberOfConquerors;

	public ConqueringAttempt conqueringAttempt;

	public ObjectNode toJson() {
		ObjectNode invitation = Json.newObject();

		Player initiator = conqueringAttempt.getInitiator();
		String uuid = conqueringAttempt.getUuid();

		invitation.put("messageType", "ConqueringInvitation");
		invitation.put("conqueringAttemptId", conqueringAttempt.getId()
				.toString());
		invitation.put("start", conqueringAttempt.getStartDate().toString());
		invitation.put("initiatorId", initiator.getId().toString());
		invitation.put("initiatorName", initiator.getUsername());
		invitation.put("placeId", uuid);
		invitation.put("placeName", conqueringAttempt.getPlaceName());
		invitation.put("lat", conqueringAttempt.getLat());
		invitation.put("lng", conqueringAttempt.getLng());

		return invitation;
	}

}