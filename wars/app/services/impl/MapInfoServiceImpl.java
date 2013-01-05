package services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
import models.PlayerLocation;
import models.Team;
import services.api.MapInfoService;

import com.google.common.collect.Lists;
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
	public List<Player> findTeamMembersNearby(Team team, Place place, Integer searchRadius) {
		Location location =  new Location(place.getLng(), place.getLat());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -15);
		Date youngerThan = calendar.getTime();
		
		Map<String, PlayerLocation> playersNearby = findPlayersNearby(location, searchRadius, youngerThan);
		List<Player> teamMembersNearby = Lists.newArrayList();
		String teamId = team.getId().toString();
		
		for (PlayerLocation pl : playersNearby.values()) {
			Player player = pl.getPlayer();
			
			// check if the player belongs to the requested team
			if (player.getTeam().getId().toString().equals(teamId)) {
				teamMembersNearby.add(pl.getPlayer());
			}
		}
		
		return teamMembersNearby;
	}
	
	@Override
	public Map<String, PlayerLocation> findPlayersNearby(Location location, Integer searchRadius, Date youngerThan) {
		// TODO: filter players that are not nearby, consider searchRadius
		List<Player> players = playerDAO.find().asList();
		Map<String, PlayerLocation> mapping = Maps.newHashMap();
		
		for (Player p : players) {
			PlayerLocation pl = playerLocationDAO.findLatestLocation(p);
			if (pl == null || pl.getPlayer() == null || pl.getTimestamp().before(youngerThan)) {
				continue;
			}
			mapping.put(pl.getPlayer().getId().toString(), pl);
		}
		
		return mapping;
	}

	@Override
	public Map<Place, Location> findPlacesNearby(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

}
