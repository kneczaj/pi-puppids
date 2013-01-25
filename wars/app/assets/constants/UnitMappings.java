package assets.constants;

import java.util.Map;

import models.ResourceType;
import models.UnitType;

import com.google.common.collect.Maps;

public class UnitMappings {
	
	/**
	 * A mapping of unit types to their name
	 */
	public final static Map<UnitType, String> UNIT_NAME_MAP = generateUnitNameMap();
	
	/**
	 * A mapping of unit types to their costs
	 */
	public final static Map<UnitType, Map<ResourceType, Integer>> UNIT_COST_MAP = generateUnitCostMap();
	
	private static Map<UnitType, String> generateUnitNameMap() {
		Map<UnitType, String> map = Maps.newHashMap();
		
		map.put(UnitType.GRUNT, "Grunt");
		map.put(UnitType.INFANTRY, "Infantry");
		
		return map;
	}

	private static Map<UnitType, Map<ResourceType, Integer>> generateUnitCostMap() {
		Map<UnitType, Map<ResourceType, Integer>> costMap = Maps.newHashMap();
		
		Map<ResourceType, Integer> gruntCost = Maps.newHashMap();
		gruntCost.put(ResourceType.Credits, 100);
		gruntCost.put(ResourceType.Material, 150);
		
		Map<ResourceType, Integer> infantryCost = Maps.newHashMap();
		infantryCost.put(ResourceType.Credits, 200);
		infantryCost.put(ResourceType.Material, 170);
		
		costMap.put(UnitType.GRUNT, gruntCost);
		costMap.put(UnitType.INFANTRY, infantryCost);
		
		return costMap;
	}
}
