package services.impl;

import java.util.Map;

import com.google.inject.Inject;

import daos.PlayerDAO;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;

/**
 * Implementation of the ResourceService
 * @author michi
 *
 */

public class ResourceServiceImpl implements ResourceService {

	@Inject
	private PlayerDAO playerDAO;
	
	/**
	 * Get a listing of the sources which generate resources for a given player.
	 * 
	 * @param player
	 * @return a mapping from places which belong to the user to the resource it
	 *         generates.
	 * @throws ResourceServiceException
	 */
	@Override
	public Map<Place, ResourceType> getResourceSourcesOfPlayer(Player player)
			throws ResourceServiceException {
		Player load = playerDAO.findOne("email", player.getEmail());
		
		if (load == null)
			throw new ResourceServiceException("Player " + player.getEmail() + " not found!");
		
		//TODO Load places from the db
		
		return null;
	}

	/**
	 * Get a listing of a players' resources.
	 * 
	 * @param player
	 * @return a mapping from a resource to the amount a player possesses.
	 * @throws ResourceServiceException
	 */
	@Override
	public Map<ResourceType, Integer> getResourcesOfPlayer(Player player)
			throws ResourceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Get a listing of a teams' resources
	 * 
	 * @param team
	 * @return a mapping from a resource to the amount a team possesses.
	 * @throws ResourceServiceException
	 */
	@Override
	public Map<ResourceType, Integer> getResourcesOfTeam(Team team)
			throws ResourceServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
