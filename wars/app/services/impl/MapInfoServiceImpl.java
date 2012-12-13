package services.impl;

import java.util.List;
import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
import models.PlayerLocation;
import play.Logger;
import services.api.MapInfoService;
import services.api.error.MapInfoServiceException;

import com.google.code.morphia.query.Query;
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
	public Map<Player, Location> findPlayersNearby(Location location)
			throws MapInfoServiceException {
		// TODO: filter players that are not nearby
		List<Player> players = playerDAO.find().asList();
		Map<Player, Location> mapping = Maps.newHashMap();
		
		for (Player p : players) {
			Query<PlayerLocation> query = playerLocationDAO.createQuery().field("player").equal(p).order("timestamp").limit(1);
			PlayerLocation pl = playerLocationDAO.findOne(query);
			if (pl == null) {
				continue;
			}
			Location l = new Location(pl.getLongitude(), pl.getLatitude());
			Logger.info(l.toString() + " " + pl.getTimestamp());
			mapping.put(pl.getPlayer(), l);
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
