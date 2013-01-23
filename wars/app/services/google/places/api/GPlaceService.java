package services.google.places.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import models.GPlace;
import models.Location;
import models.PlaceType;

/**
 * Wrapper for easy access from Java to the RESTful Google Places API
 * 
 * @author markus
 */
public interface GPlaceService {

	/**
	 * Get details for a certain Google Place by the reference of it.
	 * 
	 * @param reference
	 *            (https://developers.google.com/maps/documentation/javascript/
	 *            places#place_search_responses)
	 * @return
	 * @throws GPlaceServiceException
	 */
	public GPlace details(String reference) throws GPlaceServiceException;
	
	/**
	 * Perform a RadarSearch with the Google Places API by specifying the place
	 * types to search for within a certain area. Returns just position data
	 * (lat, lng) and the Google Places reference. With the reference ID further
	 * details of a place can be obtained by GPlaceService.details(reference).
	 * 
	 * @param placeTypes
	 * @param location
	 * @param radius
	 * @return a mapping from a Location to its Google Places reference ID
	 *         (https://developers.google.com/maps/documentation/javascript/
	 *         places#place_search_responses)
	 * @throws GPlaceServiceException
	 */
	public Map<Location, String> radarSearchByPlaceTypes(
			List<PlaceType> placeTypes, Location location, int radius)
			throws GPlaceServiceException;
	
	/**
	 * Search for a keyword near a Geolocation
	 * 
	 * @param keyword
	 * @param location
	 * @param radius
	 * @return a list of GPlaces
	 * @throws GPlaceServiceException
	 */
	public ArrayList<GPlace> search(String keyword, Location location,
			int radius) throws GPlaceServiceException;
	
	/**
	 * Fetch a place from google maps by its uuid
	 * 
	 * @param uuid
	 * @return
	 */
	public GPlace getPlace(String uuid);
	
}
