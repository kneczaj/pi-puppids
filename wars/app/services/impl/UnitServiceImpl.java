package services.impl;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Unit;
import models.UnitType;
import services.api.PlaceService;
import services.api.ResourceService;
import services.api.UnitService;
import services.api.error.UnitServiceException;
import assets.constants.UnitMappings;

import com.google.code.morphia.query.Query;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.UnitDAO;

public class UnitServiceImpl implements UnitService {

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private PlaceDAO placeDAO;

	@Inject
	private UnitDAO unitDAO;

	@Inject
	private ResourceService resourceService;

	@Inject
	private PlaceService placeService;

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
	public List<Unit> getUndeployedUnits(Player player, UnitType unitType) {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(query.criteria("deployedAt").equal(null),
				query.criteria("player").equal(player), query.criteria("type")
						.equal(unitType));
		List<Unit> undeployedUnits = unitDAO.find(query).asList();

		return undeployedUnits;
	}

	@Override
	public Integer getNumberOfUndeployedUnits(Player player) {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(query.criteria("deployedAt").equal(null),
				query.criteria("player").equal(player));
		Integer undeployedUnits = Long.valueOf(unitDAO.find(query).countAll())
				.intValue();

		return undeployedUnits;
	}

	@Override
	public Integer getNumberOfUndeployedUnits(Player player, UnitType unitType) {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(query.criteria("deployedAt").equal(null),
				query.criteria("player").equal(player), query.criteria("type")
						.equal(unitType));
		Integer undeployedUnits = Long.valueOf(unitDAO.find(query).countAll())
				.intValue();

		return undeployedUnits;
	}

	@Override
	public Integer getNumberOfDeployedUnits(Player player) {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(query.criteria("deployedAt").notEqual(null),
				query.criteria("player").equal(player));
		Integer deployedUnits = Long.valueOf(unitDAO.find(query).countAll())
				.intValue();

		return deployedUnits;
	}

	@Override
	public Integer getNumberOfDeployedUnits(Player player, UnitType unitType) {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(query.criteria("deployedAt").notEqual(null),
				query.criteria("player").equal(player), query.criteria("type")
						.equal(unitType));
		Integer deployedUnits = Long.valueOf(unitDAO.find(query).countAll())
				.intValue();

		return deployedUnits;
	}

	@Override
	public void buildUnit(Player player, UnitType type, Integer amount)
			throws UnitServiceException {

		List<Unit> units = Lists.newLinkedList();
		for (int i = 0; i < amount; i++) {
			units.add(getInstance(type));
		}

		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Map<ResourceType, Integer> playerResources = resourceService
				.getResourcesOfPlayer(loadedPlayer);
		Map<ResourceType, Integer> unitCosts = units.get(0).getCosts();

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
		for (ResourceType resourceType : unitCosts.keySet()) {
			Integer costs = unitCosts.get(resourceType) * amount;
			playerResources.put(resourceType, playerResources.get(resourceType)
					- costs);
		}

		// Persist the units
		for (Unit unit : units) {
			unit.setPlayer(loadedPlayer);
			unitDAO.save(unit);
		}

		// Add the unit to the player's unit pool
		loadedPlayer.getUnits().addAll(units);
		loadedPlayer.setResourceDepot(playerResources);
		playerDAO.save(loadedPlayer);
	}

	@Override
	public void deployUnit(Player player, UnitType type, Integer amount,
			Place target) throws UnitServiceException {

		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Place loadedPlace = placeDAO.findOne("id", target.getId());
		if (loadedPlace == null)
			throw new NullPointerException("Could not find place with id "
					+ target.getId() + ".");

		// Retrieve undeployed units of the given type
		List<Unit> deployableUnits = getUndeployedUnits(loadedPlayer, type);

		// Check if player has enough units left
		if (deployableUnits.size() < amount)
			throw new UnitServiceException(
					"Not enough units to fulfill request!");

		// Deploy units and persist changes
		for (int i=0; i<amount; i++) {
			Unit unit = deployableUnits.get(i);
			unit.setDeployedAt(loadedPlace);
			loadedPlace.getDeployedUnits().add(unit);
			unitDAO.save(unit);
		}

		placeDAO.save(loadedPlace);
	}

	@Override
	public void retrieveUnit(Player player, UnitType type, Integer amount,
			Place from) throws UnitServiceException {

		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		Place loadedPlace = placeDAO.findOne("id", from.getId());
		if (loadedPlace == null)
			throw new NullPointerException("Could not find place with id "
					+ from.getId() + ".");

		List<Unit> deployedUnits = placeService.getDeployedUnitsOfPlayer(
				loadedPlayer, type, loadedPlace);

		// Check if player has enough units deployed
		if (deployedUnits.size() < amount)
			throw new UnitServiceException(
					"Not enough units to fulfill request!");

		Map<String, Unit> retrievalMap = Maps.newHashMap();
		
		// Build the map of units to be retrived
		for (int i = 0; i < amount; i++) {
			Unit unit = deployedUnits.get(i);
			unit.setDeployedAt(null);
			retrievalMap.put(unit.getId().toStringMongod(), unit);
			unitDAO.save(unit);
			System.out.println("I just put " + unit.getId().toStringMongod() + " in the map.");
		}
		
		List<Unit> retrieveThisUnits = Lists.newLinkedList();
		
		//prepare units for retrieval
		for (Unit unit : loadedPlace.getDeployedUnits()) {
			System.out.println("Looking at " + unit.getId().toStringMongod() + ".");
			if (retrievalMap.containsKey(unit.getId().toStringMongod()))
				retrieveThisUnits.add(unit);
		}
		
		System.out.println("The retrievalList contains " + retrieveThisUnits.size() + " units.");
		
		//retrieve the units
		for (Unit unit : retrieveThisUnits) {
			loadedPlace.getDeployedUnits().remove(unit);
		}
		
		System.out.println("The place has now " + loadedPlace.getDeployedUnits().size() + " units deployed.");
				

		placeDAO.save(loadedPlace);
	}
}
