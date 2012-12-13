package controllers;

import models.Player;
import models.PlayerLocation;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;
import views.html.index;
import views.html.profile;

import com.google.inject.Inject;

import daos.PlayerDAO;
import daos.PlayerLocationDAO;

public class Application extends Controller {

	@Inject
	private static PlayerDAO playerDAO;
	
	@Inject
	private static PlayerLocationDAO playerLocationDAO;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player player = playerDAO.findOne("email", user.getEmail());
		PlayerLocation playerLocation = playerLocationDAO.findLatestLocation(player);
		
		return ok(index.render(player, playerLocation));
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player player = playerDAO.findOne("email", user.getEmail());
		return ok(profile.render(player));
	}

}