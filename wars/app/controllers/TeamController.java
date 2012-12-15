package controllers;

import play.mvc.Controller;
import services.api.TeamService;

import com.google.inject.Inject;

public class TeamController extends Controller {
	
	@Inject
	private static TeamService teamService;
	
	// TODO: Wrap the methods of the teamService into controller actions

}
