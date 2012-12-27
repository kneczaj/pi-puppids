package test.services;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import services.api.TeamService;
import test.util.InjectorHelper;

import com.google.inject.Injector;

import daos.PlayerDAO;

public class TeamServiceTest {
	
	private TeamService teamService;
	private PlayerDAO playerDAO;
	
	@Before
	public void startUp() {
		Injector injector = InjectorHelper.getInjector();
		Assert.assertNotNull(injector);
		
	    teamService = injector.getInstance(TeamService.class);
	    playerDAO = injector.getInstance(PlayerDAO.class);
		Assert.assertNotNull(teamService);
	}
	
	@Test
	public void joinCityTest() {

	}

}
