package services.api;

import java.util.List;

import models.Place;
import models.Player;
import models.Unit;
import models.UnitType;
import services.api.error.UnitServiceException;

/**
 * UnitService, build, deploy and retrieve units
 * 
 * @author michi
 * 
 */
public interface UnitService extends Service {

	/**
	 * Create a new unit of a certain type
	 * 
	 * @param type
	 * @return
	 */
	public Unit getInstance(UnitType type);

	/**
	 * Get a list of undeployed Units of a certain type
	 * @param player
	 * @param unitType
	 * @return
	 */
	public List<Unit> getUndeployedUnits(Player player, UnitType unitType);
	
	/**
	 * Get the total number of all undeployed units of a player
	 * @param player
	 * @return
	 */
	public Integer getNumberOfUndeployedUnits(Player player);
	
	/**
	 * Get the number of all undeployed units of a player of a certain type
	 * @param player
	 * @return
	 */
	public Integer getNumberOfUndeployedUnits(Player player, UnitType unitType);
	
	/**
	 * Get the total number of all deployed units of a player
	 * @param player
	 * @return
	 */
	public Integer getNumberOfDeployedUnits(Player player);
	
	/**
	 * Get the number of all deployed units of a player of a certain type
	 * @param player
	 * @return
	 */
	public Integer getNumberOfDeployedUnits(Player player, UnitType unitType);
	
	/**
	 * Build a unit of a certain type and add it to the player's unit pool
	 * 
	 * @throws UnitServiceException
	 *             Thrown if the player has insufficient funds
	 */
	public void buildUnit(Player player, UnitType type, Integer amount)
			throws UnitServiceException;

	/**
	 * Deploy amount of units of a certain type to a place
	 * 
	 * @throws UnitServiceException
	 *             thrown if the player has not enough units to fulfill the
	 *             request
	 */
	public void deployUnit(Player player, UnitType type, Integer amount,
			Place target) throws UnitServiceException;

	/**
	 * Retrieve amount of units of a certain type from a place
	 * 
	 * @throws UnitServiceException
	 *             thrown if the place has not enough units deployed to fulfill
	 *             the request
	 */
	public void retrieveUnit(Player player, UnitType type, Integer amount,
			Place from) throws UnitServiceException;


}
