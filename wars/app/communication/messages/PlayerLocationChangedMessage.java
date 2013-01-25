package communication.messages;

import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;
import models.PlayerLocation;

/**
 * Is sent to the ClientPushActor if the location of a player changed.
 * 
 * @author markus
 */
public class PlayerLocationChangedMessage {

	private final PlayerLocation playerLocation;

	public PlayerLocationChangedMessage(PlayerLocation playerLocation) {
		this.playerLocation = playerLocation;
	}

	public PlayerLocation getPlayerLocation() {
		return playerLocation;
	}
	
	public ObjectNode toJson() {
		ObjectNode positionChange = Json.newObject();

		positionChange.put("messageType", "PlayerLocationChange");
		positionChange.put("id", playerLocation.getPlayer().getId().toString());
		positionChange.put("timestamp", playerLocation.getTimestamp().getTime());
		positionChange.put("longitude", playerLocation.getLongitude().toString());
		positionChange.put("latitude", playerLocation.getLatitude().toString());
		positionChange.put("speed", playerLocation.getSpeed());
		positionChange.put("accuracy", playerLocation.getUncertainty());
		
		return positionChange;
	}

}