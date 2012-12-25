package test.services;

import models.City;
import models.Faction;
import models.Player;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import services.api.PlayerService;
import services.api.error.PlayerServiceException;
import test.util.InjectorHelper;

import com.google.inject.Injector;

import daos.CityDAO;
import daos.FactionDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Tests for the PlayerService
 * 
 * @author markus
 */
public class PlayerServiceTest {

	private static PlayerService playerService;
	private static CityDAO cityDAO;
	private static PlayerDAO playerDAO;
	private static TeamDAO teamDAO;
	private static FactionDAO factionDAO;

	private static Player player;
	private static City city;
	private static City city2;
	private static Faction faction;
	private static Faction faction2;

	@BeforeClass
	public static void startUp() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);

		playerService = injector.getInstance(PlayerService.class);
		cityDAO = injector.getInstance(CityDAO.class);
		factionDAO = injector.getInstance(FactionDAO.class);
		playerDAO = injector.getInstance(PlayerDAO.class);
		teamDAO = injector.getInstance(TeamDAO.class);
	}

	@Before
	public void before() throws PlayerServiceException {
		player = playerService.register("userpass", "test@test.e", "Bob",
				"the Builder", "hahahash", "bobthebuilder");
		Assert.assertNotNull(player);
		Assert.assertTrue(player.getUsername().equals("bobthebuilder"));

		city = new City();
		city.setName("testCity");
		cityDAO.save(city);

		city2 = new City();
		city2.setName("testCity2");
		cityDAO.save(city2);

		faction = new Faction();
		faction.setName("testfaction");
		factionDAO.save(faction);

		faction2 = new Faction();
		faction2.setName("testfaction2");
		factionDAO.save(faction2);
	}

	/**
	 * Basic test for joining a city:
	 * If a player hasn't joined a city yet, joining a city should be possible.
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void joinCityTest() throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getCity());
		Player p = playerService.joinCity(player, city.getId().toString());

		Assert.assertEquals(p.getTeam().getCity().getId().toString(), city
				.getId().toString());
	}

	/**
	 * It should be allowed that a player joins the same city again and again
	 * without an exception being thrown
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void joinCityRejoiningSameCityTest() throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getCity());
		playerService.joinCity(player, city.getId().toString());

		try {
			Player p = playerService.joinCity(player, city.getId().toString());

			Assert.assertEquals(p.getTeam().getCity().getId().toString(), city
					.getId().toString());
		} catch (PlayerServiceException e) {
			Assert.fail("Rejoining the same city should be possible!");
		}
	}

	/**
	 * If a player joined a city x it should be not possible to join a city y
	 * afterwards.
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void joinCityRejoiningDifferentCityTest()
			throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getCity());
		Player p = playerService.joinCity(player, city.getId().toString());

		try {
			playerService.joinCity(p, city2.getId().toString());
			Assert.fail("Players should not be able to join different cities");
		} catch (PlayerServiceException e) {

		}
	}

	/**
	 * Basic test for joining a faction: 
	 * If a player hasn't joined a faction yet, joining a faction should be possible
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void joinFactionTest() throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getFaction());
		Player p = playerService
				.joinFaction(player, faction.getId().toString());

		Assert.assertEquals(p.getTeam().getFaction().getId().toString(),
				faction.getId().toString());
	}
	
	/**
	 * It should be allowed that a player joins the same faction again and again
	 * without an exception being thrown
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void joinFactionRejoiningSameFactionTest() throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getFaction());
		playerService.joinFaction(player, faction.getId().toString());

		try {
			Player p = playerService.joinFaction(player, faction.getId().toString());

			Assert.assertEquals(p.getTeam().getFaction().getId().toString(), faction
					.getId().toString());
		} catch (PlayerServiceException e) {
			Assert.fail("Rejoining the same faction should be possible!");
		}
	}

	/**
	 * If a player joined a faction x it should be not possible to join a faction y
	 * afterwards.
	 * 
	 * @throws PlayerServiceException
	 */
	@Test
	public void joinFactionRejoiningDifferentFactionTest()
			throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getFaction());
		Player p = playerService.joinFaction(player, faction.getId().toString());

		try {
			playerService.joinFaction(p, faction2.getId().toString());
			Assert.fail("Players should not be able to join different factions");
		} catch (PlayerServiceException e) {

		}
	}

	@After
	public void tearDown() {
		teamDAO.delete(player.getTeam());
		factionDAO.delete(faction);
		cityDAO.delete(city);
		playerDAO.delete(player);
	}

}
