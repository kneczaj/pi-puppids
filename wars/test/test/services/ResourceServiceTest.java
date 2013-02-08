package test.services;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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

	private Player bob;
	private Player alice;
	private Team team;
	private Place creditPlace;
	private Place materialPlace;

	@BeforeClass
	public static void startUp() {
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

	@Before
	public void before() {
		bob = new Player();
		bob.setEmail("bob@bobson.de");
		bob.setName("Bobson");
		bob.setFirstname("Bob");
		bob.setUsername("bob");

		Map<ResourceType, Integer> depot = Maps.newHashMap();
		depot.put(ResourceType.Credits, 1000);
		depot.put(ResourceType.Material, 2000);
		depot.put(ResourceType.Food, 3000);
		bob.setResourceDepot(depot);
		
		alice = new Player();
		alice.setEmail("alice@alison.de");
		alice.setName("Alison");
		alice.setFirstname("Alice");
		alice.setUsername("alice");
		
		depot = Maps.newHashMap();
		depot.put(ResourceType.Credits, 1000);
		depot.put(ResourceType.Material, 2000);
		depot.put(ResourceType.Food, 3000);
		alice.setResourceDepot(depot);
		
		playerDAO.save(bob);
		playerDAO.save(alice);
		
		team = new Team();
		team.setName("The fighting Mongooses");
		team.getPlayers().add(bob);
		team.getPlayers().add(alice);

		teamDAO.save(team);
		
		bob.setTeam(team);
		alice.setTeam(team);

		List<Player> conqueredBy = Lists.newLinkedList();
		conqueredBy.add(bob);

		creditPlace = new Place();
		creditPlace.setResource(ResourceType.Credits);
		creditPlace.setAmount(100);
		creditPlace.setConqueredBy(conqueredBy);
		creditPlace.setName("CreditPlace");

		placeDAO.save(creditPlace);

		materialPlace = new Place();
		materialPlace.setResource(ResourceType.Material);
		materialPlace.setAmount(200);
		materialPlace.setConqueredBy(conqueredBy);
		materialPlace.setName("MaterialPlace");

		placeDAO.save(materialPlace);

		bob.getConquered().add(creditPlace);
		bob.getConquered().add(materialPlace);

		playerDAO.save(bob);
		playerDAO.save(alice);
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
		Map<ResourceType, Integer> resourceMap = resourceService
				.getResourcesOfPlayer(bob);

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
	public void getResourceSourcesOfPlayerTest()
			throws ResourceServiceException {
		Map<Place, ResourceType> map = resourceService
				.getResourceSourcesOfPlayer(bob);

		Assert.assertEquals(2, map.size());
	}

	@Test
	public void getResourcesOfTeamTest() throws ResourceServiceException {
		Map<ResourceType, Integer> teamResourceMap = resourceService
				.getResourcesOfTeam(team);

		Assert.assertNotNull(teamResourceMap);
		Assert.assertEquals(ResourceType.values().length,
				teamResourceMap.size());
		Assert.assertEquals(new Integer(2000),
				teamResourceMap.get(ResourceType.Credits));
		Assert.assertEquals(new Integer(4000),
				teamResourceMap.get(ResourceType.Material));
		Assert.assertEquals(new Integer(6000),
				teamResourceMap.get(ResourceType.Food));
	}
	
	@Test
	public void distributeResourcesTest() throws ResourceServiceException {
		Integer creditAmount = bob.getResourceDepot(ResourceType.Credits) + creditPlace.getAmount();
		Integer materialAmount = bob.getResourceDepot(ResourceType.Material) + materialPlace.getAmount();
		
		Map<ResourceType, Integer> depot = resourceService.distributeResourcesToPlayer(bob);
		
		Assert.assertEquals(creditAmount, depot.get(ResourceType.Credits));
		Assert.assertEquals(materialAmount, depot.get(ResourceType.Material));
	}

	/**
	 * Delete all instances from the database
	 * 
	 * @author michi
	 */
	@After
	public void tearDown() {
		playerDAO.delete(bob);
		playerDAO.delete(alice);
		teamDAO.delete(team);
		placeDAO.delete(creditPlace);
		placeDAO.delete(materialPlace);
	}
}
