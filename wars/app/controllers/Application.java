package controllers;

import models.Player;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import views.html.index;
import views.html.profile;

import com.google.inject.Inject;

import daos.PlayerDAO;

public class Application extends Controller {

	@Inject
	private static PlayerDAO playerDAO;
	
	@SecureSocial.SecuredAction
	public static Result index() {
		// insert a sample user if it isn't there
		Player samplePlayer = playerDAO.findOne("username", "sepp");
		if (samplePlayer == null) {
			samplePlayer = new Player();
			samplePlayer.setUsername("sepp");
			playerDAO.save(samplePlayer);
		}
		
		samplePlayer = playerDAO.findOne("username", "sepp");
		return ok(index.render(samplePlayer));
	}
	
	@SecureSocial.SecuredAction
	public static Result profile() {
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		Player player = playerDAO.findOne("email", user.getEmail());
		return ok(profile.render(player));
	}

}