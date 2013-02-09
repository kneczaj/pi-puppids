package plugins;

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
import services.api.PlayerService;
import services.api.error.PlayerServiceException;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;
import com.google.inject.Injector;

import daos.AccessTokenDAO;
import daos.PlayerDAO;

/**
 * Implementation of a UserServicePlugin needed for SecureSocial Stores things
 * directly with the playerDAO.
 * 
 * @author markus
 */
public class UserServicePlugin extends BaseUserService {

	private PlayerDAO playerDAO;
	private AccessTokenDAO accessTokenDAO;
	private PlayerService playerService;

	private Application application;

	public UserServicePlugin(Application application) {
		super(application);
		this.application = application;

		MorphiaLoggerFactory.reset();
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}

	public void start() {
		GuicePlugin plugin = application.plugin(GuicePlugin.class);
		Injector injector = plugin.getInjector();

		playerDAO = injector.getInstance(PlayerDAO.class);
		accessTokenDAO = injector.getInstance(AccessTokenDAO.class);
		playerService = plugin.getInjector().getInstance(PlayerService.class);
	}

	@Override
	public void doSave(SocialUser user) {
		try {
			playerService.register(user);
		} catch (PlayerServiceException e) {
			Logger.error(e.toString());
		}
	}

	@Override
	public void doSave(Token token) {
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
		AccessToken accessToken = accessTokenDAO.findOne("uuid", tokenId);
		if (accessToken == null) {
			Logger.error("could not find token");
			return null;
		} else {
			return accessToken.toToken();
		}
	}

	@Override
	public SocialUser doFindByEmailAndProvider(String email, String providerId) {
		Player p = playerDAO.findOne("email", email);
		if (p == null) {
			return null;
		}

		SocialUser result = p.toSocialUser();

		return result;
	}

	@Override
	public void doDeleteToken(String uuid) {
		AccessToken aToken = accessTokenDAO.findOne("uuid", uuid);
		accessTokenDAO.delete(aToken);
	}

	@Override
	public void doDeleteExpiredTokens() {
		for (AccessToken aToken : accessTokenDAO.find()) {
			if (aToken.toToken().isExpired()) {
				accessTokenDAO.delete(aToken);
			}
		}
	}

}