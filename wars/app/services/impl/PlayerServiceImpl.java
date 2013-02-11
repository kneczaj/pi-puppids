package services.impl;

import java.util.Date;

import models.City;
import models.Faction;
import models.Player;
import models.Team;

import org.bson.types.ObjectId;

import securesocial.core.java.SocialUser;
import services.api.PlayerService;
import services.api.TeamService;
import services.api.error.PlayerServiceException;

import com.google.inject.Inject;

import daos.CityDAO;
import daos.FactionDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * PlayerService Implementation
 * 
 * @author markus
 */
public class PlayerServiceImpl implements PlayerService {

	@Inject
	private PlayerDAO playerDAO;

	@Inject
	private TeamService teamService;

	@Inject
	private TeamDAO teamDAO;

	@Inject
	private FactionDAO factionDAO;

	@Inject
	private CityDAO cityDAO;

	@Override
	public Player register(String authenticationProvider, String email,
			String firstname, String lastname, String passwordHash,
			String username) throws PlayerServiceException {
		Player p = new Player();
		p.setAuthenticationProvider(authenticationProvider);
		p.setEmail(email);
		p.setFirstname(firstname);
		p.setName(lastname);
		p.setPasswordHash(passwordHash);
		p.setUsername(username);

		Player load = playerDAO.findOne("username", username);
		if (load == null) {
			Team initialTeam = teamService.createInitialTeam(p);
			playerDAO.save(p);
			teamService.joinTeam(p, initialTeam);
		} else {
			throw new PlayerServiceException(
					"This username is already taken by an other player");
		}

		return p;
	}
	
	public Player setData(Player player, String firstname, String lastname, String email, String hometown, Date birthday) {
		player.setEmail(email);
		player.setFirstname(firstname);
		player.setName(lastname);
		player.setDateOfBirth(birthday);
		player.setHomeTown(hometown);
		playerDAO.save(player);
		return player;
	}

	@Override
	public Player register(SocialUser user) throws PlayerServiceException {
		return register(user.getAuthMethod().toString(), user.getEmail(),
				user.getFirstName(), user.getLastName(), user.getPasswordInfo()
						.getPassword(), user.getId().getId());
	}

	@Override
	public Player joinCity(Player player, String cityId)
			throws PlayerServiceException {
		if (player == null || player.getTeam() == null || cityId == null) {
			throw new NullPointerException(
					"Player and city and a player's team must not be null");
		}

		City newCity = cityDAO.get(new ObjectId(cityId));
		if (newCity == null) {
			throw new PlayerServiceException(
					"The Faction with this factionId could not be found.");
		}

		City oldCity = player.getTeam().getCity();
		if (oldCity != null) {
			// Player belongs already to this faction => is okay
			if (oldCity.getId().toString().equals(newCity.getId().toString())) {
				return player;
			} else {
				throw new PlayerServiceException(
						"Player already belongs to an other city.");
			}
		}

		teamDAO.updateCityOfTeam(player.getTeam(), newCity);
		Player p = playerDAO.get(player.getId());

		return p;
	}

	@Override
	public Player joinFaction(Player player, String factionId)
			throws PlayerServiceException {
		if (player == null || player.getTeam() == null || factionId == null) {
			throw new NullPointerException(
					"Player and factionId and a player's team must not be null");
		}

		Faction newFaction = factionDAO.get(new ObjectId(factionId));
		if (newFaction == null) {
			throw new PlayerServiceException(
					"The Faction with this factionId could not be found.");
		}

		Faction oldFaction = player.getTeam().getFaction();
		if (oldFaction != null) {
			// Player belongs already to this faction => is okay
			if (oldFaction.getName().equals(newFaction.getName())) {
				return player;
			} else {
				throw new PlayerServiceException(
						"Player already belongs to an other faction.");
			}
		}

		teamDAO.updateFactionOfTeam(player.getTeam(), newFaction);
		Player p = playerDAO.get(player.getId());

		return p;
	}
	
	

}
