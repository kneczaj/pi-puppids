package services.api;

import java.util.List;

import models.Player;
import models.PlayerLocation;
import models.conquer.ConqueringAttempt;

public interface WebSocketCommunicationService {
	
	/**
     * Sends "Hi!" message to the teammates
     * for testing purposes
     * 
     * @param player
     */
    public void sendHi(Player player);

    public void sendConqueringInvitation(ConqueringAttempt ca,
                List<Player> onlinePlayersOfTeam);

    public void conquerParticipantJoined(Player participant, ConqueringAttempt conqueringAttempt);

    public void sendConquerPossible(ConqueringAttempt conqueringAttempt);
    
    /**
     * Used by the LocationTrackingService to tell the Actor to fan out the
     * change of the location of a player to all others.
     *
     * @param playerLocation
     */
    public void playerLocationChanged(PlayerLocation playerLocation);

}
