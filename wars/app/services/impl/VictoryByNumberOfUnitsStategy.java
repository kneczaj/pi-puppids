package services.impl;

import java.util.List;
import java.util.Set;

import models.Player;
import models.Unit;
import services.api.VictoryStrategy;

import com.google.common.collect.Lists;

/**
 * A simple implementation of a Victory Strategy. Just compares the number of
 * units the attackers have with the number of units that defend a place.
 * 
 * @author markus
 */
public class VictoryByNumberOfUnitsStategy implements VictoryStrategy {

	@Override
	public boolean doAttackersWin(Set<Player> attackers,
			List<Unit> defendingUnits) {

		List<Unit> undeployedUnits = Lists.newArrayList();

		for (Player p : attackers) {
			List<Unit> pUnits = p.getUnits();
			for (Unit u : pUnits) {
				if (u.getDeployedAt() == null) {
					undeployedUnits.add(u);
				}
			}
		}

		if (undeployedUnits.size() > defendingUnits.size()) {
			return true;
		} else {
			return false;
		}
	}

}
