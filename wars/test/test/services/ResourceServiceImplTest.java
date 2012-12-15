package test.services;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

import models.Player;
import models.ResourceDepot;
import models.ResourceType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.common.collect.Maps;
import com.mongodb.Mongo;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.ResourceDepotDAO;

public class ResourceServiceImplTest {

	private static PlayerDAO playerDAO;
	private static PlaceDAO placeDAO;
	private static ResourceDepotDAO resourceDepotDAO;

	@BeforeClass
	public static void before() {
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

		try {
			Mongo mongo = new Mongo();
			Morphia morphia = new Morphia();
			playerDAO = new PlayerDAO(mongo, morphia);
			placeDAO = new PlaceDAO(mongo, morphia);
			resourceDepotDAO = new ResourceDepotDAO(mongo, morphia);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void DAOsNotNull() {
		Assert.assertNotNull(playerDAO);
		Assert.assertNotNull(placeDAO);
		Assert.assertNotNull(resourceDepotDAO);
	}

	/**
	 * Try to persist and retrieve a Player from the database
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
	 * @author michi
	 */
	@Test
	public void persistAndRetrieveResourceDepots() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");

		ResourceDepot creditDepot = new ResourceDepot();
		creditDepot.setResourceType(ResourceType.Credits);
		creditDepot.setAmount(1024);
		creditDepot.setPlayer(player);
		resourceDepotDAO.save(creditDepot);

		player.getResourceDepots().add(creditDepot);
		playerDAO.save(player);

		player = playerDAO.findOne("email", "bob@bobson.de");
		ResourceDepot creditLoad = player.getResourceDepots().get(0);

		Assert.assertNotNull(player);
		Assert.assertNotNull(creditLoad);
		Assert.assertEquals(ResourceType.Credits, creditLoad.getResourceType());
		Assert.assertEquals(new Integer(1024), creditLoad.getAmount());
	}

	/**
	 * Try to add a couple of ResourceDepots to a Player
	 * and generate a Map<ResourceType, Integer> with the
	 * resource and their respective amount for the player
	 * @author michi
	 */
	@Test
	public void getResourcesOfPlayerTest() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");

		ResourceDepot materialDepot = new ResourceDepot();
		materialDepot.setResourceType(ResourceType.Material);
		materialDepot.setAmount(2048);
		materialDepot.setPlayer(player);
		resourceDepotDAO.save(materialDepot);

		ResourceDepot foodDepot = new ResourceDepot();
		foodDepot.setResourceType(ResourceType.Food);
		foodDepot.setAmount(3096);
		foodDepot.setPlayer(player);
		resourceDepotDAO.save(foodDepot);

		player.getResourceDepots().add(materialDepot);
		player.getResourceDepots().add(foodDepot);
		playerDAO.save(player);

		player = playerDAO.findOne("email", "bob@bobson.de");

		Map<ResourceType, Integer> resourceMap = Maps.newHashMap();

		List<ResourceDepot> depotList = player.getResourceDepots();

		for (ResourceDepot depot : depotList) {
			resourceMap.put(depot.getResourceType(), depot.getAmount());
		}

		Assert.assertNotNull(player);
		Assert.assertNotNull(resourceMap);
		Assert.assertEquals(3, resourceMap.size());
		Assert.assertEquals(true, resourceMap.containsKey(ResourceType.Credits));
		Assert.assertEquals(true, resourceMap.containsKey(ResourceType.Material));
		Assert.assertEquals(true, resourceMap.containsKey(ResourceType.Food));
		Assert.assertEquals(new Integer(1024), resourceMap.get(ResourceType.Credits));
		Assert.assertEquals(new Integer(2048), resourceMap.get(ResourceType.Material));
		Assert.assertEquals(new Integer(3096), resourceMap.get(ResourceType.Food));
	}
	
	/**
	 * Delete all instances from the database
	 * @author michi
	 */
	@AfterClass
	public static void cleanDatabase() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");
		List<ResourceDepot> depotList = player.getResourceDepots();
		
		for (ResourceDepot depot : depotList) {
			resourceDepotDAO.delete(depot);
		}
		
		playerDAO.delete(player);
	}
}
