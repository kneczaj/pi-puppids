package services.impl;

import java.util.List;

import models.Player;
import models.Unit;
import models.UnitType;

import org.apache.commons.math.MathException;
import org.apache.commons.math.random.RandomDataImpl;

import play.Logger;
import services.api.UnitService;
import services.api.VictoryStrategy;

import com.google.inject.Inject;

import daos.UnitDAO;

/**
 * Victory by strength strategy implementation Uses random values from a uniform
 * distribution between min and max strength of each unit
 * 
 * @author markus
 */
public class VictoryByStrengthStrategy implements VictoryStrategy {

	@Inject
	private UnitService unitService;

	private RandomDataImpl random = new RandomDataImpl();
	
	@Inject
	private UnitDAO unitDAO;

	private double strengthOfUnits(List<Unit> units) {
		double strength = 0;
		for (Unit u : units) {
			try {
				int unitFailure = random.nextBinomial(1, u.getChanceOfFailure());
				if (unitFailure == 1) {
					// TODO: test if unit needs also be removed in Player.units
					
					unitDAO.delete(u);
					strength += 0;
				} else {
					strength += random.nextUniform(u.getMinStrength(),
						u.getMaxStrength());
				}
			} catch (MathException e) {
				Logger.warn("bad things are happening here.", e);
			}
		}
		

		return strength;
	}

	@Override
	public boolean doAttackersWin(List<Player> attackers,
			List<Unit> defendingUnits) {

		int strength = 0;
		for (Player attacker : attackers) {
			for (UnitType unitType : UnitType.values()) {
				List<Unit> units = unitService.getUndeployedUnits(attacker,
						unitType);
				strength += strengthOfUnits(units);
			}
		}

		return (strength > strengthOfUnits(defendingUnits));
	}

}
