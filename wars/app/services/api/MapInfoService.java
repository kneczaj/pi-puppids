package services.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import models.Location;
import models.Place;
import models.Player;
import models.PlayerLocation;
import models.Team;
import services.google.places.api.GPlaceServiceException;

/**
 * MapInfoService: - find players nearby - find places nearby
 * 
 * @author markus
 */
public interface MapInfoService extends Service {

	/**
	 * Find members of a team that are around a place. Thereby, search within a
	 * given search radius. Only players that have updated their position within
	 * the last 15 minutes to a location nearby a place are going to be
	 * considered.
	 * 
	 * @param team
	 *            the team whose members are considered during the search
	 * @param reference
	 *            the reference of the google maps place around which to search
	 * @param searchRadius
	 *            a radius (in meters) around the place where team members are
	 *            considered as being nearby
	 * @return a list of team members near a place
	 */
	public List<Player> findTeamMembersNearby(Team team, String reference,
			Integer searchRadius) throws GPlaceServiceException;

	/**
	 * Find players nearby a location.
	 * 
	 * @param location
	 * @param searchRadius
	 * @param youngerThan
	 *            only consider location information that is not older then this
	 *            date
	 */
	public Map<String, PlayerLocation> findPlayersNearby(Location location,
			Integer searchRadius, Date youngerThan);

	/**
	 * Find all conquered places.
	 * 
	 */
	public List<Place> findConqueredPlaces();

}
