package services.impl;

import java.util.Date;

import models.Location;
import models.Player;
import models.PlayerLocation;
import services.api.LocationTrackingService;
import services.api.WebSocketCommunicationService;
import services.api.error.LocationTrackingServiceException;

import com.google.inject.Inject;

import daos.PlayerLocationDAO;

/**
 * Implementation of the LocationTrackingService.
 * 
 * @author markus
 */
public class LocationTrackingServiceImpl implements LocationTrackingService {

	@Inject
	private PlayerLocationDAO locationDAO;
	
	@Inject
	private WebSocketCommunicationService webSocketCommunicationService;

	@Override
	public void updatePlayerLocation(Player player, Location location,
			Date timeStamp, Integer accuracy, Integer speed)
			throws LocationTrackingServiceException {
		
		PlayerLocation pl = new PlayerLocation();
		pl.setPlayer(player);
		pl.setLatitude(location.getLatitude());
		pl.setLongitude(location.getLongitude());
		pl.setUncertainty(accuracy);
		pl.setSpeed(speed);
		pl.setTimestamp(timeStamp);
		
		locationDAO.save(pl);
		
		webSocketCommunicationService.playerLocationChanged(pl);
	}
	

}
