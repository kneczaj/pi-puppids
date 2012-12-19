package services.impl;

import services.api.ProfileService;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;

import daos.PlayerDAO;

import com.google.inject.Inject;

import models.Player;
import play.mvc.Http;

/**
 * Implementation of the ProfileService
 * @author kamil
 *
 */
public class ProfileServiceImpl implements ProfileService {
	
	// some problems with service registration
	
	@Inject
	private PlayerDAO playerDAO;

	@Override
	@SecureSocial.SecuredAction
	public SocialUser loggedUser(Http.Context ctx) {
		SocialUser user = (SocialUser) ctx.args.get(SecureSocial.USER_KEY);
		return user;
	}
	
	@Override
	public Player loggedPlayer(Http.Context ctx) {
		SocialUser user = loggedUser(ctx);
		return playerDAO.findOne("email", user.getEmail());
	}

}
