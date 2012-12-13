package services.impl;

import java.util.List;
import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
import models.PlayerLocation;
import services.api.MapInfoService;
import services.api.error.MapInfoServiceException;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.PlayerLocationDAO;

/**
 * Implementation of the MapInfoService.
 * 
 * @author markus
 */
public class MapInfoServiceImpl implements MapInfoService {

	@Inject
	private PlayerDAO playerDAO;
	
	@Inject
	private PlayerLocationDAO playerLocationDAO;
	
	@Override
	public Map<String, PlayerLocation> findPlayersNearby(Location location)
			throws MapInfoServiceException {
		// TODO: filter players that are not nearby
		List<Player> players = playerDAO.find().asList();
		Map<String, PlayerLocation> mapping = Maps.newHashMap();
		
		for (Player p : players) {
			PlayerLocation pl = playerLocationDAO.findLatestLocation(p);
			if (pl == null || pl.getPlayer() == null) {
				continue;
			}
			mapping.put(pl.getPlayer().getId().toString(), pl);
		}
		
		return mapping;
	}

	@Override
	public Map<Place, Location> findPlacesNearby(Location location)
			throws MapInfoServiceException {
		// TODO Auto-generated method stub
		return null;
	}

}
