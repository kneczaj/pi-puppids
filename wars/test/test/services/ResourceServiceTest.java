package test.services;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import services.api.ResourceService;
import services.api.error.ResourceServiceException;
import test.util.InjectorHelper;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Injector;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

public class ResourceServiceTest {

	private static PlayerDAO playerDAO;
	private static PlaceDAO placeDAO;
	private static TeamDAO teamDAO;
	private static ResourceService resourceService;

	@BeforeClass
	public static void before() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);
		
	    playerDAO = injector.getInstance(PlayerDAO.class);
	    placeDAO = injector.getInstance(PlaceDAO.class);
	    teamDAO = injector.getInstance(TeamDAO.class);
	    resourceService = injector.getInstance(ResourceService.class);
	    
		Assert.assertNotNull(playerDAO);
		Assert.assertNotNull(placeDAO);
		Assert.assertNotNull(teamDAO);
	    Assert.assertNotNull(resourceService);
	}

	/**
	 * Try to persist and retrieve a Player from the database
	 * 
	 * @author michi
	 */
	@Test
	public void persistAndRetrievePlayer() {
		Player player = new Player();
		player.setEmail("bob@bobson.de");
		player.setName("Bobson");
		player.setFirstname("Bob");

		playerDAO.save(player);

		Player load = playerDAO.findOne("email", "bob@bobson.de");

		Assert.assertNotNull(load);
		Assert.assertEquals(player.getEmail(), load.getEmail());
		Assert.assertEquals(player.getName(), load.getName());
		Assert.assertEquals(player.getFirstname(), load.getFirstname());
	}

	/**
	 * Try to add a ResourceDepot to a player and persist and retrieve it
	 * 
	 * @author michi
	 */
	@Test
	public void persistAndRetrieveResourceDepots() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");

		Map<ResourceType, Integer> depot = Maps.newHashMap();
		depot.put(ResourceType.Credits, 1000);
		player.setResourceDepot(depot);
		playerDAO.save(player);

		player = playerDAO.findOne("email", "bob@bobson.de");
		
		Assert.assertNotNull(player);
		Assert.assertNotNull(player.getResourceDepot());
		Assert.assertEquals(new Integer(1000), player.getResourceDepot(ResourceType.Credits));
	}

	/**
	 * Try to add a couple of ResourceDepots to a Player and generate a
	 * Map<ResourceType, Integer> with the resource and their respective amount
	 * for the player
	 * 
	 * @author michi
	 * @throws ResourceServiceException 
	 */
	@Test
	public void getResourcesOfPlayerTest() throws ResourceServiceException {
		Player player = playerDAO.findOne("email", "bob@bobson.de");

		Map<ResourceType, Integer> depot = Maps.newHashMap();
		depot.put(ResourceType.Credits, 1000);
		depot.put(ResourceType.Material, 2000);
		depot.put(ResourceType.Food, 3000);
		player.setResourceDepot(depot);
		
		playerDAO.save(player);
		player = playerDAO.findOne("email", "bob@bobson.de");

		Map<ResourceType, Integer> resourceMap = resourceService.getResourcesOfPlayer(player);

		Assert.assertNotNull(player);
		Assert.assertNotNull(resourceMap);
		Assert.assertEquals(3, resourceMap.size());
		Assert.assertEquals(true, resourceMap.containsKey(ResourceType.Credits));
		Assert.assertEquals(true,
				resourceMap.containsKey(ResourceType.Material));
		Assert.assertEquals(true, resourceMap.containsKey(ResourceType.Food));
		Assert.assertEquals(new Integer(1000),
				resourceMap.get(ResourceType.Credits));
		Assert.assertEquals(new Integer(2000),
				resourceMap.get(ResourceType.Material));
		Assert.assertEquals(new Integer(3000),
				resourceMap.get(ResourceType.Food));
	}

	@Test
	public void getResourceSourcesOfPlayerTest() throws ResourceServiceException {
		Player player = playerDAO.findOne("email", "bob@bobson.de");

		List<Player> conqueredBy = Lists.newLinkedList();
		conqueredBy.add(player);

		Place creditPlace = new Place();
		creditPlace.setResource(ResourceType.Credits);
		creditPlace.setAmount(100);
		creditPlace.setConqueredBy(conqueredBy);
		creditPlace.setName("CreditPlace");

		placeDAO.save(creditPlace);

		Place materialPlace = new Place();
		materialPlace.setResource(ResourceType.Material);
		materialPlace.setAmount(200);
		materialPlace.setConqueredBy(conqueredBy);
		materialPlace.setName("MaterialPlace");

		placeDAO.save(materialPlace);

		player.getConquered().add(creditPlace);
		player.getConquered().add(materialPlace);

		playerDAO.save(player);

		player = playerDAO.findOne("email", "bob@bobson.de");

		Map<Place, ResourceType> map = resourceService.getResourceSourcesOfPlayer(player);

		Assert.assertNotNull(player);
		Assert.assertEquals(2, map.size());
	}

	@Test
	public void persistAndRetrieveTeam() {
		Player bob = playerDAO.findOne("email", "bob@bobson.de");
		Player alice = new Player();
		alice.setEmail("alice@alison.de");
		alice.setName("Alison");
		alice.setFirstname("Alice");

		playerDAO.save(alice);
		alice = playerDAO.findOne("email", "alice@alison.de");
		
		Map<ResourceType, Integer> depot = Maps.newHashMap();
		depot.put(ResourceType.Credits, 1000);
		depot.put(ResourceType.Material, 2000);
		depot.put(ResourceType.Food, 3000);
		
		alice.setResourceDepot(depot);
		playerDAO.save(alice);

		Team team = new Team();
		team.setName("The fighting Mongooses");
		team.getPlayers().add(bob);
		team.getPlayers().add(alice);

		teamDAO.save(team);

		team = teamDAO.findOne("name", "The fighting Mongooses");

		Assert.assertNotNull(team);
		Assert.assertEquals(2, team.getPlayers().size());
	}

	@Test
	public void getResourcesOfTeamTest() throws ResourceServiceException {
		Team team = teamDAO.findOne("name", "The fighting Mongooses");
		
		Map<ResourceType, Integer> teamResourceMap = resourceService.getResourcesOfTeam(team);
		
		Assert.assertNotNull(teamResourceMap);
		Assert.assertEquals(ResourceType.values().length, teamResourceMap.size());
		Assert.assertEquals(new Integer(2000), teamResourceMap.get(ResourceType.Credits));
		Assert.assertEquals(new Integer(4000), teamResourceMap.get(ResourceType.Material));
		Assert.assertEquals(new Integer(6000), teamResourceMap.get(ResourceType.Food));
	}

	/**
	 * Delete all instances from the database
	 * 
	 * @author michi
	 */
	@AfterClass
	public static void cleanDatabase() {
		List<Player> players = Lists.newLinkedList();
		players.add(playerDAO.findOne("email", "bob@bobson.de"));
		players.add(playerDAO.findOne("email", "alice@alison.de"));

		Team team = teamDAO.findOne("name", "The fighting Mongooses");
		teamDAO.delete(team);

		for (Player player : players) {
			List<Place> places = player.getConquered();

			for (Place place : places) {
				placeDAO.delete(place);
			}

			playerDAO.delete(player);
		}
	}
}
