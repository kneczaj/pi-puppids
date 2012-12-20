package services.api;

import models.Unit;
import models.UnitType;

/**
 * UnitService, build, deploy and retrieve units
 * @author michi
 *
 */
public interface UnitService extends Service {

	/**
	 * Create a new unit of a certain type
	 * @param type
	 * @return
	 */
	public Unit getInstance(UnitType type);
}
