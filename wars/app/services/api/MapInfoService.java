package services.api;

import java.util.Map;

import services.api.error.MapInfoServiceException;


import models.Location;
import models.Place;
import models.Player;

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
	 * @throws MapInfoServiceException
	 */
	public Map<Player, Location> findPlayersNearby(Location location)
			throws MapInfoServiceException;

	/**
	 * Find places nearby a location.
	 * 
	 * @param location
	 * @throws MapInfoServiceException
	 */
	public Map<Place, Location> findPlacesNearby(Location location)
			throws MapInfoServiceException;

}
