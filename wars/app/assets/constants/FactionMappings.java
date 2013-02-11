package assets.constants;

import java.util.Map;

import models.FactionName;

import com.google.common.collect.Maps;

public class FactionMappings {
	public static Map<FactionName, String> FACTION_NAME_MAP = generateFactionNameMap();
	
	private static Map<FactionName, String> generateFactionNameMap() {
		Map<FactionName, String> factionNameMap = Maps.newHashMap();
		factionNameMap.put(FactionName.BLUE, "Blue");
		factionNameMap.put(FactionName.RED, "Red");
		return factionNameMap;
	}
}
