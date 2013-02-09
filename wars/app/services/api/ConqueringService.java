package services.api;

import java.util.Set;

import models.Player;
import models.conquer.CheckConquerConditionsResult;
import models.conquer.InitiateConquerResult;
import services.google.places.api.GPlaceServiceException;

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

	public enum ConqueringStatus {
		SUCCESSFUL, LOST, PLAYER_NOT_NEARBY, PLACE_ALREADY_BELONGS_TO_FACTION, RESOURCES_DO_NOT_SUFFICE, NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE, PLAYER_HAS_INSUFFICIENT_RESOURCES, CONQUER_POSSIBLE;
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
	 * @param uuid
	 * 			  the uuid of the place to conquer (from google maps)
	 * @param reference
	 * 			  the reference of the place to conquer (from google maps)
	 * @return
	 */
	public InitiateConquerResult initiateConquer(Player player, String uuid, String reference) throws GPlaceServiceException;

	public void sendOutInvitations(String conqueringAttemptId);

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
			Player player) throws GPlaceServiceException;

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
	public ConqueringStatus conquer(String conqueringAttemptId, Player player) throws GPlaceServiceException;

	/**
	 * Find the team members of a given player that are nearby a place.
	 * 
	 * @param player
	 * @param place
	 * @return
	 */
	public Set<Player> getTeamMembersNearby(Player player, String uuid) throws GPlaceServiceException;

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
	public Set<Player> getTeamMembersWithSufficientResources(String uuid, String reference,
			Set<Player> players) throws GPlaceServiceException;

	public CheckConquerConditionsResult checkConquerConditions(
			String conqueringAttemptId, Player player) throws GPlaceServiceException;

}
