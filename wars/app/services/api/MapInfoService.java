package services.api;

import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
import services.api.error.MapInfoServiceException;

/**
 * MapInfoService: 
 *  - find players nearby
 *  - find places nearby
 * 
 * @author markus
 */
public interface MapInfoService {
	
	public Map<Player, Location> findPlayersNearby() throws MapInfoServiceException;
	
	public Map<Place, Location> findPlacesNearby() throws MapInfoServiceException;

}
