package module;

import services.api.LocationTrackingService;
import services.api.MapInfoService;
import services.impl.LocationTrackingServiceImpl;
import services.impl.MapInfoServiceImpl;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Singleton;

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
	}

}