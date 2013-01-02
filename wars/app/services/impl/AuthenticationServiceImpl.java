package services.impl;

import models.Player;
import play.mvc.Http;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;
import services.api.AuthenticationService;

import com.google.inject.Inject;

import daos.PlayerDAO;

/**
 * Implementation of the ProfileService
 * @author kamil
 *
 */
public class AuthenticationServiceImpl implements AuthenticationService {
	
	@Inject
	private PlayerDAO playerDAO;

	@Override
	public SocialUser getUser(Http.Context ctx) {
		SocialUser user = (SocialUser) ctx.args.get(SecureSocial.USER_KEY);
		return user;
	}
	
	@Override
	public Player getPlayer(Http.Context ctx) {
		SocialUser user = getUser(ctx);
		return playerDAO.findOne("email", user.getEmail());
	}

}
