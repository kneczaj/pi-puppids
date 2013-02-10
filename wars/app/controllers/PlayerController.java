package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial.SecuredAction;

public class PlayerController extends Controller {
	
	@SecuredAction(ajaxCall=true)
	public Result changeProfile(String firstname, String lastname, String email, String hometown, String birthday) {
		return ok();
	}
}
