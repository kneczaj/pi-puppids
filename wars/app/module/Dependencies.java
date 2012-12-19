package module;


import services.api.LocationTrackingService;
import services.api.MapInfoService;
import services.api.ProfileService;
import services.api.ResourceService;
import services.api.ScoreService;
import services.api.TeamService;
import services.dummy.ResourceServiceDummyImpl;
import services.dummy.ScoreServiceDummyImpl;
import services.impl.LocationTrackingServiceImpl;
import services.impl.MapInfoServiceImpl;
import services.impl.ProfileServiceImpl;
import services.impl.TeamServiceImpl;

import com.google.code.morphia.Morphia;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.mongodb.Mongo;

import daos.AccessTokenDAO;
import daos.CityDAO;
import daos.FactionDAO;
import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.PlayerLocationDAO;
import daos.ResourceDepotDAO;
import daos.TeamDAO;
import daos.InvitationDAO;
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
		binder.bind(MapInfoService.class).to(MapInfoServiceImpl.class).in(Singleton.class);
		binder.bind(LocationTrackingService.class).to(LocationTrackingServiceImpl.class).in(Singleton.class);
		binder.bind(TeamService.class).to(TeamServiceImpl.class).in(Singleton.class);
		binder.bind(ResourceService.class).to(ResourceServiceDummyImpl.class).in(Singleton.class);
		binder.bind(ScoreService.class).to(ScoreServiceDummyImpl.class).in(Singleton.class);
		binder.bind(ProfileService.class).to(ProfileServiceImpl.class).in(Singleton.class);
	
		binder.bind(Morphia.class);
		binder.bind(Mongo.class);
		
		binder.bind(PlayerDAO.class).in(Singleton.class);
		binder.bind(TeamDAO.class).in(Singleton.class);
		binder.bind(InvitationDAO.class).in(Singleton.class);
		binder.bind(FactionDAO.class).in(Singleton.class);
		binder.bind(CityDAO.class).in(Singleton.class);
		binder.bind(UnitDAO.class).in(Singleton.class);
		binder.bind(AccessTokenDAO.class).in(Singleton.class);
		binder.bind(PlaceDAO.class).in(Singleton.class);
		binder.bind(PlayerLocationDAO.class).in(Singleton.class);
		binder.bind(ResourceDepotDAO.class).in(Singleton.class);
	}

}