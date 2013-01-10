package services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import models.Place;
import models.Player;
import models.ResourceType;
import services.api.ConqueringService;
import services.api.MapInfoService;
import services.api.VictoryStrategy;
import services.api.error.ConqueringServiceException;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;

import daos.PlaceDAO;

/**
 * Implementation of the ConqueringService
 * 
 * @author markus
 */
public class ConqueringServiceImpl implements ConqueringService {

	@Inject
	private PlaceDAO placeDAO;

	@Inject
	private MapInfoService mapInfoService;

	@Inject
	private VictoryStrategy victoryStrategy;

	@Override
	public List<Player> getTeamMembersAllowedToParticipateInConquer(
			Player player, Place place) {
		List<Player> teamMembersNearby = mapInfoService.findTeamMembersNearby(
				player.getTeam(), place, 100);

		return teamMembersNearby;
	}

	@Override
	public HashSet<Player> getTeamMembersWithSufficientResources(Place place,
			List<Player> players) {
		
		if (players.isEmpty()) {
			return Sets.newHashSet();
		}
		
		List<Player> filteredList = Lists.newArrayList();
		HashSet<String> ignoreList = Sets.newHashSet();
		
		for (Entry<ResourceType, Integer> resourceDemand : place
				.getResourceDemand().entrySet()) {
			
			float resourceDemandPerPlayer = resourceDemand.getValue() / players.size();
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
			
			// number of players that are allowed to participate decreased => so the rest of the team has to carry their share
			return getTeamMembersWithSufficientResources(place, filteredList);
		}
	}

	@Override
	public JoinConquerResult joinConquer(String conqueringAttemptId,
			Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void declineJoinRequest(String conqueringAttemptId, Player player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean cancelConquer(String conqueringAttemptId, Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ConqueringResult conquer(String conqueringAttemptId, Player player) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InitiateConquerResult initiateConquer(Player player, Place place) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendOutInvitations(String conqueringAttemptId)
			throws ConqueringServiceException {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	public ConqueringResult conquer(Player player, Place place) {
//		List<Player> teamMembersNearby = getTeamMembersAllowedToParticipateInConquer(player, place);
//		
//		boolean playerIsNearby = false;
//
//		// Check whether player is near the place
//		for (Player p : teamMembersNearby) {
//			if (p.getId().toString().equals(player.getId().toString())) {
//				playerIsNearby = true;
//			}
//		}
//
//		if (!playerIsNearby) {
//			return ConqueringResult.PLAYER_NOT_NEARBY;
//		}
//		
//		// Check if team members are wealthy enough
//		HashSet<Player> wealthyMembersNearby = getTeamMembersWithSufficientResources(place, teamMembersNearby);
//		if (!wealthyMembersNearby.contains(player)) {
//			return ConqueringResult.PLAYER_HAS_INSUFFICIENT_RESOURCES;
//		}
//		
//		if (wealthyMembersNearby.isEmpty()) {
//			return ConqueringResult.RESOURCES_DO_NOT_SUFFICE;
//		}
//
//		// check if the place already belongs to someone
//		if (!place.getConqueredBy().isEmpty()) {
//			Team playersTeam = player.getTeam();
//			Team placesTeam = place.getConqueredBy().get(0).getTeam();
//
//			// ensure that the place does not already belong to the player's
//			// faction
//			if (placesTeam.getFaction().equals(playersTeam.getFaction())) {
//				return ConqueringResult.PLACE_ALREADY_BELONGS_TO_FACTION;
//			}
//
//			// check if the players nearby overcome the defending units of the
//			// current owner of the place
//			if (!victoryStrategy.doAttackersWin(teamMembersNearby,
//					place.getDeployedUnits())) {
//				return ConqueringResult.LOST;
//			}
//
//			int conquerors = place.getNumberOfConquerors();
//			int attackers = teamMembersNearby.size();
//
//			if (attackers <= conquerors) {
//				return ConqueringResult.NUMBER_OF_ATTACKERS_DOES_NOT_SUFFICE;
//			}
//		}
//
//		// Conquering Attempt was successful => assign place to the conquerors
//		place.setConqueredBy(teamMembersNearby);
//		placeDAO.updateConquerors(place, teamMembersNearby);
//
//		return ConqueringResult.SUCCESSFUL;
//	}

}
