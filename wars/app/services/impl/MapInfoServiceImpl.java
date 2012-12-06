package services.impl;

import java.util.Map;

import services.api.MapInfoService;
import services.api.error.MapInfoServiceException;


import models.Location;
import models.Place;
import models.Player;

/**
 * Implementation of the MapInfoService.
 * 
 * @author markus
 */
public class MapInfoServiceImpl implements MapInfoService {

	@Override
	public Map<Player, Location> findPlayersNearby(Location location)
			throws MapInfoServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Place, Location> findPlacesNearby(Location location)
			throws MapInfoServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
