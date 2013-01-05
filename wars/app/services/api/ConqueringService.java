package services.api;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Unit;

/**
 * Service for calculating the outcome of conquering attempts.
 * 
 * @author markus
 */
public interface ConqueringService {

	public enum ConqueringResult {
		SUCCESSFUL,
		LOST, 
		PLAYER_NOT_NEARBY, 
		PLACE_ALREADY_BELONGS_TO_FACTION,
		RESOURCES_DO_NOT_SUFFICE;
	}

	/**
	 * Check whether the resources of players overcome a given set of defending units.
	 * 
	 * @param players
	 * @param defendingUnits
	 * @return
	 */
	public boolean doResourcesOvercomeDefendingUnits(List<Player> players,
			List<Unit> defendingUnits);
	
	/**
	 * Check whether the resources of players overcome a given resource demand.
	 * 
	 * @param players
	 * @param resourceDemand
	 * @return
	 */
	public boolean doResourcesOvercomeResourceDemand(List<Player> players,
			Map<ResourceType, Integer> resourceDemand);

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


}
