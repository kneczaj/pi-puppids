package services.api;

import java.util.Set;

import models.InitiateConquerResult;
import models.Place;
import models.Player;
import services.api.error.ConqueringServiceException;

/**
 * Service for calculating the outcome of conquering attempts (conquer). The
 * service provides also information about which team mates of a player are
 * around a given place (getTeamMembersAllowedToParticipateInConquer) and which
 * are the players that are allowed to take part into a conquering attempt
 * (getTeamMembersWithSufficientResources)
 * 
 * @author markus
 */
public interface ConqueringService {

	public enum ConqueringResult {
		SUCCESSFUL, LOST, PLAYER_NOT_NEARBY, PLACE_ALREADY_BELONGS_TO_FACTION, RESOURCES_DO_NOT_SUFFICE, NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE, PLAYER_HAS_INSUFFICIENT_RESOURCES;
	}

	public enum JoinConquerResult {
		SUCCESSFUL, UNALLOWED_TO_JOIN, CONQUER_CANCELED, CONQUER_ALREADY_ENDED
	}

	/**
	 * Initiate a new Conquering-Attempt. Sends out notifications to all members
	 * of the player's team that are nearby.
	 * 
	 * @param player
	 *            the player which initiates the conquering attempt
	 * @param place
	 *            the place to conquer
	 * @return
	 */
	public InitiateConquerResult initiateConquer(Player player, Place place);

	public void sendOutInvitations(String conqueringAttemptId)
			throws ConqueringServiceException;

	/**
	 * Allows other players to join a conquering attempt.
	 * 
	 * @param conqueringAttemptId
	 *            the conquering attempt to join
	 * @param player
	 *            the player who wants to join
	 * @return
	 */
	public JoinConquerResult joinConquer(String conqueringAttemptId,
			Player player);

	/**
	 * Cancels an Conquering-Attempt
	 * 
	 * @param conqueringAttemptId
	 *            the ID of the conquering attempt to cancel
	 * @param player
	 *            the player that wants to cancel the conquer
	 * @return
	 */
	public boolean cancelConquer(String conqueringAttemptId, Player player);

	/**
	 * Do the actual conquer. Using all players of a given player's team that
	 * are currently around the requested place.
	 * 
	 * @param player
	 *            the initiator of the conquering attempt
	 * @param place
	 *            the place the player wants to conquer
	 */
	public ConqueringResult conquer(String conqueringAttemptId, Player player);

	/**
	 * Find the team members of a given player that are nearby a place.
	 * 
	 * @param player
	 * @param place
	 * @return
	 */
	public Set<Player> getTeamMembersNearby(Player player, Place place);

	/**
	 * Filter out the players that are not allowed to take part into a
	 * conquering attempt of a place (since they have not enough resources). If
	 * the resource demand of a place can not be fulfilled with the given list
	 * of players the returned set will be empty.
	 * 
	 * @param place
	 *            the place to be conquered by the players
	 * @param players
	 *            a list of players that are nearby the place
	 * @return
	 */
	public Set<Player> getTeamMembersWithSufficientResources(Place place,
			Set<Player> players);

}
