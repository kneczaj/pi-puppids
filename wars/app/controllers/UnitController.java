package controllers;

import java.util.Map;

import models.Player;
import models.UnitType;
import play.mvc.Controller;
import play.mvc.Result;
import securesocial.core.java.SecureSocial;
import services.api.AuthenticationService;
import services.api.UnitService;
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
}
