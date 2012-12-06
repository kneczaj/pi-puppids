package controllers;

import static play.libs.Json.toJson;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import models.Player;
import models.PlayerLocation;

import org.bson.types.ObjectId;

import play.mvc.Controller;
import play.mvc.Result;

import communication.ClientPushActor;

import daos.PlayerDAO;
import daos.PlayerLocationDAO;

public class Location extends Controller {

	private static PlayerLocationDAO locationDAO = new PlayerLocationDAO();
	private static PlayerDAO playerDAO = new PlayerDAO();

	public static Result updateLocation(String playerId, String lat,
			String lng, String uncertainty, String speed, String timestamp) {
		
		Player p = playerDAO.get(new ObjectId(playerId));
		if (p == null) {
			return badRequest();
		}
		
		Double latitude = new Double(lat);
		Double longitude = new Double(lng);
		Integer sp = (speed == null || speed.equals("")) ? null : new Integer(speed);
		Integer accuracy = (uncertainty == null || uncertainty.equals("")) ? null : new Integer(uncertainty);
		
		PlayerLocation pl = new PlayerLocation();
		pl.setPlayer(p);
		pl.setLatitude(latitude);
		pl.setLongitude(longitude);
		pl.setUncertainty(accuracy);
		pl.setSpeed(sp);
		pl.setTimestamp(new Date(new Long(timestamp)));
		locationDAO.save(pl);

		// inform other player of location change
		ClientPushActor.locationChanged(pl.getId().toString(), new Long(timestamp), longitude, latitude, accuracy, sp);
		
		Map<String, String> d = new HashMap<String, String>();
		d.put("success", "foo");
		return ok(toJson(d));
	}
}
