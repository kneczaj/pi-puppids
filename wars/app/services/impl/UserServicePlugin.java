package services.impl;

import java.net.UnknownHostException;
import java.util.Date;

import models.AccessToken;
import models.City;
import models.Faction;
import models.Player;
import models.Team;
import play.Application;
import play.Logger;
import securesocial.core.java.AuthenticationMethod;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.PasswordInfo;
import securesocial.core.java.SocialUser;
import securesocial.core.java.Token;
import securesocial.core.java.UserId;
import services.api.TeamService;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.inject.Inject;
import com.mongodb.Mongo;

import daos.AccessTokenDAO;
import daos.CityDAO;
import daos.FactionDAO;
import daos.PlayerDAO;
import daos.TeamDAO;

/**
 * Implementation of a UserServicePlugin needed for SecureSocial
 * Stores things directly with the playerDAO.
 * 
 * @author markus
 */
public class UserServicePlugin extends BaseUserService {

	private PlayerDAO playerDAO;
	private AccessTokenDAO accessTokenDAO;
	private TeamDAO teamDAO;
	private FactionDAO factionDAO;
	private CityDAO cityDAO;
	
	@Inject
	private TeamService teamService;

	public UserServicePlugin(Application application) {
		super(application);
		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);

		Logger.info("Loading UserServicePlugin");
		
		try {
			Mongo mongo = new Mongo();
			Morphia morphia = new Morphia();
			
			playerDAO = new PlayerDAO(mongo, morphia);
			accessTokenDAO = new AccessTokenDAO(mongo, morphia);
			factionDAO = new FactionDAO(mongo, morphia);
			cityDAO = new CityDAO(mongo, morphia);
			teamDAO = new TeamDAO(mongo, morphia);
		} catch (UnknownHostException e) {
			Logger.error("Could not instantiate DAOs necessary for the authentication");
		}
	}
	
	private Faction getRedFaction() {
		Faction load = factionDAO.findOne("name", "red");
		if (load != null) {
			return load;
		}
		
		Faction faction = new Faction();
		faction.setName("red");
		faction.setScore(0);
		factionDAO.save(faction);
		
		return faction;
	}
	
	private City getMunich() {
		City load = cityDAO.findOne("name", "Munich");
		if (load != null) {
			return load;
		}
		
		City munich = new City();
		munich.setCountry("de");
		munich.setName("Munich");
		munich.setLatitude(48.133333);
		munich.setLongitude(11.566667);
		cityDAO.save(munich);
		
		return munich;
	}
	
	private Team createPseudoTeamForPlayer() {
		Team t = new Team();
		t.setCity(getMunich());
		t.setCreatedAt(new Date());
		t.setFaction(getRedFaction());
		t.setName("pseudo");
		teamDAO.save(t);
		
		return t;
	}

	@Override
	public void doSave(SocialUser user) {
		Logger.debug("save user");
		Player p = new Player();
		p.setAuthenticationProvider(user.getAuthMethod().toString());
		p.setEmail(user.getEmail());
		p.setFirstname(user.getFirstName());
		p.setName(user.getLastName());
		p.setPasswordHash(user.getPasswordInfo().getPassword());
		p.setUsername(user.getId().getId());
		
		Player load = playerDAO.findOne("email", user.getEmail());
		if (load == null) {
			Team pseudoTeam = createPseudoTeamForPlayer();
			p.setTeam(pseudoTeam);
			
			playerDAO.save(p);
		}
	}

	@Override
	public void doSave(Token token) {
		Logger.debug("save token");
		AccessToken aToken = new AccessToken();
		aToken.setCreationTime(token.getCreationTime().toDate());
		aToken.setExpirationTime(token.getExpirationTime().toDate());
		aToken.setSignUp(token.getIsSignUp());
		aToken.setUuid(token.getUuid());
		aToken.setEmail(token.getEmail());

		accessTokenDAO.save(aToken);
	}

	@Override
	public SocialUser doFind(UserId userId) {
		Logger.debug("find user by username: " + userId.getId() + ", provider: " + userId.getProvider());
		Player p = playerDAO.findOne("username", userId.getId());
		if (p == null) {
			return null;
		}
		SocialUser s = new SocialUser();
		s.setAuthMethod(AuthenticationMethod.valueOf(p
				.getAuthenticationProvider()));
		s.setEmail(p.getEmail());
		s.setFirstName(p.getFirstname());
		s.setLastName(p.getName());
		
		UserId uId = new UserId();
		uId.setId(p.getUsername());
		uId.setProvider(p.getAuthenticationProvider());
		s.setId(uId);

		PasswordInfo pwInfo = new PasswordInfo();
		pwInfo.setPassword(p.getPasswordHash());
		s.setPasswordInfo(pwInfo);

		return s;
	}

	@Override
	public Token doFindToken(String tokenId) {
		Logger.debug("find token");
		AccessToken accessToken = accessTokenDAO.findOne("uuid", tokenId);
		if (accessToken == null) {
			Logger.error("could not find token");
			return null;
		}
		else {
			Logger.debug(accessToken.toString());
			Logger.debug(accessToken.toToken().toString());
			return accessToken.toToken();
		}
	}

	@Override
	public SocialUser doFindByEmailAndProvider(String email, String providerId) {
		Logger.debug("find social user by email and provider");
		Player p = playerDAO.findOne("email", email);
		if (p == null) {
			return null;
		}
		
		SocialUser result = p.toSocialUser();

		return result;
	}

	@Override
	public void doDeleteToken(String uuid) {
		Logger.debug("delete token");
		
		AccessToken aToken = accessTokenDAO.findOne("uuid", uuid);
		accessTokenDAO.delete(aToken);
	}

	@Override
	public void doDeleteExpiredTokens() {
		if (accessTokenDAO == null) {
			Logger.warn("DAO IS NULL!!!");
		}
		
		for (AccessToken aToken : accessTokenDAO.find()) {
			if (aToken.toToken().isExpired()) {
				accessTokenDAO.delete(aToken);
			}
		}
	}

}