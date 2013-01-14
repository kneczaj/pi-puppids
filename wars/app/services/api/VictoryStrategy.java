package services.api;

import java.util.List;
import java.util.Set;

import models.Player;
import models.Unit;

/**
 * Interface for different strategies to calculate whether the resources of
 * attackers overcome a number of defending units.
 * 
 * @author markus
 */
public interface VictoryStrategy {

	/**
	 * Do the resources and units of the attackers overcome the given defending Units?
	 * 
	 * @param attackers
	 * @param defendingUnits
	 * @return
	 */
	public boolean doAttackersWin(List<Player> attackers,
			List<Unit> defendingUnits);

}
