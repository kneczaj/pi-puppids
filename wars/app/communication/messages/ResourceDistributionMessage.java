package communication.messages;

import models.Player;
/**
 * Is sent to the RessourceDistributionActor to split up distribution work
 * @author michi
 *
 */
public class ResourceDistributionMessage {
	
	private Player player;
	
	public ResourceDistributionMessage(Player player) {
		this.player = player;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
