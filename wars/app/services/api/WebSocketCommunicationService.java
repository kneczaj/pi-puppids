package services.api;

import java.util.List;
import java.util.Map;

import models.Player;
import models.PlayerLocation;
import models.ResourceType;
import models.conquer.ConqueringAttempt;

public interface WebSocketCommunicationService {
	
	/**
     * Sends "Hi!" message to the teammates of player
     * for testing purposes
     * 
     * @param player
     */
    public void sendHi(Player player);
    
    /**
	 * Creates simple text notification and sends to recipients
	 * @param recipients
	 */
	public void sendSimpleNotification(String title, String message, String type, List<Player> recipients);
	public void sendSimpleNotification(String title, String message, String type, Player recipient);

    public void sendConqueringInvitation(ConqueringAttempt ca,
                List<Player> players);

    public void conquerParticipantJoined(Player participant, ConqueringAttempt conqueringAttempt);

    public void sendConquerPossible(ConqueringAttempt conqueringAttempt);
    
    /**
     * Used by the LocationTrackingService to tell the Actor to fan out the
     * change of the location of a player to all others.
     *
     * @param playerLocation
     */
    public void playerLocationChanged(PlayerLocation playerLocation);
    
    public void sendResourcesChanged(Player player, Map<ResourceType, Integer> teamResources);

}
