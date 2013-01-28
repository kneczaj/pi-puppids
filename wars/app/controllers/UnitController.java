package controllers;

import java.util.Map;

import models.Player;
import models.UnitType;
import play.Logger;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.UnitService;
import services.api.error.UnitServiceException;
import util.JsonHelper;

import com.google.common.collect.Maps;
import com.google.inject.Inject;

/**
 * Controller for the UnitService
 * 
 * @author Peter
 *
 */
public class UnitController extends Controller {
	
	@Inject
	private static AuthenticationService authenticationService;
	
	@Inject
	private static UnitService unitService;
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result getUnitsOfPlayer() {
		Player p = authenticationService.getPlayer();
		
		Map<String, Integer> undeployedUnitsMap = Maps.newHashMap();
		Map<String, Integer> overallUnitsMap = Maps.newHashMap();
		
		for (UnitType type : UnitType.values()) {
			undeployedUnitsMap.put(type.toString(), unitService.getNumberOfUndeployedUnits(p, type));
			overallUnitsMap.put(type.toString(), unitService.getNumberOfDeployedUnits(p, type) + unitService.getNumberOfUndeployedUnits(p, type));
		}
		
		Map<String, Object> ret = Maps.newHashMap();
		ret.put("undeployedUnits", undeployedUnitsMap);
		ret.put("overallUnits", overallUnitsMap);
		
		return ok(JsonHelper.toJson(ret));
	}
	
	@SecureSocial.SecuredAction(ajaxCall=true)
	public static Result buildUnits(String gruntAmount, String infantryAmount) {
		Player player = authenticationService.getPlayer();
		int ga = Integer.parseInt(gruntAmount);
		int ia = Integer.parseInt(infantryAmount);
		
		Logger.debug("player tries to build " + gruntAmount + " new Grunts and " + infantryAmount + " new Infantry");

		try {
			if (ga > 0) {
				unitService.buildUnit(player, UnitType.GRUNT, ga);
			}
			if (ia > 0) {
				unitService.buildUnit(player, UnitType.INFANTRY, ia);
			}
			
			return ok("ok");
		} catch (UnitServiceException e) {
			Logger.info(e.toString());
			
			return ok("error");
		}
	}
}
