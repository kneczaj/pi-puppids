package controllers;

import static play.libs.Json.toJson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import models.Player;
import models.PlayerLocation;

import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonNode;

import daos.PlayerDAO;
import daos.PlayerLocationDAO;


import play.mvc.BodyParser;
import play.mvc.BodyParser.Json;
import play.mvc.Controller;
import play.mvc.Result;

public class Location extends Controller {

	private static PlayerLocationDAO locationDAO = new PlayerLocationDAO();
	private static PlayerDAO playerDAO = new PlayerDAO();
	
//	@BodyParser.Of(Json.class)
	public static Result updateLocation(String playerId, String lat, String lng, String uncertainty, String speed, String timestamp) {
		Player p = playerDAO.get(new ObjectId(playerId));
		if (p == null) {
			return badRequest();
		}
		
		PlayerLocation pl = new PlayerLocation();
		pl.setPlayer(p);
		pl.setLatitude(new Double(lat));
		pl.setLongitude(new Double(lng));
		
		if (uncertainty != null && !uncertainty.equals("")) 
			pl.setUncertainty(new Integer(uncertainty));
		
		if (speed != null && !speed.equals("")) 
			pl.setSpeed(new Integer(speed));
		
		pl.setTimestamp(new Date(new Long(timestamp)));
		locationDAO.save(pl);
		
		Map<String, String> d = new HashMap<String, String>();
		d.put("success", "foo");
		return ok(toJson(d));
	}

}
