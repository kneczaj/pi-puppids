package test.services;

import java.util.List;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Unit;
import models.UnitType;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import services.api.PlaceService;
import services.api.UnitService;
import services.api.error.UnitServiceException;
import services.impl.PlaceServiceImpl;
import services.impl.UnitServiceImpl;
import test.util.InjectorHelper;
import test.util.SamplePlaces;

import com.google.inject.Injector;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.UnitDAO;

public class UnitServiceImplTest {

	private static PlayerDAO playerDAO;
	private static PlaceDAO placeDAO;
	private static UnitDAO unitDAO;
	private static PlaceService placeService;
	private static UnitService unitService;

	@BeforeClass
	public static void before() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);

		playerDAO = injector.getInstance(PlayerDAO.class);
		placeDAO = injector.getInstance(PlaceDAO.class);
		unitDAO = injector.getInstance(UnitDAO.class);
		placeService = injector.getInstance(PlaceServiceImpl.class);
		unitService = injector.getInstance(UnitServiceImpl.class);

		Assert.assertNotNull(placeService);
		Assert.assertNotNull(playerDAO);
		Assert.assertNotNull(placeDAO);
		Assert.assertNotNull(unitDAO);
		Assert.assertNotNull(unitService);

		// Generate testing data

		// persist a player
		Player player = new Player();
		player.setUsername("mrbob");
		player.setEmail("bob@bobson.de");
		player.setName("Bobson");
		player.setFirstname("Bob");

		// give the player some resources
		player.getResourceDepot().put(ResourceType.Credits, 1000);
		player.getResourceDepot().put(ResourceType.Material, 1500);

		playerDAO.save(player);
		player = playerDAO.findOne("username", "mrbob");
		
		// conquer a place
		// TUM was conquered by bob
		Place tum = SamplePlaces.tum;
		tum.getConqueredBy().add(player);
		
		placeDAO.save(tum);

		// Bob has conquered the TUM
		player.getConquered().add(tum);

		playerDAO.save(player);
		

		Player load = playerDAO.findOne("username", "mrbob");
		Assert.assertNotNull(load);
		Assert.assertEquals(new Integer(1000),
				load.getResourceDepot(ResourceType.Credits));
		Assert.assertEquals(new Integer(1500),
				load.getResourceDepot(ResourceType.Material));

	}

	@Test
	public void buildUnitTest() {
		Player load = playerDAO.findOne("username", "mrbob");

		try {
			unitService.buildUnit(load, UnitType.GRUNT, 5);
		} catch (UnitServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		load = playerDAO.findOne("username", "mrbob");

		Assert.assertNotNull(load.getUnits());
		Assert.assertNotNull(load.getResourceDepot(ResourceType.Credits));
		Assert.assertNotNull(load.getResourceDepot(ResourceType.Material));
		Assert.assertEquals(5, load.getUnits().size());
		Assert.assertEquals(new Integer(500),
				load.getResourceDepot(ResourceType.Credits));
		Assert.assertEquals(new Integer(750),
				load.getResourceDepot(ResourceType.Material));
	}

	@Test
	public void deployableUnitsTest() {
		Player load = playerDAO.findOne("username", "mrbob");

		Integer deployableUnits = unitService.getNumberOfUndeployedUnits(load);
		Integer deployableGrunts = unitService.getNumberOfUndeployedUnits(load,
				UnitType.GRUNT);

		Assert.assertNotNull(load);
		Assert.assertNotNull(deployableUnits);
		Assert.assertNotNull(deployableGrunts);
		Assert.assertEquals(new Integer(5), deployableUnits);
		Assert.assertEquals(new Integer(5), deployableGrunts);
	}

	@Test
	public void deployUnitTest() {
		Player load = playerDAO.findOne("username", "mrbob");

		// Retriving a place by name is good for now
		Place tum = placeDAO.findOne("name", "Technische Universität München");

		try {
			unitService.deployUnit(load, UnitType.GRUNT, 3, tum);
		} catch (UnitServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		load = playerDAO.findOne("username", "mrbob");
		tum = placeDAO.findOne("name", "Technische Universität München");

		Assert.assertEquals(3, tum.getDeployedUnits().size());
		Assert.assertEquals(new Integer(2),
				unitService.getNumberOfUndeployedUnits(load));
		Assert.assertEquals(new Integer(3),
				unitService.getNumberOfDeployedUnits(load));
	}
	
	@Test
	public void retrieveUnitTest() {
		Player load = playerDAO.findOne("username", "mrbob");

		// Retriving a place by name is good for now
		Place tum = placeDAO.findOne("name", "Technische Universität München");
		
		try {
			unitService.undeployUnit(load, UnitType.GRUNT, 2, tum);
		} catch (UnitServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		tum = placeDAO.findOne("name", "Technische Universität München");
		
		Assert.assertEquals(1, tum.getDeployedUnits().size());
		Assert.assertEquals(new Integer(4),
				unitService.getNumberOfUndeployedUnits(load));
		Assert.assertEquals(new Integer(1),
				unitService.getNumberOfDeployedUnits(load));
	}

	/**
	 * Delete all instances from the database
	 * 
	 * @author michi
	 */
	@AfterClass
	public static void cleanDatabase() {
		List<Player> players = playerDAO.find().asList();
		List<Place> places = placeDAO.find().asList();
		List<Unit> units = unitDAO.find().asList();

		for (Player player : players) {
			playerDAO.delete(player);
		}

		for (Place place : places) {
			placeDAO.delete(place);
		}
		
		for (Unit unit : units) {
			unitDAO.delete(unit);
		}
	}
}
