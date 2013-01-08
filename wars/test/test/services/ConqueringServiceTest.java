package test.services;

import java.util.Date;
import java.util.List;

import models.City;
import models.Faction;
import models.Location;
import models.Place;
import models.Player;

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
import test.util.InjectorHelper;
import test.util.SampleLocations;

import com.google.inject.Injector;

import daos.CityDAO;
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

	@BeforeClass
	public static void startUp() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);

		conqueringService = injector.getInstance(ConqueringService.class);
		playerService = injector.getInstance(PlayerService.class);
		locationTrackingService = injector
				.getInstance(LocationTrackingService.class);

		cityDAO = injector.getInstance(CityDAO.class);
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

		// 2 players have the same team
		teamDAO.delete(player2.getTeam());
		player2.setTeam(player1.getTeam());
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
	public void getTeamMembersAllowedToParticipateInConquerTest()
			throws LocationTrackingServiceException {
		Location munich = SampleLocations.MUNICH;
		Place munichsCenter = new Place();
		munichsCenter.setLat(munich.getLatitude());
		munichsCenter.setLng(munich.getLongitude());

		// check in all the three players near the center of munich
		Location location = new Location();
		location.setLatitude(munich.getLatitude() - 0.00001);
		location.setLongitude(munich.getLongitude() - 0.00001);

		locationTrackingService.updatePlayerLocation(player1, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player2, location,
				new Date(), 5, 0);
		locationTrackingService.updatePlayerLocation(player3, location,
				new Date(), 5, 0);

		List<Player> allowedParticipants = conqueringService
				.getTeamMembersAllowedToParticipateInConquer(player1,
						munichsCenter);
		Assert.assertNotNull(allowedParticipants);
		Assert.assertEquals(2, allowedParticipants.size());

		allowedParticipants = conqueringService
				.getTeamMembersAllowedToParticipateInConquer(player3,
						munichsCenter);
		Assert.assertNotNull(allowedParticipants);
		Assert.assertEquals(1, allowedParticipants.size());
	}

	@After
	public void tearDown() {
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
