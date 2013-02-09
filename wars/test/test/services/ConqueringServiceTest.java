package test.services;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import models.City;
import models.Faction;
import models.GPlace;
import models.Location;
import models.Player;
import models.ResourceType;
import models.Team;
import models.conquer.CheckConquerConditionsResult;
import models.conquer.ConqueringAttempt;
import models.conquer.InitiateConquerResult;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import services.api.ConqueringService;
import services.api.LocationTrackingService;
import services.api.PlayerService;
import services.api.error.LocationTrackingServiceException;
import services.api.error.PlayerServiceException;
import services.google.places.api.GPlaceServiceException;
import test.util.InjectorHelper;
import test.util.SamplePlaces;

import com.google.common.collect.Maps;
import com.google.inject.Injector;

import daos.CityDAO;
import daos.ConqueringAttemptDAO;
import daos.FactionDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * 
 * @author markus
 */
public class ConqueringServiceTest {

	private static ConqueringService conqueringService;
	private static PlayerService playerService;

	private static CityDAO cityDAO;
	private static ConqueringAttemptDAO conqueringAttemptDAO;
	private static FactionDAO factionDAO;
	private static TeamDAO teamDAO;
	private static PlayerDAO playerDAO;
	private static LocationTrackingService locationTrackingService;

	private Player player2;
	private Player player1;
	private City city;
	private Faction faction;
	private Faction faction2;
	private Player player3;
	private ConqueringAttempt conqueringAttempt;

	@BeforeClass
	public static void startUp() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);

		conqueringService = injector.getInstance(ConqueringService.class);
		playerService = injector.getInstance(PlayerService.class);
		locationTrackingService = injector
				.getInstance(LocationTrackingService.class);

		cityDAO = injector.getInstance(CityDAO.class);
		conqueringAttemptDAO = injector.getInstance(ConqueringAttemptDAO.class);
		factionDAO = injector.getInstance(FactionDAO.class);
		teamDAO = injector.getInstance(TeamDAO.class);
		playerDAO = injector.getInstance(PlayerDAO.class);
	}

	@Before
	public void before() throws PlayerServiceException {
		player1 = playerService.register("userpass", "test@test.e", "Bob",
				"the Builder", "hahahash", "bobthebuilder1");

		player2 = playerService.register("userpass", "test2@test.e", "Bob2",
				"the Builder", "hahahash", "bobthebuilder2");

		player3 = playerService.register("userpass", "test3@test.e", "Bob 3",
				"the Builder", "hahahash", "bobthebuilder3");
		
		city = new City();
		city.setName("testCity");
		cityDAO.save(city);

		faction = new Faction();
		faction.setName("red");
		factionDAO.save(faction);

		faction2 = new Faction();
		faction2.setName("blue");
		factionDAO.save(faction2);

		// All three players join the same city
		playerService.joinCity(player1, city.getId().toString());
		playerService.joinCity(player2, city.getId().toString());
		playerService.joinCity(player3, city.getId().toString());

		// 2 players choose the red faction the third one the blue faction
		playerService.joinFaction(player1, faction.getId().toString());
		playerService.joinFaction(player2, faction.getId().toString());
		playerService.joinFaction(player3, faction2.getId().toString());
		
		Team team = new Team();
		team.setName("The Fighting Mongooses");
		team.setCity(city);
		teamDAO.save(team);
		
		team.addPlayer(player1);
		team.addPlayer(player2);
		teamDAO.save(team);
		
		player1.setTeam(team);
		player2.setTeam(team);
		
		Map<ResourceType, Integer> resourceDepot = Maps.newHashMap();
		resourceDepot.put(ResourceType.Material, 10000);
		resourceDepot.put(ResourceType.Credits, 10000);
		
		player1.setResourceDepot(resourceDepot);
		player2.setResourceDepot(resourceDepot);
		
		playerDAO.save(player1);
		playerDAO.save(player2);
	}

	/**
	 * Ensure that players that are around a place and that are not part of the
	 * conquering team are not listed as possible participants of a conquering
	 * attempt.
	 * 
	 * @throws LocationTrackingServiceException
	 */
	@Test
	public void getTeamMembersNearbyTest()
			throws LocationTrackingServiceException {

		GPlace townHall = SamplePlaces.newTownHall;

		// check in all the three players near the new town hall in munich
		Location location = new Location();
		location.setLatitude(townHall.getLatitude() - 0.00001);
		location.setLongitude(townHall.getLongitude() - 0.00001);

		locationTrackingService.updatePlayerLocation(player1, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player2, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player3, location,
				new Date(), 5, 0);

		Set<Player> allowedParticipants;
		try {
			allowedParticipants = conqueringService.getTeamMembersNearby(
					player1, townHall.getReference());
			Assert.assertNotNull(allowedParticipants);
			Assert.assertEquals(2, allowedParticipants.size());
		} catch (GPlaceServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			allowedParticipants = conqueringService.getTeamMembersNearby(
					player3, townHall.getReference());
			Assert.assertNotNull(allowedParticipants);
			Assert.assertEquals(1, allowedParticipants.size());
		} catch (GPlaceServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Test
	public void initiateConquerTest() throws LocationTrackingServiceException,
			GPlaceServiceException {
		GPlace townHall = SamplePlaces.newTownHall;

		// check in all the three players near the new town hall in munich
		Location location = new Location();
		location.setLatitude(townHall.getLatitude() - 0.00001);
		location.setLongitude(townHall.getLongitude() - 0.00001);

		locationTrackingService.updatePlayerLocation(player1, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player2, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player3, location,
				new Date(), 5, 0);
		
		InitiateConquerResult result = conqueringService.initiateConquer(
				player1, townHall.getUuid(), townHall.getReference());
		
		Assert.assertNotNull(result);
		Assert.assertEquals(InitiateConquerResult.Type.SUCCESSFUL,
				result.getType());
		Assert.assertNotNull(result.getConqueringAttempt());
	}

	@Test
	public void checkConquerConditionsTest() throws LocationTrackingServiceException, GPlaceServiceException {
		
		GPlace townHall = SamplePlaces.newTownHall;

		// check in all the three players near the new town hall in munich
		Location location = new Location();
		location.setLatitude(townHall.getLatitude() - 0.00001);
		location.setLongitude(townHall.getLongitude() - 0.00001);

		locationTrackingService.updatePlayerLocation(player1, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player2, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player3, location,
				new Date(), 5, 0);
		
		conqueringService.initiateConquer(
				player1, townHall.getUuid(), townHall.getReference());
		
		conqueringAttempt = conqueringAttemptDAO.findOne("uuid", townHall.getUuid());
		
		CheckConquerConditionsResult result = conqueringService.checkConquerConditions(conqueringAttempt.getId().toString(), player1);
	
		Assert.assertNotNull(result);
		Assert.assertEquals(ConqueringService.ConqueringStatus.CONQUER_POSSIBLE, result.getConqueringStatus());
	}
	
	@After
	public void tearDown() {
		for (ConqueringAttempt attempt : conqueringAttemptDAO.find().asList()) {
			conqueringAttemptDAO.delete(attempt);
		}
		teamDAO.delete(player1.getTeam());
		teamDAO.delete(player3.getTeam());
		factionDAO.delete(faction);
		factionDAO.delete(faction2);
		cityDAO.delete(city);
		playerDAO.delete(player1);
		playerDAO.delete(player2);
		playerDAO.delete(player3);
	}

}
