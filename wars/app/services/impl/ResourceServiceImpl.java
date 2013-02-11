package services.impl;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;

import assets.constants.TimeConstants;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Implementation of the ResourceService
 * 
 * @author michi
 * 
 */

public class ResourceServiceImpl implements ResourceService {

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private PlaceDAO placeDAO;

	@Inject
	private TeamDAO teamDAO;

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
		Player load = playerDAO.findOne("username", player.getUsername());

		if (load == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		List<Place> conquered = load.getConquered();

		Map<Place, ResourceType> map = Maps.newHashMap();

		for (Place place : conquered) {
			map.put(place, place.getResource());
		}

		return map;
	}

	/**
	 * Get a listing of a players' resources.
	 * 
	 * @param player
	 * @return a mapping from a resource to the amount a player possesses.
	 * @throws ResourceServiceException
	 */
	@Override
	public Map<ResourceType, Integer> getResourcesOfPlayer(Player player) {
		return player.getResourceDepot();
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

		Team load = teamDAO.findOne("id", team.getId());

		if (load == null)
			throw new NullPointerException("Could not find team with id "
					+ team.getId() + ".");

		List<Player> players = load.getPlayers();
		Map<ResourceType, Integer> teamResourceMap = Maps.newHashMap();

		// initialize the map for every resource with 0
		for (ResourceType type : ResourceType.values()) {
			teamResourceMap.put(type, 0);
		}

		for (Player player : players) {
			Map<ResourceType, Integer> playerResourceMap = this
					.getResourcesOfPlayer(player);

			for (ResourceType key : playerResourceMap.keySet()) {
				Integer value = teamResourceMap.get(key);
				value += playerResourceMap.get(key);
				teamResourceMap.put(key, value);
			}
		}

		return teamResourceMap;
	}

	@Override
	public Map<ResourceType, Integer> distributeResourcesToPlayer(Player player)
			throws ResourceServiceException {
		Player load = playerDAO.findOne("username", player.getUsername());

		if (load == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Map<ResourceType, Integer> resourceDepot = load.getResourceDepot();
		Map<ResourceType, Integer> resourcesToAdd = Maps.newHashMap();

		// Initialize map
		for (ResourceType type : ResourceType.values()) {
			resourcesToAdd.put(type, 0);
		}

		// sum up resources for all places
		// except for special resource, because it's not gained that way
		for (Place place : load.getConquered()) {
			ResourceType type = place.getResource();
			if (type != ResourceType.Special) {
				Integer amount = place.getAmount();
				resourcesToAdd.put(type, resourcesToAdd.get(type) + amount);
			}
		}

		// add resources to the player's depot
		// replace food, cultural, knowledge and transportation with the new values
		for (ResourceType type : ResourceType.values()) {
			Integer amount = resourcesToAdd.get(type);
			if (type == ResourceType.Material || type == ResourceType.Credits) {
				resourceDepot.put(type, resourceDepot.get(type) + amount);
			} else if (type == ResourceType.Food) {
				resourceDepot.put(type, amount);
			} else if (type == ResourceType.Cultural) {
				resourceDepot.put(type, calculateCulturalCoefficient(amount));
			} else if (type == ResourceType.Transportation) {
				resourceDepot.put(type, calculateTransportationCoefficient(amount));
			} else if (type == ResourceType.Knowledge) {
				resourceDepot.put(type, calculateKnowledgeCoefficient(amount));
			}
		}

		load.setResourceDepot(resourceDepot);
		playerDAO.save(load);
		
		return resourceDepot;
	}
	
	private Integer calculateTransportationCoefficient(Integer transportationValue) {
		//The formula is BaseValue * (1 / (2 - 1 / transportationValue))
		if (transportationValue <= 0)
			transportationValue = 1;
		return TimeConstants.BASE_TRAVELLING_TIME * (1 / (2 - 1 / transportationValue));
	}
	
	private Integer calculateCulturalCoefficient(Integer cultureValue) {
		//The formula is 2 - 1 / cultureValue
		if (cultureValue <= 0)
			cultureValue = 1;
		return 2 - 1 / cultureValue;
	}
	
	private Integer calculateKnowledgeCoefficient(Integer knowledgeValue) {
		//The formula is 2 - 1 / knowledgeValue
		if (knowledgeValue <= 0)
			knowledgeValue = 1;
		return 2 - 1 / knowledgeValue;
	}

}
