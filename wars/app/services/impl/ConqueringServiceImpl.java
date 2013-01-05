package services.impl;

import java.util.List;
import java.util.Map;

import models.Place;
import models.Player;
import models.ResourceType;
import models.Team;
import models.Unit;
import services.api.ConqueringService;
import services.api.MapInfoService;

import com.google.inject.Inject;

/**
 * Implementation of the ConqueringService
 * 
 * @author markus
 */
public class ConqueringServiceImpl implements ConqueringService {

	@Inject
	private static MapInfoService mapInfoService;

	@Override
	public ConqueringResult conquer(Player player, Place place) {
		List<Player> teamMembersNearby = mapInfoService.findTeamMembersNearby(
				player.getTeam(), place, 100);
		boolean playerIsNearby = false;

		// Check whether player is near the place
		for (Player p : teamMembersNearby) {
			if (p.getId().toString().equals(player.getId().toString())) {
				playerIsNearby = true;
			}
		}

		if (!playerIsNearby) {
			return ConqueringResult.PLAYER_NOT_NEARBY;
		}

		// check if the place already belongs to someone
		if (place.getConqueredBy().isEmpty()) {
			// check if the players nearby overcome the resource demand of the place
			if (!doResourcesOvercomeResourceDemand(teamMembersNearby, place.getResourceDemand())) {
				return ConqueringResult.RESOURCES_DO_NOT_SUFFICE;
			}
		} else {
			// place is already conquered by a team
			Team playersTeam = player.getTeam();
			Team placesTeam = place.getConqueredBy().get(0).getTeam();

			// ensure that the place does not already belong to the player's
			// faction
			if (placesTeam.getFaction().equals(playersTeam.getFaction())) {
				return ConqueringResult.PLACE_ALREADY_BELONGS_TO_FACTION;
			}
			
			// check if the players nearby overcome the defending units of the current owner of the place
			if (!doResourcesOvercomeDefendingUnits(teamMembersNearby, place.getDeployedUnits())) {
				return ConqueringResult.LOST;
			}
		}
		
		// TODO: assign place to the player's team
		// TODO: save the participants of the place within place.conqueredBy
		// TODO: save the number of participants taking part in the conquering attempt (list of conqueredBy might decrease with the time since players might drop their account)

		return ConqueringResult.SUCCESSFUL;
	}

	@Override
	public boolean doResourcesOvercomeDefendingUnits(List<Player> players,
			List<Unit> defendingUnits) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean doResourcesOvercomeResourceDemand(List<Player> players,
			Map<ResourceType, Integer> resourceDemand) {
		// TODO Auto-generated method stub
		return false;
	}
}
