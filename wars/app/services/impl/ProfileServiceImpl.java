package services.impl;

import models.Player;
import daos.PlayerDAO;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;
import services.api.ProfileService;

import com.google.inject.Inject;

import play.mvc.Http;

/**
 * Implementation of the ProfileService
 * @author kamil
 *
 */
public class ProfileServiceImpl implements ProfileService {
	
	// some problems with service registration
	
//	@Inject
//	private PlayerDAO playerDAO;
//
//	@Override
//	@SecureSocial.SecuredAction
//	public SocialUser loggedUser(Http.Context ctx) {
//		return (SocialUser) ctx.args.get(SecureSocial.USER_KEY);
//	}
//	
//	@Override
//	public Player loggedPlayer(Http.Context ctx) {
//		SocialUser user = loggedUser(ctx);
//		return playerDAO.findOne("email", user.getEmail());
//	}

}
