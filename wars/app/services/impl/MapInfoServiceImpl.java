package services.impl;

import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
import services.api.MapInfoService;
import services.api.error.MapInfoServiceException;

/**
 * Implementation of the MapInfoService.
 * 
 * @author markus
 */
public class MapInfoServiceImpl implements MapInfoService {
	
	//private PlayerDAO playerDAO = new PlayerDAO();

	@Override
	public Map<Player, Location> findPlayersNearby()
			throws MapInfoServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Place, Location> findPlacesNearby()
			throws MapInfoServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
