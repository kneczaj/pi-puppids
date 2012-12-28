package test.services;

import java.util.List;

import models.Player;
import models.Unit;
import models.UnitType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import services.api.PlaceService;
import services.api.ResourceService;
import services.api.UnitService;
import services.impl.PlaceServiceImpl;
import services.impl.ResourceServiceImpl;
import services.impl.UnitServiceImpl;
import test.util.InjectorHelper;

import com.google.common.collect.Lists;
import com.google.inject.Injector;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.UnitDAO;

public class PlaceServiceTest {

	private static PlayerDAO playerDAO;
	private static PlaceDAO placeDAO;
	private static UnitDAO unitDAO;
	private static PlaceService placeService;
	private static UnitService unitService;
	private static ResourceService resourceService;

	@BeforeClass
	public static void before() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);
		
	    playerDAO = injector.getInstance(PlayerDAO.class);
	    placeDAO = injector.getInstance(PlaceDAO.class);
	    unitDAO = injector.getInstance(UnitDAO.class);
	    placeService = injector.getInstance(PlaceServiceImpl.class);
	    unitService = injector.getInstance(UnitServiceImpl.class);
	    resourceService = injector.getInstance(ResourceServiceImpl.class);
	    
	    Assert.assertNotNull(placeService);
		Assert.assertNotNull(playerDAO);
		Assert.assertNotNull(placeDAO);
		Assert.assertNotNull(unitDAO);
		Assert.assertNotNull(unitService);
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
	 * Try to add a Unit to a player and persist and retrieve it
	 * 
	 * @author michi
	 */
	@Test
	public void persistAndRetrieveUnit() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");
		
		Unit grunt = unitService.getInstance(UnitType.GRUNT);
		grunt.setPlayer(player);
		unitDAO.save(grunt);
		
		player.getUnits().add(grunt);
		playerDAO.save(player);
		
		player = playerDAO.findOne("email", "bob@bobson.de");
		Unit loadedUnit = player.getUnits().get(0);
		
		Assert.assertNotNull(player);
		Assert.assertNotNull(loadedUnit);
		Assert.assertEquals(UnitType.GRUNT, loadedUnit.getType());
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

		for (Player player : players) {
			List<Unit> unitList = player.getUnits();

			for (Unit unit : unitList) {
				unitDAO.delete(unit);
			}

			playerDAO.delete(player);
		}
	}
}
