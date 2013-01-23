package services.impl;

import models.Player;
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
	
	@SecureSocial.SecuredAction
	public Player getPlayer() {
		SocialUser user = SecureSocial.currentUser();
		return playerDAO.findOne("email", user.getEmail());
	}

}
