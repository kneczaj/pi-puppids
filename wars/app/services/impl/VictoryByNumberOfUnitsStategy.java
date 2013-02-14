package services.impl;

import java.util.List;

import models.Player;
import models.Unit;
import services.api.UnitService;
import services.api.VictoryStrategy;

import com.google.inject.Inject;

/**
 * A simple implementation of a Victory Strategy. Just compares the number of
 * units the attackers have with the number of units that defend a place.
 * 
 * @author markus
 */
public class VictoryByNumberOfUnitsStategy implements VictoryStrategy {

	@Inject
	private UnitService unitService;
	
	@Override
	public boolean doAttackersWin(List<Player> attackers,
			List<Unit> defendingUnits) {
		
		int undeployedUnits = 0; 
				
		for (Player p : attackers) {
			undeployedUnits += unitService.getNumberOfUndeployedUnits(p);
		}

		if (undeployedUnits > defendingUnits.size()) {
			return true;
		} else {
			return false;
		}
	}

}
