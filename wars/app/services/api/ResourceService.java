package services.api;

import java.util.Map;

import services.api.error.ResourceServiceException;


import models.Place;
import models.Player;
import models.Resource;
import models.Team;

/**
 * ResourceService
 * 
 * @author markus
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
	public Map<Place, Resource> getResourceSourcesOfPlayer(Player player)
			throws ResourceServiceException;

	/**
	 * Get a listing of a players' resources.
	 * 
	 * @param player
	 * @return a mapping from a resource to the amount a player possesses.
	 * @throws ResourceServiceException
	 */
	public Map<Resource, Integer> getResourcesOfPlayer(Player player)
			throws ResourceServiceException;

	/**
	 * Get a listing of a teams' resources
	 * 
	 * @param team
	 * @return a mapping from a resource to the amount a team possesses.
	 * @throws ResourceServiceException
	 */
	public Map<Resource, Integer> getResourcesOfTeam(Team team)
			throws ResourceServiceException;

}
