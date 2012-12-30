package services.impl;

import assets.constants.UnitMappings;
import models.Unit;
import models.UnitType;
import services.api.UnitService;

public class UnitServiceImpl implements UnitService {

	@Override
	public Unit getInstance(UnitType type) {
		Unit unit = new Unit();

		switch (type) {
		case GRUNT:
			unit.setName(UnitMappings.UNIT_NAME_MAP.get(type));
			unit.setCosts(UnitMappings.UNIT_COST_MAP.get(type));
			unit.setChanceOfFailure(0.1);
			unit.setMinStrength(3);
			unit.setMaxStrength(6);
			unit.setType(type);

		case INFANTRY:
			unit.setName(UnitMappings.UNIT_NAME_MAP.get(type));
			unit.setCosts(UnitMappings.UNIT_COST_MAP.get(type));
			unit.setChanceOfFailure(0.05);
			unit.setMinStrength(2);
			unit.setMaxStrength(5);
			unit.setType(type);
		}

		return unit;
	}

}
