package communication.messages;

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

}