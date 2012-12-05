package controllers;

import models.Player;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

import com.google.code.morphia.logging.MorphiaLoggerFactory;
import com.google.code.morphia.logging.slf4j.SLF4JLogrImplFactory;

import daos.PlayerDAO;


public class Application extends Controller {
	
	static {
		MorphiaLoggerFactory.registerLogger(SLF4JLogrImplFactory.class);
	}

	private static PlayerDAO playerDAO = new PlayerDAO();
	
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

}