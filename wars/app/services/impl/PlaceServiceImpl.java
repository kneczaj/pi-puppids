package services.impl;

import java.util.List;

import models.AmountType;
import models.Place;
import models.PlaceType;
import models.Player;
import models.ResourceType;
import models.Unit;
import models.UnitType;
import services.api.PlaceService;
import assets.constants.PlaceMappings;

import com.google.code.morphia.query.Query;
import com.google.inject.Inject;

import daos.PlaceDAO;
import daos.PlayerDAO;
import daos.UnitDAO;

/**
 * Implementation for the PlaceService
 * 
 * @author michi
 * 
 */
public class PlaceServiceImpl implements PlaceService {

	@Inject
	private static PlaceDAO placeDAO;

	@Inject
	private static PlayerDAO playerDAO;

	@Inject
	private static UnitDAO unitDAO;

	@Override
	public ResourceType getResourceType(PlaceType placeType) {
		return PlaceMappings.PLACE_TO_RESOURCE_MAP.get(placeType);
	}

	@Override
	public Integer getResourceAmount(PlaceType placeType) {
		AmountType amount = PlaceMappings.PLACE_TO_AMOUNT_MAP.get(placeType);
		return PlaceMappings.AMOUNT_TYPE_TO_VALUE_MAP.get(amount);
	}

	@Override
	public List<Unit> getDeployedUnitsOfPlayer(Player player, Place place)
			throws NullPointerException {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		Place loadedPlace = placeDAO.findOne("id", place.getId());

		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		if (loadedPlace == null)
			throw new NullPointerException("Could not find place with id "
					+ place.getId() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(
				query.criteria("deployedAt").equal(place),
				query.criteria("player").equal(player)
				);
		
		List<Unit> result = unitDAO.find(query).asList();

		return result;
	}

	@Override
	public List<Unit> getDeployedUnitsOfPlayer(Player player,
			UnitType unitType, Place place) throws NullPointerException {
		Player loadedPlayer = playerDAO.findOne("username",
				player.getUsername());
		Place loadedPlace = placeDAO.findOne("id", place.getId());

		if (loadedPlayer == null)
			throw new NullPointerException("Could not find player with name "
					+ player.getUsername() + ".");

		if (loadedPlace == null)
			throw new NullPointerException("Could not find place with id "
					+ place.getId() + ".");

		Query<Unit> query = unitDAO.createQuery();
		query.and(
				query.criteria("deployedAt").equal(place),
				query.criteria("player").equal(player),
				query.criteria("type").equal(unitType)
				);
		
		List<Unit> result = unitDAO.find(query).asList();

		return result;
	}

}
