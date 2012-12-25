package test.services;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceDepot;
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
import com.google.inject.Injector;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.ResourceDepotDAO;
import daos.TeamDAO;

public class ResourceServiceImplTest {

	private static PlayerDAO playerDAO;
	private static PlaceDAO placeDAO;
	private static ResourceDepotDAO resourceDepotDAO;
	private static TeamDAO teamDAO;
	private static ResourceService resourceService;

	@BeforeClass
	public static void before() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);
		
	    playerDAO = injector.getInstance(PlayerDAO.class);
	    placeDAO = injector.getInstance(PlaceDAO.class);
	    resourceDepotDAO = injector.getInstance(ResourceDepotDAO.class);
	    teamDAO = injector.getInstance(TeamDAO.class);
	    resourceService = injector.getInstance(ResourceService.class);
	    
		Assert.assertNotNull(playerDAO);
		Assert.assertNotNull(placeDAO);
		Assert.assertNotNull(resourceDepotDAO);
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

		ResourceDepot creditDepot = new ResourceDepot();
		creditDepot.setResourceType(ResourceType.Credits);
		creditDepot.setAmount(1000);
		creditDepot.setPlayer(player);
		resourceDepotDAO.save(creditDepot);

		player.getResourceDepots().add(creditDepot);
		playerDAO.save(player);

		player = playerDAO.findOne("email", "bob@bobson.de");
		ResourceDepot creditLoad = player.getResourceDepots().get(0);

		Assert.assertNotNull(player);
		Assert.assertNotNull(creditLoad);
		Assert.assertEquals(ResourceType.Credits, creditLoad.getResourceType());
		Assert.assertEquals(new Integer(1000), creditLoad.getAmount());
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

		ResourceDepot materialDepot = new ResourceDepot();
		materialDepot.setResourceType(ResourceType.Material);
		materialDepot.setAmount(2000);
		materialDepot.setPlayer(player);
		resourceDepotDAO.save(materialDepot);

		ResourceDepot foodDepot = new ResourceDepot();
		foodDepot.setResourceType(ResourceType.Food);
		foodDepot.setAmount(3000);
		foodDepot.setPlayer(player);
		resourceDepotDAO.save(foodDepot);

		player.getResourceDepots().add(materialDepot);
		player.getResourceDepots().add(foodDepot);
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

		ResourceDepot creditDepot = new ResourceDepot();
		creditDepot.setResourceType(ResourceType.Credits);
		creditDepot.setAmount(1000);
		creditDepot.setPlayer(alice);
		resourceDepotDAO.save(creditDepot);

		ResourceDepot materialDepot = new ResourceDepot();
		materialDepot.setResourceType(ResourceType.Material);
		materialDepot.setAmount(2000);
		materialDepot.setPlayer(alice);
		resourceDepotDAO.save(materialDepot);

		ResourceDepot foodDepot = new ResourceDepot();
		foodDepot.setResourceType(ResourceType.Food);
		foodDepot.setAmount(3000);
		foodDepot.setPlayer(alice);
		resourceDepotDAO.save(foodDepot);

		alice.getResourceDepots().add(creditDepot);
		alice.getResourceDepots().add(materialDepot);
		alice.getResourceDepots().add(foodDepot);
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
			List<ResourceDepot> depotList = player.getResourceDepots();
			List<Place> places = player.getConquered();

			for (ResourceDepot depot : depotList) {
				resourceDepotDAO.delete(depot);
			}

			for (Place place : places) {
				placeDAO.delete(place);
			}

			playerDAO.delete(player);
		}
	}
}
