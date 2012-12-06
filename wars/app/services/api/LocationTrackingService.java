package services.api;

import java.util.Date;

import models.Location;
import models.Player;
import services.api.error.LocationTrackingServiceException;

/**
 * Service-Interface for tracking locations
 * 
 * @author markus
 */
public interface LocationTrackingService extends Service {

	/**
	 * Updates the location of a player
	 * 
	 * @param player
	 * @param location
	 * @param timeStamp
	 * @param accuracy
	 * @param speed
	 * @throws LocationTrackingServiceException
	 */
	public void updatePlayerLocation(Player player, Location location,
			Date timeStamp, Integer accuracy, Integer speed)
			throws LocationTrackingServiceException;

}
