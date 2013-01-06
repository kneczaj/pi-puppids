package services.dummy;

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
import daos.TeamDAO;

public class ResourceServiceDummyImpl implements ResourceService {

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

		Map<Place, ResourceType> map = Maps.newHashMap();

		String[] placeNames = {"TU München", 
							"Augustinerkeller", 
							"Deutsche Bank", 
							"Hornbach", 
							"Garching-Forschungszentrum",
							"Flughafen Franz Josef Strauß",
							"Alte Pinakothek"};
		ResourceType[] resourceTypes = {ResourceType.Knowledge, 
										ResourceType.Food, 
										ResourceType.Credits, 
										ResourceType.Material, 
										ResourceType.Transportation,
										ResourceType.Special,
										ResourceType.Cultural};
		
		Integer[] amounts = {100, 200, 300, 400, 500, 600, 700};
		
		for (int i=0; i<7; i++) {
			Place place = new Place();
			place.setName(placeNames[i]);
			place.setResource(resourceTypes[i]);
			place.setAmount(amounts[i]);
			
			map.put(place, resourceTypes[i]);
		}

		return map;
	}

	/**
	 * Get a listing of a players' resources. Generates random resource amounts
	 * for testing purposes
	 * 
	 * @param player
	 * @return a mapping from a resource to the amount a player possesses.
	 */
	@Override
	public Map<ResourceType, Integer> getResourcesOfPlayer(Player player) {
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

		Map<ResourceType, Integer> map = Maps.newHashMap();
		Random rnd = new Random();

		for (ResourceType type : ResourceType.values()) {
			map.put(type, (rnd.nextInt(8192)+4096));
		}

		return map;
	}

}
