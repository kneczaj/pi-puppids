package services.impl;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import models.CheckConquerConditionsResult;
import models.ConqueringAttempt;
import models.Faction;
import models.InitiateConquerResult;
import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;

import org.bson.types.ObjectId;

import services.api.ConqueringService;
import services.api.MapInfoService;
import services.api.VictoryStrategy;
import services.api.error.ConqueringServiceException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import communication.ClientPushActor;

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
	private MapInfoService mapInfoService;

	@Inject
	private VictoryStrategy victoryStrategy;

	@Override
	public Set<Player> getTeamMembersNearby(Player player, Place place) {
		Set<Player> teamMembersNearby = new HashSet<Player>(
				mapInfoService.findTeamMembersNearby(player.getTeam(), place,
						100));

		return teamMembersNearby;
	}

	@Override
	public Set<Player> getTeamMembersWithSufficientResources(Place place,
			Set<Player> players) {

		if (players.isEmpty()) {
			return Sets.newHashSet();
		}

		Set<Player> filteredList = Sets.newHashSet();
		HashSet<String> ignoreList = Sets.newHashSet();

		for (Entry<ResourceType, Integer> resourceDemand : place
				.getResourceDemand().entrySet()) {

			float resourceDemandPerPlayer = resourceDemand.getValue()
					/ players.size();
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
			return getTeamMembersWithSufficientResources(place, filteredList);
		}
	}

	@Override
	public JoinConquerResult joinConquer(String conqueringAttemptId,
			Player player) {
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

		Team teamOfPlayer = player.getTeam();
		Team teamOfInitiator = ca.getInitiator().getTeam();

		if (!teamOfPlayer.getId().equals(teamOfInitiator)) {
			return JoinConquerResult.UNALLOWED_TO_JOIN;
		}

		// only add the player to the joining members if it isn't already
		// participating
		if (!ca.getJoiningMembers().contains(player)) {
			ca.getJoiningMembers().add(player);
			conqueringAttemptDAO.save(ca);

			// inform the conquer initiator that a team member joined
			// successfully the conquering attempt
			ClientPushActor.conquerParticipantJoined(player, ca);

			// Recheck the conquering conditions
			CheckConquerConditionsResult result = checkConquerConditions(
					conqueringAttemptId, player);

			// If the conditions are met by all participating members than the
			// initiator is informed that the conquer is now possible
			if (result.getConqueringStatus().equals(
					ConqueringStatus.CONQUER_POSSIBLE)) {
				ClientPushActor.sendConquerPossible(ca);
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
			String conqueringAttemptId, Player player) {
		if (player == null) {
			throw new NullPointerException("Player has to be specified");
		}

		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));

		if (ca == null) {
			throw new IllegalArgumentException(
					"Could not find conquering attempt");
		}

		Place place = ca.getPlace();
		Set<Player> teamMembersNearby = getTeamMembersNearby(player, place);
		CheckConquerConditionsResult result = new CheckConquerConditionsResult(
				ca);

		if (!teamMembersNearby.contains(player)) {
			result.setConqueringStatus(ConqueringStatus.PLAYER_NOT_NEARBY);
			return result;
		}

		// Check if team members are wealthy enough
		Set<Player> wealthyMembersNearby = getTeamMembersWithSufficientResources(
				place, teamMembersNearby);
		result.setParticipants(Lists.newArrayList(wealthyMembersNearby));

		if (!wealthyMembersNearby.contains(player)) {
			result.setConqueringStatus(ConqueringStatus.PLAYER_HAS_INSUFFICIENT_RESOURCES);
			return result;
		}

		if (wealthyMembersNearby.isEmpty()) {
			result.setConqueringStatus(ConqueringStatus.RESOURCES_DO_NOT_SUFFICE);
			return result;
		}

		// check if the place already belongs to someone
		if (!place.getConqueredBy().isEmpty()) {
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
	public ConqueringStatus conquer(String conqueringAttemptId, Player player) {
		CheckConquerConditionsResult result = checkConquerConditions(
				conqueringAttemptId, player);

		ConqueringStatus status = result.getConqueringStatus();
		if (!status.equals(ConqueringStatus.CONQUER_POSSIBLE)) {
			return status;
		}

		Place place = result.getConqueringAttempt().getPlace();
		List<Player> participants = result.getParticipants();

		// check if the place already belongs to someone
		if (!place.getConqueredBy().isEmpty()) {
			// check if the players nearby overcome the defending units of the
			// current owner of the place
			if (!victoryStrategy.doAttackersWin(participants,
					place.getDeployedUnits())) {
				return ConqueringStatus.LOST;
			}
		}

		// Conquering Attempt was successful => assign place to the conquerors
		place.setConqueredBy(participants);
		placeDAO.updateConquerors(place, participants);

		return ConqueringStatus.SUCCESSFUL;
	}

	@Override
	public InitiateConquerResult initiateConquer(Player player, Place place) {
		if (player == null || place == null) {
			throw new NullPointerException("Player and place must not be null");
		}

		// check if place belongs already to the faction of the player
		if (place.getConqueredBy().size() > 0) {
			Faction factionOfPlayer = player.getTeam().getFaction();
			Player conqueror = place.getConqueredBy().get(0);
			Faction factionOfPlace = conqueror.getTeam().getFaction();

			if (factionOfPlace.getId().equals(factionOfPlayer.getId())) {
				return new InitiateConquerResult(
						InitiateConquerResult.Type.PLACE_ALREADY_BELONGS_TO_FACTION);
			}
		}

		Set<Player> membersNearby = getTeamMembersNearby(player, place);
		if (!membersNearby.contains(player)) {
			return new InitiateConquerResult(
					InitiateConquerResult.Type.PLAYER_NOT_NEARBY);
		}

		ConqueringAttempt ca = new ConqueringAttempt();
		ca.setStartDate(new Date());
		ca.setInitiator(player);
		ca.setPlace(place);

		conqueringAttemptDAO.save(ca);
		return new InitiateConquerResult(InitiateConquerResult.Type.SUCCESSFUL,
				ca);
	}

	@Override
	public void sendOutInvitations(String conqueringAttemptId)
			throws ConqueringServiceException {
		ConqueringAttempt ca = conqueringAttemptDAO.get(new ObjectId(
				conqueringAttemptId));
		if (ca == null) {
			throw new IllegalArgumentException(
					"Could not find conquering attempt");
		}

		// Currently all online team members are notified
		// TODO: filter players by distance
		List<String> onlinePlayerIds = ClientPushActor.getReachablePlayers();
		List<Player> onlinePlayersOfTeam = playerDAO.findPlayers(
				onlinePlayerIds, ca.getInitiator().getTeam());
		ClientPushActor.sendConqueringInvitation(ca, onlinePlayersOfTeam);
	}

}
