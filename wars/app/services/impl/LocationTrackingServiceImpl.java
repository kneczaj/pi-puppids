package services.impl;

import java.util.Date;

import services.api.LocationTrackingService;
import services.api.error.LocationTrackingServiceException;

import models.Location;
import models.Player;
import models.PlayerLocation;

import com.google.inject.Inject;
import communication.ClientPushActor;

import daos.PlayerLocationDAO;

/**
 * Implementation of the LocationTrackingService.
 * 
 * @author markus
 */
public class LocationTrackingServiceImpl implements LocationTrackingService {

	@Inject
	private static PlayerLocationDAO locationDAO;

	@Override
	public void updatePlayerLocation(Player player, Location location,
			Date timeStamp, Integer accuracy, Integer speed)
			throws LocationTrackingServiceException {
		
		PlayerLocation pl = new PlayerLocation();
		pl.setPlayer(player);
		pl.setLatitude(location.getLatitude());
		pl.setLongitude(location.getLatitude());
		pl.setUncertainty(accuracy);
		pl.setSpeed(speed);
		pl.setTimestamp(timeStamp);
		
		locationDAO.save(pl);
		
		ClientPushActor.playerLocationChanged(pl);
	}
	

}
