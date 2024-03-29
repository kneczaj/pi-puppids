package test.services;

import java.util.List;

import models.Place;
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
import test.util.SamplePlaces;

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
	
	@Test
	public void persistAndRetrievePlaceWithUnits() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");
		
		//TUM was conquered by bob
		Place tum = SamplePlaces.tum; 
		tum.getConqueredBy().add(player);
		
		//Bob has conquered the TUM
		player.getConquered().add(tum);
		
		Unit infantry = unitService.getInstance(UnitType.INFANTRY);
		infantry.setPlayer(player);		
		player.getUnits().add(infantry);
		unitDAO.save(infantry);
		
		for (Unit unit : player.getUnits()) {
			//All units of bob are deployed at TUM
			unit.setDeployedAt(tum);
			tum.getDeployedUnits().add(unit);
			placeDAO.save(tum);
			unitDAO.save(unit);
		}
		
		playerDAO.save(player);		
		placeDAO.save(tum);
		
		Place load = placeDAO.findOne("id", tum.getId());
		
		Assert.assertNotNull(load);
		Assert.assertEquals("Technische Universität München", load.getName());
		Assert.assertEquals(2, tum.getDeployedUnits().size());
	}
	
	@Test
	public void getDeployedUnitsOfPlayer() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");
		
		//Retriving a place by name is good for now
		Place tum = placeDAO.findOne("name", "Technische Universität München");
		List<Unit> deployedUnits = placeService.getDeployedUnitsOfPlayer(player, tum);
		
		Assert.assertNotNull(deployedUnits);
		Assert.assertEquals(2, deployedUnits.size());
	}
	
	@Test
	public void getDeployedUnitsOfPlayerByUnitType() {
		Player player = playerDAO.findOne("email", "bob@bobson.de");
		
		//Retriving a place by name is good for now
		Place tum = placeDAO.findOne("name", "Technische Universität München");
		
		List<Unit> grunts = placeService.getDeployedUnitsOfPlayer(player, UnitType.GRUNT, tum);
		List<Unit> infantry = placeService.getDeployedUnitsOfPlayer(player, UnitType.GRUNT, tum);
		
		Assert.assertNotNull(grunts);
		Assert.assertNotNull(infantry);
		Assert.assertEquals(1, grunts.size());
		Assert.assertEquals(1, infantry.size());
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
		
		List<Place> places = placeDAO.find().asList();

		for (Player player : players) {
			List<Unit> unitList = player.getUnits();

			for (Unit unit : unitList) {
				unitDAO.delete(unit);
			}

			playerDAO.delete(player);
		}
		
		for (Place place : places) {
			placeDAO.delete(place);
		}
	}
}
