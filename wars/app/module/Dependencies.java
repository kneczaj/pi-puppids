package module;


import services.api.LocationTrackingService;
import services.api.MapInfoService;
import services.impl.LocationTrackingServiceImpl;
import services.impl.MapInfoServiceImpl;

import com.google.code.morphia.Morphia;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;
import com.mongodb.Mongo;

import daos.AccessTokenDAO;
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
		binder.bind(MapInfoService.class).to(MapInfoServiceImpl.class).in(Singleton.class);
		binder.bind(LocationTrackingService.class).to(LocationTrackingServiceImpl.class).in(Singleton.class);
	
		binder.bind(Morphia.class);
		binder.bind(Mongo.class);
		
		binder.bind(PlayerDAO.class).in(Singleton.class);
		binder.bind(TeamDAO.class).in(Singleton.class);
		binder.bind(UnitDAO.class).in(Singleton.class);
		binder.bind(AccessTokenDAO.class).in(Singleton.class);
		binder.bind(PlaceDAO.class).in(Singleton.class);
		binder.bind(PlayerLocationDAO.class).in(Singleton.class);
	}

}