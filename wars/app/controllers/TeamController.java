package controllers;

import java.util.List;

import org.bson.types.ObjectId;

import models.Player;
import models.Team;

import views.html.invite;

import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;

import securesocial.core.java.SecureSocial;
import securesocial.core.java.SocialUser;

import services.api.TeamService;
import services.impl.UserServicePlugin;

import com.google.code.morphia.query.Query;
import com.google.inject.Inject;

import daos.PlayerDAO;

public class TeamController extends Controller {
	
	@Inject
	private static TeamService teamService;
	
	@Inject
	private static PlayerDAO playerDAO;
	
	// TODO: Wrap the methods of the teamService into controller actions
	
	@SecureSocial.SecuredAction
	public static Result canInvite(String email)
	{
		SocialUser user = (SocialUser) ctx().args.get(SecureSocial.USER_KEY);
		if (email.equals(user.getEmail()))
			return ok("usersEmailGiven");
		
		Query<Player> playerQ = playerDAO.createQuery().field("email").equal(email);

		if (playerDAO.find(playerQ).countAll() == 0)
			return ok("givenEmailDoesntExist");
		
		return ok("ok");
	}
	
	public static Result inviteForm()
	{
		return ok(invite.render());
	}

}
