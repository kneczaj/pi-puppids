package services.api;

import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import services.api.error.ResourceServiceException;

/**
 * ResourceService
 * 
 * @author michi
 */
public interface ResourceService extends Service {

	/**
	 * Get a listing of the sources which generate resources for a given player.
	 * 
	 * @param player
	 * @return a mapping from places which belong to the user to the resource it
	 *         generates.
	 * @throws ResourceServiceException
	 */
	public Map<Place, ResourceType> getResourceSourcesOfPlayer(Player player)
			throws ResourceServiceException;

	/**
	 * Get a listing of a players' resources.
	 * 
	 * @param player
	 * @return a mapping from a resource to the amount a player possesses.
	 * @throws ResourceServiceException
	 */
	public Map<ResourceType, Integer> getResourcesOfPlayer(Player player)
			throws ResourceServiceException;

	/**
	 * Get a listing of a teams' resources
	 * 
	 * @param team
	 * @return a mapping from a resource to the amount a team possesses.
	 * @throws ResourceServiceException
	 */
	public Map<ResourceType, Integer> getResourcesOfTeam(Team team)
			throws ResourceServiceException;
}
