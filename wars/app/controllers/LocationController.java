package controllers;

import java.util.Date;

import models.Location;
import models.Player;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.LocationTrackingService;
import services.api.error.LocationTrackingServiceException;

import com.google.inject.Inject;

import daos.PlayerDAO;

/**
 * Controller for the LocationTrackingService
 * 
 * @author markus
 */
public class LocationController extends Controller {

	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static LocationTrackingService locationTracking; 
	
	@Inject
	private static PlayerDAO playerDAO;

	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result updateLocation(String lat,
			String lng, String uncertainty, String speed, String timestamp) {
		Player p = authenticationService.getPlayer();
		
		if (p == null) {
			return badRequest();
		}
		Logger.info("updating the Location of player " + p.getEmail());
		
		Location location = new Location(new Double(lng), new Double(lat));
		Integer sp = (speed == null || speed.equals("")) ? null : new Integer(speed);
		Integer accuracy = (uncertainty == null || uncertainty.equals("")) ? null : (new Double(uncertainty)).intValue();
		Date timeStamp = new Date(new Long(timestamp));
		
		try {
			locationTracking.updatePlayerLocation(p, location, timeStamp, accuracy, sp);
			return ok("ok");
		} catch (LocationTrackingServiceException e) {
			Logger.warn("could not update player location", e);
			return internalServerError();
		}
	}
	
}
