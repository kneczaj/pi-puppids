package services.api;

import java.util.HashSet;
import java.util.List;

import models.Place;
import models.Player;

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

	/**
	 * Start a conquering attempt. Using all players of a given player's team
	 * that are currently around the requested place.
	 * 
	 * @param player
	 *            the initiator of the conquering attempt
	 * @param place
	 *            the place the player wants to conquer
	 */
	public ConqueringResult conquer(Player player, Place place);

	/**
	 * Find the team members of a given player that are nearby a place.
	 * 
	 * @param player
	 * @param place
	 * @return
	 */
	public List<Player> getTeamMembersAllowedToParticipateInConquer(
			Player player, Place place);

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
	public HashSet<Player> getTeamMembersWithSufficientResources(Place place,
			List<Player> players);

}
