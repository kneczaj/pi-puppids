package services.impl;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import play.Logger;

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
import daos.PlaceDAO;
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
	private PlaceDAO placeDAO;
	
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
			Logger.info("Player " + pl.getPlayer().getUsername() + " is nearby");
			Player player = pl.getPlayer();
			
			// check if the player belongs to the requested team
			if (player.getTeam().getId().toString().equals(teamId)) {
				Logger.info("Player " + pl.getPlayer().getUsername() + " has the propriate team");
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
			
			double currentDistance = calculateLocationDistance(pl.getLocation(), location);
			Logger.info("Distance between location and " + p.getId().toString() + " is " + currentDistance + "m");
			if (currentDistance <= radius) {
				mapping.put(pl.getPlayer().getId().toString(), pl);
			}
		}
		
		return mapping;
	}

	@Override
	public List<Place> findConqueredPlaces() {
		List<Place> places = placeDAO.find().asList();
		for (Place p : places) {
			if (p == null || p.getConqueredBy().isEmpty()) {
				places.remove(p);
			}
		}
		return places;
	}

}
