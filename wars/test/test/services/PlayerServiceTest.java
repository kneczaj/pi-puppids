package test.services;


import models.City;
import models.Faction;
import models.Player;

import org.junit.AfterClass;
import org.junit.Assert;
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
	private static Faction faction;
	
	@BeforeClass
	public static void startUp() throws PlayerServiceException {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);
		
	    playerService = injector.getInstance(PlayerService.class);
	    cityDAO = injector.getInstance(CityDAO.class);
	    factionDAO = injector.getInstance(FactionDAO.class);
	    playerDAO = injector.getInstance(PlayerDAO.class);
	    teamDAO = injector.getInstance(TeamDAO.class);
	    
	    player = playerService.register("userpass", "test@test.e", "Bob", "the Builder", "hahahash", "bobthebuilder");
	    Assert.assertNotNull(player);
	    Assert.assertTrue(player.getUsername().equals("bobthebuilder"));
	    
	    city = new City();
		city.setName("testCity");
		cityDAO.save(city);
		
		faction = new Faction();
		faction.setName("testfaction");
		factionDAO.save(faction);
	}
	
	@Test
	public void joinCityTest() throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getCity());
		Player p = playerService.joinCity(player, city.getId().toString());
		
		Assert.assertEquals(p.getTeam().getCity().getId().toString(), city.getId().toString());
	}
	
	@Test
	public void joinFactionTest() throws PlayerServiceException {
		Assert.assertNull(player.getTeam().getFaction());
		Player p = playerService.joinFaction(player, faction.getId().toString());
		
		Assert.assertEquals(p.getTeam().getFaction().getId().toString(), faction.getId().toString());
	}
	
	@AfterClass
	public static void tearDown() {
		teamDAO.delete(player.getTeam());
		factionDAO.delete(faction);
		cityDAO.delete(city);
		playerDAO.delete(player);
	}

}
