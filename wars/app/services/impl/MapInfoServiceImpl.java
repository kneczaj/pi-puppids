package services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import models.GPlace;
import models.Location;
import models.Place;
import models.Player;
import models.PlayerLocation;
import models.Team;
import services.api.MapInfoService;
import services.google.places.api.GPlaceService;
import services.google.places.api.GPlaceServiceException;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.GPlaceDAO;
import daos.PlayerDAO;
import daos.PlayerLocationDAO;

/**
 * Implementation of the MapInfoService.
 * 
 * @author markus
 */
public class MapInfoServiceImpl implements MapInfoService {

	@Inject GPlaceDAO gPlaceDAO;
	
	@Inject
	private PlayerDAO playerDAO;
	
	@Inject
	private PlayerLocationDAO playerLocationDAO;
	
	@Inject
	private GPlaceService gPlaceService;
	
	@Override
	public List<Player> findTeamMembersNearby(Team team, String reference, Integer searchRadius) throws GPlaceServiceException {
		GPlace place = gPlaceService.details(reference);
		Location location =  new Location(place.getLongitude(), place.getLatitude());
		
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
	
	public double calculateLocationDistance(Location location1, Location location2) {
		return Math.acos(Math.sin(location1.getLatitudeRadians()) * Math.sin(location2.getLatitudeRadians()) +
				Math.cos(location1.getLatitudeRadians()) * Math.cos(location2.getLatitudeRadians()) *
				Math.cos(location1.getLongitudeRadians() - location2.getLongitudeRadians())) * 6371010;
	}
	
	@Override
	public Map<String, PlayerLocation> findPlayersNearby(Location location, Integer searchRadius, Date youngerThan) {
		List<Player> players = playerDAO.find().asList();
		Map<String, PlayerLocation> mapping = Maps.newHashMap();
		double radius = (double) searchRadius;
		
		for (Player p : players) {
			PlayerLocation pl = playerLocationDAO.findLatestLocation(p);
			if (pl == null || pl.getPlayer() == null || pl.getTimestamp().before(youngerThan)) {
				continue;
			}
			
			if (calculateLocationDistance(pl.getLocation(), location) <= radius) {
				mapping.put(pl.getPlayer().getId().toString(), pl);
			}
		}
		
		return mapping;
	}

	@Override
	public Map<Place, Location> findPlacesNearby(Location location) {
		// TODO Auto-generated method stub
		return null;
	}

}
