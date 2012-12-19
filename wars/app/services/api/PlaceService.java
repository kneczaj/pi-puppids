package services.api;

import models.PlaceType;
import models.ResourceType;

/**
 * Service to get information for places not in the db
 * 
 * @author michi
 * 
 */
public interface PlaceService extends Service {

	/**
	 * Fetch the ResourceType according to the type of the place
	 */
	public ResourceType getResourceType(PlaceType placeType);

	/**
	 * Fetch the numeric value of the resource amount according to the type of
	 * the place
	 */
	public Integer getResourceAmount(PlaceType placeType);
}
