package test.util;

import models.AmountType;
import models.Place;
import models.PlaceType;
import assets.constants.PlaceMappings;

public class SamplePlaces {
	
	public static Place tum = generateTUM();
	public static Place townHall = generateTownHall();
	
	private static Place generateTUM() {
		Place tum = new Place();
		
		tum.setType(PlaceType.university);
		
		tum.setResource(PlaceMappings.PLACE_TO_RESOURCE_MAP.get(tum.getType()));
		
		AmountType amountType = PlaceMappings.PLACE_TO_AMOUNT_MAP.get(tum.getType());
		tum.setAmount(PlaceMappings.AMOUNT_TYPE_TO_VALUE_MAP.get(amountType));
		
		tum.setName("Technische Universität München");
		
		tum.setLat(48.14766);
		tum.setLng(11.56757);
		
		return tum;
	}

	private static Place generateTownHall() {
		Place townHall = new Place();
		
		townHall.setType(PlaceType.city_hall);
		townHall.setResource(PlaceMappings.PLACE_TO_RESOURCE_MAP.get(townHall.getType()));
		
		AmountType amountType = PlaceMappings.PLACE_TO_AMOUNT_MAP.get(townHall.getType());
		townHall.setAmount(PlaceMappings.AMOUNT_TYPE_TO_VALUE_MAP.get(amountType));
		
		townHall.setName("New Town Hall");
		
		townHall.setLat(SampleLocations.NEW_TOWN_HALL.getLatitude());
		townHall.setLng(SampleLocations.NEW_TOWN_HALL.getLongitude());
		
		townHall.setUuid("28298fbc0d4fbd3f89500f00f93eb01ecffc3ce9");
		
		return townHall;
	}
	
}
