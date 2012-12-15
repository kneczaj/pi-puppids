package services.impl;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceDepot;
import models.ResourceType;
import models.Team;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.ResourceDepotDAO;

/**
 * Implementation of the ResourceService
 * @author michi
 *
 */

public class ResourceServiceImpl implements ResourceService {

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private PlaceDAO placeDAO;
	
	@Inject
	private ResourceDepotDAO resourceDepotDAO;
	
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
		Player load = playerDAO.findOne("id", player.getId());
		
		if (load == null)
			throw new ResourceServiceException("Player " + player.getId() + " not found!");
		
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
		
		Map<ResourceType, Integer> map = Maps.newHashMap();
		
		Player load = playerDAO.findOne("id", player.getId());
		
		if (load == null)
			throw new ResourceServiceException("Player " + player.getId() + " not found!");
		
		List<ResourceDepot> depotList = load.getResourceDepots();
		
		for (ResourceDepot depot : depotList) {
			map.put(depot.getResourceType(), depot.getAmount());
		}
		
		return map;
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
