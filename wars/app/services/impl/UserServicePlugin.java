package services.impl;

import java.net.UnknownHostException;

import models.AccessToken;
import models.Player;
import play.Application;
import play.Logger;
import securesocial.core.java.AuthenticationMethod;
import securesocial.core.java.BaseUserService;
import securesocial.core.java.PasswordInfo;
import securesocial.core.java.SocialUser;
import securesocial.core.java.Token;
import securesocial.core.java.UserId;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import daos.AccessTokenDAO;
import daos.PlayerDAO;

/**
 * A Sample In Memory user service in Java
 * 
 * Note: This is NOT suitable for a production environment and is provided only
 * as a guide. A real implementation would persist things in a database
 */
public class UserServicePlugin extends BaseUserService {

	private PlayerDAO playerDAO;
	private AccessTokenDAO accessTokenDAO;

	public UserServicePlugin(Application application) {
		super(application);
		Logger.info("Loading UserServicePlugin");
		
		try {
			Mongo mongo = new Mongo();
			Morphia morphia = new Morphia();
			
			playerDAO = new PlayerDAO(mongo, morphia);
			accessTokenDAO = new AccessTokenDAO(mongo, morphia);
		} catch (UnknownHostException e) {
			Logger.error("Could not instantiate DAOs necessary for the authentication");
		}
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
		p.setSecureSocialIdentifier(user.getId().getId());
		playerDAO.save(p);
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
		Logger.debug("find user by userId: " + userId.getId() + ", provider: " + userId.getProvider());
		Player p = playerDAO.findOne("secureSocialIdentifier", userId.getId());
		SocialUser s = new SocialUser();
		s.setAuthMethod(AuthenticationMethod.valueOf(p
				.getAuthenticationProvider()));
		s.setEmail(p.getEmail());
		s.setFirstName(p.getFirstname());
		s.setLastName(p.getName());
		
		UserId uId = new UserId();
		uId.setId(p.getSecureSocialIdentifier());
		uId.setProvider(p.getAuthenticationProvider());
		s.setId(uId);

		PasswordInfo pwInfo = new PasswordInfo();
		pwInfo.setPassword(p.getPasswordHash());
		s.setPasswordInfo(pwInfo);
		Logger.debug("end of find");

		return s;
	}

	@Override
	public Token doFindToken(String tokenId) {
		Logger.debug("find token");
		AccessToken accessToken = accessTokenDAO.findOne("uuid", tokenId);
		if (accessToken == null) {
			Logger.error("could not find token");
		}
		else {
			Logger.debug(accessToken.toString());
			Logger.debug(accessToken.toToken().toString());
		}
		
		return accessToken.toToken();
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