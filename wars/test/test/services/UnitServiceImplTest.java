package test.services;

import java.util.List;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Unit;
import models.UnitType;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
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
	private static Player player;
	private static Place tum;

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
	}

	@Before
	public void setup() {
		System.out.println("Setting up");
		// persist a player
		player = new Player();
		player.setUsername("mrbob");
		player.setEmail("bob@bobson.de");
		player.setName("Bobson");
		player.setFirstname("Bob");

		// give the player some resources
		player.getResourceDepot().put(ResourceType.Credits, 1000);
		player.getResourceDepot().put(ResourceType.Material, 1500);
		player.getResourceDepot().put(ResourceType.Food, 10);
		player.getResourceDepot().put(ResourceType.Transportation, 80);

		playerDAO.save(player);

		// conquer a place
		// TUM was conquered by bob
		tum = SamplePlaces.generateTUM();
		tum.getConqueredBy().add(player);

		placeDAO.save(tum);

		System.out.println("Player: " + player.getId());
		System.out.println("Tum: " + tum.getId());

		// Bob has conquered the TUM
		player.getConquered().add(tum);

		playerDAO.save(player);

		Assert.assertNotNull(player);
		Assert.assertEquals(new Integer(1000),
				player.getResourceDepot(ResourceType.Credits));
		Assert.assertEquals(new Integer(1500),
				player.getResourceDepot(ResourceType.Material));
		Assert.assertEquals(new Integer(10),
				player.getResourceDepot(ResourceType.Food));
		Assert.assertEquals(new Integer(80), player.getResourceDepot(ResourceType.Transportation));
		Assert.assertEquals(1, player.getConquered().size());
	}

	@Test
	public void buildUnitTest() {

		try {
			unitService.buildUnit(player, UnitType.GRUNT, 5);
		} catch (UnitServiceException e) {
			e.printStackTrace();
		}

		player = playerDAO.findOne("username", "mrbob");

		Assert.assertNotNull(player.getUnits());
		Assert.assertNotNull(player.getResourceDepot(ResourceType.Credits));
		Assert.assertNotNull(player.getResourceDepot(ResourceType.Material));
		Assert.assertEquals(5, player.getUnits().size());
		Assert.assertEquals(new Integer(500),
				player.getResourceDepot(ResourceType.Credits));
		Assert.assertEquals(new Integer(750),
				player.getResourceDepot(ResourceType.Material));
	}

	@Test
	public void deployableUnitsTest() {

		// Build a few units
		try {
			unitService.buildUnit(player, UnitType.GRUNT, 5);
		} catch (UnitServiceException e) {
			e.printStackTrace();
		}

		System.out.println("Player: " + player.getId());
		System.out.println("Tum: " + tum.getId());

		Integer deployableUnits = unitService
				.getNumberOfUndeployedUnits(player);
		Integer deployableGrunts = unitService.getNumberOfUndeployedUnits(
				player, UnitType.GRUNT);

		Assert.assertNotNull(player);
		Assert.assertNotNull(deployableUnits);
		Assert.assertNotNull(deployableGrunts);
		Assert.assertEquals(new Integer(5), deployableUnits);
		Assert.assertEquals(new Integer(5), deployableGrunts);
	}

	@Test
	public void deployUnitTest() {
		// build a few units
		try {
			unitService.buildUnit(player, UnitType.GRUNT, 5);
			unitService.deployUnit(player, UnitType.GRUNT, 3, tum);
		} catch (UnitServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player = playerDAO.findOne("username", "mrbob");
		tum = placeDAO.findOne("name", "Technische Universität München");

		Assert.assertEquals(3, tum.getDeployedUnits().size());
		Assert.assertEquals(new Integer(2),
				unitService.getNumberOfUndeployedUnits(player));
		Assert.assertEquals(new Integer(3),
				unitService.getNumberOfDeployedUnits(player));
	}

	@Test
	public void runningDeploymentsTest() {
		// build a few units
		try {
			unitService.buildUnit(player, UnitType.GRUNT, 5);
			unitService.deployUnit(player, UnitType.GRUNT, 3, tum);
		} catch (UnitServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		player = playerDAO.findOne("username", "mrbob");
		Integer runningDeployments = unitService.getRunningDeployments(player).size();

		Assert.assertEquals(new Integer(2),
				unitService.getNumberOfUndeployedUnits(player));
		Assert.assertEquals(new Integer(3),
				runningDeployments);
	}

	@Test
	public void retrieveUnitTest() {

		try {
			unitService.buildUnit(player, UnitType.GRUNT, 5);
			unitService.deployUnit(player, UnitType.GRUNT, 3, tum);
			unitService.undeployUnit(player, UnitType.GRUNT, 2, tum);
		} catch (UnitServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		tum = placeDAO.findOne("name", "Technische Universität München");

		Assert.assertEquals(1, tum.getDeployedUnits().size());
		Assert.assertEquals(new Integer(4),
				unitService.getNumberOfUndeployedUnits(player));
		Assert.assertEquals(new Integer(1),
				unitService.getNumberOfDeployedUnits(player));
	}

	/**
	 * Delete all instances from the database
	 * 
	 * @author michi
	 */
	@After
	public void tearDown() {
		System.out.println("Tearing down");
		List<Unit> units = unitDAO.find().asList();

		for (Unit unit : units) {
			System.out.println("Deleting units..");
			unitDAO.delete(unit);
		}

		placeDAO.delete(tum);
		playerDAO.delete(player);
	}
}
