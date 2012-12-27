package services.impl;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceDepot;
import models.ResourceType;
import models.Unit;
import models.UnitType;
import services.api.ResourceService;
import services.api.UnitService;
import services.api.error.ResourceServiceException;
import services.api.error.UnitServiceException;
import assets.constants.UnitMappings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.ResourceDepotDAO;
import daos.UnitDAO;

public class UnitServiceImpl implements UnitService {

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private UnitDAO unitDAO;

	@Inject
	private ResourceDepotDAO resourceDepotDAO;

	@Inject
	private ResourceService resourceService;

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

	@Override
	public void buildUnit(Player player, UnitType type, Integer amount)
			throws UnitServiceException {

		List<Unit> units = Lists.newLinkedList();
		for (int i = 0; i < amount; i++) {
			units.add(getInstance(type));
		}

		Player load = playerDAO.findOne("username", player.getUsername());
		List<ResourceDepot> resourceDepots = load.getResourceDepots();
		Map<ResourceType, Integer> playerResources = Maps.newHashMap();
		Map<ResourceType, Integer> unitCosts = units.get(0).getCosts();

		try {
			playerResources = resourceService.getResourcesOfPlayer(load);
		} catch (ResourceServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Check funds
		boolean playerHasFunds = true;
		for (ResourceType rType : unitCosts.keySet()) {
			if (playerResources.get(rType) < (unitCosts.get(rType) * amount))
				playerHasFunds = false;
		}

		// Throw exception if funds were insufficient
		if (!playerHasFunds)
			throw new UnitServiceException("Insufficient funds!");

		// Pay for the unit
		for (ResourceDepot depot : resourceDepots) {
			if (unitCosts.containsKey(depot.getResourceType())) {
				Integer costs = unitCosts.get(depot.getResourceType()) * amount;
				depot.setAmount(depot.getAmount() - costs);
				resourceDepotDAO.save(depot);
			}
		}

		// Add the unit to the player's unit pool
		List<Unit> newUnitList = Lists.newLinkedList();
		newUnitList.addAll(units);
		newUnitList.addAll(player.getUnits());
		player.setUnits(newUnitList);
		playerDAO.save(player);

	}

	@Override
	public void deployUnit(Player player, UnitType type, Integer amount,
			Place target) throws UnitServiceException {
		// TODO Auto-generated method stub

	}

	@Override
	public void retrieveUnit(Player player, UnitType type, Integer amount,
			Place from) throws UnitServiceException {
		// TODO Auto-generated method stub

	}

}
