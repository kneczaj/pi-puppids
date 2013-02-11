package services.dummy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import models.Place;
import models.PlaceType;
import models.Player;
import models.ResourceType;
import models.Team;
import services.api.ResourceService;
import services.api.error.ResourceServiceException;
import assets.constants.PlaceMappings;

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
		
		PlaceType[] placeTypes = {PlaceType.atm, 
				PlaceType.bakery,
				PlaceType.school,
				PlaceType.church,
				PlaceType.movie_theater,
				PlaceType.pharmacy,
				PlaceType.train_station};

		Integer[] amounts = {100, 200, 300, 400, 500, 600, 700};
		
		List<Place> places = placeDAO.find().asList();
		if (!places.isEmpty()) {
			for (int i = 0; i < places.size(); i++) {
				map.put(places.get(i), resourceTypes[i%7]);
			}
			
			return map;
		}

		List <Player> players = new ArrayList <Player> ();
		players.add(player);
		
		for (int i=0; i<7; i++) {
			Map<ResourceType, Integer> resourceDemands = PlaceMappings.PLACE_TO_RESOURCE_DEMAND_MAP.get(placeTypes[i]);
			
			Place place = new Place();
			place.setName(placeNames[i]);
			place.setResource(resourceTypes[i]);
			place.setAmount(amounts[i]);
			place.setLat(48.136944 + (0.01 * i));
			place.setLng(11.575278 + (0.01 * i));
			place.setConqueredBy(players);
			place.setType(placeTypes[i]);
			place.setResourceDemand(resourceDemands);
			place.setUuid("uuid" + i);
			placeDAO.save(place);
			
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
		Map<ResourceType, Integer> resources = player.getResourceDepot();
		if (resources.size() > 0) {
			return resources;
		}
		
		for (ResourceType type : ResourceType.values()) {
			resources.put(type, 0);
		}
		
		return resources;
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

	@Override
	public Map<ResourceType, Integer> distributeResourcesToPlayer(Player player)
			throws ResourceServiceException {
		
		Player load = playerDAO.findOne("username", player.getUsername());

		if (load == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");
		
		Map<ResourceType, Integer> resourceDepot = load.getResourceDepot();
		
		//add resources to the player's depot
		for (ResourceType type : ResourceType.values()) {
			Integer amount = 5000;
			Integer newAmount = resourceDepot.get(type) + amount;
			if (type == ResourceType.Material || type == ResourceType.Credits) {
				resourceDepot.put(type, newAmount);
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
		double value = transportationValue.doubleValue();
					
		if (value <= 0)
			value = 1f;
		//The formula is BaseValue * (1 / (2 - 1 / transportationValue))	
		double coefficient = (1 / (2 - 1 / value)) * 100;
		
		return (int) coefficient;
	}
	
	private Integer calculateCulturalCoefficient(Integer cultureValue) {
		double value = cultureValue.doubleValue();
		
		if (value <= 0)
			value = 1f;
		
		//The formula is 2 - 1 / cultureValue
		double coefficient = (2 - 1 / value) * 100;
		
		
		return (int) coefficient;
	}
	
	private Integer calculateKnowledgeCoefficient(Integer knowledgeValue) {
		double value = knowledgeValue.doubleValue();
		
		if (value <= 0)
			value = 1f;
		
		//The formula is 2 - 1 / knowledgeValue
		double coefficient = (2 - 1 / knowledgeValue) * 100;
		return (int) coefficient;
	}

}
