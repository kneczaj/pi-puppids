package module;


import play.Play;
import services.api.AuthenticationService;
import services.api.ConqueringService;
import services.api.LocationTrackingService;
import services.api.MapInfoService;
import services.api.PlaceService;
import services.api.PlayerService;
import services.api.ResourceService;
import services.api.ScoreService;
import services.api.TeamService;
import services.api.UnitService;
import services.api.VictoryStrategy;
import services.dummy.ResourceServiceDummyImpl;
import services.dummy.ScoreServiceDummyImpl;
import services.google.places.api.GPlaceService;
import services.google.places.impl.GPlaceServiceImpl;
import services.impl.AuthenticationServiceImpl;
import services.impl.ConqueringServiceImpl;
import services.impl.LocationTrackingServiceImpl;
import services.impl.MapInfoServiceImpl;
import services.impl.PlaceServiceImpl;
import services.impl.PlayerServiceImpl;
import services.impl.ResourceServiceImpl;
import services.impl.TeamServiceImpl;
import services.impl.UnitServiceImpl;
import services.impl.VictoryByNumberOfUnitsStategy;

import com.google.code.morphia.Morphia;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.mongodb.Mongo;

import daos.AccessTokenDAO;
import daos.CityDAO;
import daos.FactionDAO;
import daos.InvitationDAO;
import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.PlayerLocationDAO;
import daos.TeamDAO;
import daos.UnitDAO;

/**
 * Configures Google Guice to inject the proper implementations for certain interfaces.
 * 
 * @author markus
 */
public class Dependencies implements Module {

	@Override
	public void configure(Binder binder) {
		// bind service interfaces to concrete implementations
		binder.bind(AuthenticationService.class).to(AuthenticationServiceImpl.class).in(Singleton.class);
		binder.bind(LocationTrackingService.class).to(LocationTrackingServiceImpl.class).in(Singleton.class);
		binder.bind(MapInfoService.class).to(MapInfoServiceImpl.class).in(Singleton.class);
		binder.bind(PlaceService.class).to(PlaceServiceImpl.class).in(Singleton.class);
		binder.bind(PlayerService.class).to(PlayerServiceImpl.class).in(Singleton.class);
		binder.bind(GPlaceService.class).to(GPlaceServiceImpl.class).in(Singleton.class);
		binder.bind(ConqueringService.class).to(ConqueringServiceImpl.class).in(Singleton.class);
		binder.bind(VictoryStrategy.class).to(VictoryByNumberOfUnitsStategy.class).in(Singleton.class);
		
		if (Play.isTest()) {
			binder.bind(ResourceService.class).to(ResourceServiceImpl.class).in(Singleton.class);
		} else {

			binder.bind(ResourceService.class).to(ResourceServiceDummyImpl.class).in(Singleton.class);
		}
		
		binder.bind(ScoreService.class).to(ScoreServiceDummyImpl.class).in(Singleton.class);
		binder.bind(TeamService.class).to(TeamServiceImpl.class).in(Singleton.class);
		binder.bind(UnitService.class).to(UnitServiceImpl.class).in(Singleton.class);
		
		binder.bind(Morphia.class);
		binder.bind(Mongo.class);
	
		binder.bind(AccessTokenDAO.class).in(Singleton.class);
		binder.bind(CityDAO.class).in(Singleton.class);
		binder.bind(FactionDAO.class).in(Singleton.class);
		binder.bind(InvitationDAO.class).in(Singleton.class);
		binder.bind(PlaceDAO.class).in(Singleton.class);
		binder.bind(PlayerDAO.class).in(Singleton.class);
		binder.bind(PlayerLocationDAO.class).in(Singleton.class);
		binder.bind(TeamDAO.class).in(Singleton.class);
		binder.bind(UnitDAO.class).in(Singleton.class);
	}

}