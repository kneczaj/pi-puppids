package services.api;

import java.util.Date;
import java.util.Map;

import models.Location;
import models.Place;
import models.PlayerLocation;
import services.api.error.MapInfoServiceException;

/**
 * MapInfoService: - find players nearby - find places nearby
 * 
 * @author markus
 */
public interface MapInfoService extends Service {

	/**
	 * Find players nearby a location.
	 * 
	 * @param location
	 * @param searchRadius
	 * @param youngerThan
	 *            only consider location information that is not older then this
	 *            date
	 * @throws MapInfoServiceException
	 */
	public Map<String, PlayerLocation> findPlayersNearby(Location location,
			Integer searchRadius, Date youngerThan) throws MapInfoServiceException;

	/**
	 * Find places nearby a location.
	 * 
	 * @param location
	 * @throws MapInfoServiceException
	 */
	public Map<Place, Location> findPlacesNearby(Location location)
			throws MapInfoServiceException;

}
