package services.dummy;

import java.util.List;
import java.util.Map;
import java.util.Random;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.ResourceDepotDAO;
import daos.TeamDAO;

public class ResourceServiceDummyImpl implements ResourceService {

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private PlaceDAO placeDAO;

	@Inject
	private ResourceDepotDAO resourceDepotDAO;

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
		Player load = playerDAO.findOne("id", player.getId());

		if (load == null)
			throw new ResourceServiceException("Player " + player.getId()
					+ " not found!");

		List<Place> conquered = load.getConquered();

		Map<Place, ResourceType> map = Maps.newHashMap();

		for (Place place : conquered) {
			map.put(place, place.getResource());
		}

		return map;
	}

	/**
	 * Get a listing of a players' resources. Generates random resource amounts
	 * for testing purposes
	 * 
	 * @param player
	 * @return a mapping from a resource to the amount a player possesses.
	 * @throws ResourceServiceException
	 */
	@Override
	public Map<ResourceType, Integer> getResourcesOfPlayer(Player player)
			throws ResourceServiceException {

		Map<ResourceType, Integer> map = Maps.newHashMap();
		Random rnd = new Random();

		for (ResourceType type : ResourceType.values()) {
			map.put(type, rnd.nextInt(1024));
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

		Team load = teamDAO.findOne("id", team.getId());
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
}
