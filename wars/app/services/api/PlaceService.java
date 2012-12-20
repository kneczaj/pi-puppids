package services.api;

import java.util.List;

import models.Place;
import models.PlaceType;
import models.Player;
import models.ResourceType;
import models.Unit;
import models.UnitType;

/**
 * Service to get information for places and for deployed units
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

	/**
	 * Retrieve all deployed units belonging to a specific player
	 */
	public List<Unit> getDeployedUnitsOfPlayer(Player player, Place place) throws NullPointerException;
	
	/**
	 * Retrieve all deployed units of a certain UnitType belonging to a specific player 
	 */
	public List<Unit> getDeployedUnitsOfPlayer(Player player, UnitType unitType, Place place) throws NullPointerException;
}
