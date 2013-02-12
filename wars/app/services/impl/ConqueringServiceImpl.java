package services.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import models.AmountType;
import models.Faction;
import models.GPlace;
import models.Place;
import models.PlaceType;
import models.Player;
import models.ResourceType;
import models.Team;
import models.conquer.CheckConquerConditionsResult;
import models.conquer.ConqueringAttempt;
import models.conquer.InitiateConquerResult;

import org.bson.types.ObjectId;

import services.api.ConqueringService;
import services.api.MapInfoService;
import services.api.VictoryStrategy;
import services.api.WebSocketCommunicationService;
import services.google.places.api.GPlaceService;
import services.google.places.api.GPlaceServiceException;
import assets.constants.PlaceMappings;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import daos.ConqueringAttemptDAO;
import daos.PlaceDAO;
import daos.PlayerDAO;

/**
 * Implementation of the ConqueringService
 * 
 * @author markus
 */
public class ConqueringServiceImpl implements ConqueringService {

	@Inject
	private PlaceDAO placeDAO;

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private ConqueringAttemptDAO conqueringAttemptDAO;

	@Inject
	private GPlaceService gPlaceService;

	@Inject
	private MapInfoService mapInfoService;

	@Inject
	private VictoryStrategy victoryStrategy;

	@Inject
	private WebSocketCommunicationService webSocketCommunicationService;

	@Override
	public Set<Player> getTeamMembersNearby(Player player, String reference)
			throws GPlaceServiceException {
		Set<Player> teamMembersNearby = new HashSet<Player>(
				mapInfoService.findTeamMembersNearby(player.getTeam(),
						reference, 150));

		return teamMembersNearby;
	}

	public Integer calculateDemandPerPlayer(Integer demand, int playerCount) {
		return new Integer((int) Math.floor(demand / playerCount));
	}

	public void withdrawResourceDemandFromPlayers(Place p, List<Player> players) {
		Map<ResourceType, Integer> resourceDemand = PlaceMappings.PLACE_TO_RESOURCE_DEMAND_MAP
				.get(p.getType());

		for (Entry<ResourceType, Integer> demand : resourceDemand.entrySet()) {
			if (demand.getValue().intValue() == 0) {
				continue;
			}

			Integer demandPerPlayer = calculateDemandPerPlayer(
					demand.getValue(), players.size());
			for (Player player : players) {
				player.withdrawFromResourceDepot(demand.getKey(),
						demandPerPlayer);
			}
		}
	}

	@Override
	public Set<Player> getTeamMembersWithSufficientResources(String uuid,
			String reference, Set<Player> players)
			throws GPlaceServiceException {

		if (players.isEmpty()) {
			return Sets.newHashSet();
		}

		Set<Player> filteredList = Sets.newHashSet();
		HashSet<String> ignoreList = Sets.newHashSet();

		GPlace gPlace = gPlaceService.details(reference);
		PlaceType type = PlaceType.valueOf(gPlace.getTypes().get(0));
		Map<ResourceType, Integer> resourceDemands = PlaceMappings.PLACE_TO_RESOURCE_DEMAND_MAP
				.get(type);

		for (Entry<ResourceType, Integer> resourceDemand : resourceDemands
				.entrySet()) {

			Integer resourceDemandPerPlayer = calculateDemandPerPlayer(
					resourceDemand.getValue(), players.size());
			for (Player p : players) {
				// ignore players with insufficient resources
				if (p.getResourceDepot(resourceDemand.getKey()) < resourceDemandPerPlayer) {
					ignoreList.add(p.getId().toString());
				}
			}
		}

		if (ignoreList.isEmpty()) {
			return Sets.newHashSet(players);
		} else {
			for (Player p : players) {
				if (!ignoreList.contains(p.getId().toString())) {
					filteredList.add(p);
				}
			}

			// number of players that are allowed to participate decreased => so
			// the rest of the team has to carry their share
			return getTeamMembersWithSufficientResources(uuid, reference,
					filteredList);
		}
	}

	@Override
	public JoinConquerResult joinConquer(String conqueringAttemptId,
			Player player) throws GPlaceServiceException {
		if (player == null) {
			throw new NullPointerException("Player has to be specified");
		}

		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));

		if (ca == null) {
			throw new IllegalArgumentException(
					"Could not find conquering attempt");
		}

		if (ca.getEndDate() != null) {
			return JoinConquerResult.CONQUER_ALREADY_ENDED;
		}

		if (ca.isCanceled()) {
			return JoinConquerResult.CONQUER_CANCELED;
		}

		String teamOfPlayer = player.getTeam().getId().toString();
		String teamOfInitiator = ca.getInitiator().getTeam().getId().toString();

		if (!teamOfPlayer.equals(teamOfInitiator)) {
			return JoinConquerResult.UNALLOWED_TO_JOIN;
		}

		// only add the player to the joining members if it isn't already
		// participating
		if (!ca.getJoiningMembers().contains(player)) {
			ca.getJoiningMembers().add(player);
			conqueringAttemptDAO.save(ca);

			// inform the conquer initiator that a team member joined
			// successfully the conquering attempt
			webSocketCommunicationService.conquerParticipantJoined(player, ca);

			// Recheck the conquering conditions
			CheckConquerConditionsResult result = checkConquerConditions(
					conqueringAttemptId, player);

			// If the conditions are met by all participating members than the
			// initiator is informed that the conquer is now possible
			if (result.getConqueringStatus().equals(
					ConqueringStatus.CONQUER_POSSIBLE)) {
				webSocketCommunicationService.sendConquerPossible(ca);
			}
		}

		return JoinConquerResult.SUCCESSFUL;
	}

	@Override
	public boolean cancelConquer(String conqueringAttemptId, Player player) {
		if (player == null) {
			throw new NullPointerException("Player has to be specified");
		}

		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));

		if (ca == null) {
			throw new IllegalArgumentException(
					"Could not find conquering attempt");
		}

		// check if the player given as argument matches the initiator of the
		// conquering attempt to cancel
		if (!ca.getInitiator().getId().equals(player.getId())) {
			return false;
		}

		ca.setCanceled(true);
		conqueringAttemptDAO.save(ca);

		return true;
	}

	@Override
	public CheckConquerConditionsResult checkConquerConditions(
			String conqueringAttemptId, Player player)
			throws GPlaceServiceException {
		if (player == null) {
			throw new NullPointerException("Player has to be specified");
		}

		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));

		if (ca == null) {
			throw new IllegalArgumentException(
					"Could not find conquering attempt");
		}

		Set<Player> teamMembersNearby = getTeamMembersNearby(player,
				ca.getReference());
		CheckConquerConditionsResult result = new CheckConquerConditionsResult(
				ca);

		boolean playerNearby = false;
		for (Player p : teamMembersNearby) {
			if (p.getId().equals(player.getId()))
				playerNearby = true;
		}

		if (!playerNearby) {
			result.setConqueringStatus(ConqueringStatus.PLAYER_NOT_NEARBY);
			return result;
		}

		// Check if team members are wealthy enough
		Set<Player> wealthyMembersNearby = getTeamMembersWithSufficientResources(
				ca.getUuid(), ca.getReference(), teamMembersNearby);
		result.setParticipants(Lists.newArrayList(wealthyMembersNearby));

		boolean playerIsWealthy = false;
		for (Player p : wealthyMembersNearby) {
			if (p.getId().equals(player.getId()))
				playerIsWealthy = true;
		}

		if (!playerIsWealthy) {
			result.setConqueringStatus(ConqueringStatus.PLAYER_HAS_INSUFFICIENT_RESOURCES);
			return result;
		}

		if (wealthyMembersNearby.isEmpty()) {
			result.setConqueringStatus(ConqueringStatus.RESOURCES_DO_NOT_SUFFICE);
			return result;
		}

		Place place = placeDAO.findOne("uuid", ca.getUuid());

		// check if the place already belongs to someone
		if (place != null && !place.getConqueredBy().isEmpty()) {
			Team playersTeam = player.getTeam();
			Team placesTeam = place.getConqueredBy().get(0).getTeam();

			// ensure that the place does not already belong to the player's
			// faction
			if (placesTeam.getFaction().equals(playersTeam.getFaction())) {
				result.setConqueringStatus(ConqueringStatus.PLACE_ALREADY_BELONGS_TO_FACTION);
				return result;
			}

			int conquerors = place.getNumberOfConquerors();
			int attackers = teamMembersNearby.size();

			if (attackers <= conquerors) {
				result.setConqueringStatus(ConqueringStatus.NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE);
				return result;
			}
		}

		result.setConqueringStatus(ConqueringStatus.CONQUER_POSSIBLE);

		return result;
	}

	@Override
	public ConqueringStatus conquer(String conqueringAttemptId, Player player)
			throws GPlaceServiceException {
		CheckConquerConditionsResult result = checkConquerConditions(
				conqueringAttemptId, player);

		ConqueringStatus status = result.getConqueringStatus();
		if (!status.equals(ConqueringStatus.CONQUER_POSSIBLE)) {
			return status;
		}

		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));

		Place place = placeDAO.findOne("uuid", ca.getUuid());
		List<Player> participants = result.getParticipants();

		// check if the place already belongs to someone
		if (place != null) {
			// check if the players nearby overcome the defending units of the
			// current owner of the place
			if (!victoryStrategy.doAttackersWin(participants,
					place.getDeployedUnits())) {
				return ConqueringStatus.LOST;
			}
		}

		// Conquering Attempt was successful => assign place to the conquerors
		// Check if the place is already stored in the database, else create it
		if (place != null) {
			place.setConqueredBy(participants);
			placeDAO.updateConquerors(place, participants);

		} else {
			GPlace gPlace = gPlaceService.details(ca.getReference());
			place = new Place();
			place.setName(gPlace.getName());
			place.setLat(gPlace.getLatitude());
			place.setLng(gPlace.getLongitude());
			place.setUuid(gPlace.getUuid());

			PlaceType type = PlaceType.valueOf(gPlace.getTypes().get(0));
			place.setType(type);

			AmountType amount = PlaceMappings.PLACE_TO_AMOUNT_MAP.get(type);
			place.setAmount(PlaceMappings.AMOUNT_TYPE_TO_VALUE_MAP
					.get(amount));
			place.setResource(PlaceMappings.PLACE_TO_RESOURCE_MAP.get(type));

			// TODO: Set Resource Demand correct
			Map<ResourceType, Integer> resourceDemands = PlaceMappings.PLACE_TO_RESOURCE_DEMAND_MAP
					.get(type);
			place.setResourceDemand(resourceDemands);

			placeDAO.save(place);
			place.setConqueredBy(participants);
			placeDAO.updateConquerors(place, participants);
		}
		//add the place to the participants
		for (Player p : participants) {
			p.getConquered().add(place);
			playerDAO.save(p);
		}
		
		withdrawResourceDemandFromPlayers(place, participants);

		return ConqueringStatus.SUCCESSFUL;
	}

	@Override
	public InitiateConquerResult initiateConquer(Player player, String uuid,
			String reference) throws GPlaceServiceException {
		if (player == null || uuid == null || reference == null) {
			throw new NullPointerException(
					"Player, id and reference must not be null");
		}

		// check if place belongs already to the faction of the player
		Place place = placeDAO.findOne("uuid", uuid);

		String placeName;
		Double placeLat;
		Double placeLng;

		if (place != null && place.getConqueredBy().size() > 0) {
			Faction factionOfPlayer = player.getTeam().getFaction();
			Player conqueror = place.getConqueredBy().get(0);
			Faction factionOfPlace = conqueror.getTeam().getFaction();

			if (factionOfPlace.getId().equals(factionOfPlayer.getId())) {
				return new InitiateConquerResult(
						InitiateConquerResult.Type.PLACE_ALREADY_BELONGS_TO_FACTION);
			}

			placeName = place.getName();
			placeLat = place.getLat();
			placeLng = place.getLng();
		} else {
			GPlace details = gPlaceService.details(reference);
			placeName = details.getName();
			placeLat = details.getLatitude();
			placeLng = details.getLongitude();
		}

		Set<Player> membersNearby = getTeamMembersNearby(player, reference);

		boolean playerNearby = false;
		for (Player p : membersNearby) {
			if (p.getId().equals(player.getId()))
				playerNearby = true;
		}

		if (!playerNearby) {
			return new InitiateConquerResult(
					InitiateConquerResult.Type.PLAYER_NOT_NEARBY);
		}

		ConqueringAttempt ca = new ConqueringAttempt();
		ca.setStartDate(new Date());
		ca.setInitiator(player);
		ca.setUuid(uuid);
		ca.setPlaceName(placeName);
		ca.setLat(placeLat);
		ca.setLng(placeLng);
		ca.setReference(reference);

		conqueringAttemptDAO.save(ca);
		return new InitiateConquerResult(InitiateConquerResult.Type.SUCCESSFUL,
				ca);
	}

	@Override
	public void sendOutInvitations(String conqueringAttemptId) {
		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));
		if (ca == null) {
			throw new IllegalArgumentException(
					"Could not find conquering attempt");
		}

		// TODO: filter players by distance
		String initiatorId = ca.getInitiator().getId().toString();
		List<Player> playersToInvite = Lists.newArrayList();
		List<Player> teamMembers = ca.getInitiator().getTeam().getPlayers();

		for (Player p : teamMembers) {
			if (p.getId().toString().equals(initiatorId)) {
				continue;
			}

			playersToInvite.add(p);
		}

		webSocketCommunicationService.sendConqueringInvitation(ca,
				playersToInvite);
	}

}
