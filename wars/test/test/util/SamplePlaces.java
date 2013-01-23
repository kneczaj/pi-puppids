package test.util;

import java.util.List;

import models.AmountType;
import models.GPlace;
import models.Place;
import models.PlaceType;
import assets.constants.PlaceMappings;

import com.google.common.collect.Lists;

public class SamplePlaces {

	public static Place tum = generateTUM();
	public static Place townHall = generateTownHall();
	public static GPlace newTownHall = generateNewTownHall();

	private static Place generateTUM() {
		Place tum = new Place();

		tum.setType(PlaceType.university);

		tum.setResource(PlaceMappings.PLACE_TO_RESOURCE_MAP.get(tum.getType()));

		AmountType amountType = PlaceMappings.PLACE_TO_AMOUNT_MAP.get(tum
				.getType());
		tum.setAmount(PlaceMappings.AMOUNT_TYPE_TO_VALUE_MAP.get(amountType));

		tum.setName("Technische Universität München");

		tum.setLat(48.14766);
		tum.setLng(11.56757);

		return tum;
	}

	private static Place generateTownHall() {
		Place townHall = new Place();

		townHall.setType(PlaceType.city_hall);
		townHall.setResource(PlaceMappings.PLACE_TO_RESOURCE_MAP.get(townHall
				.getType()));

		AmountType amountType = PlaceMappings.PLACE_TO_AMOUNT_MAP.get(townHall
				.getType());
		townHall.setAmount(PlaceMappings.AMOUNT_TYPE_TO_VALUE_MAP
				.get(amountType));

		townHall.setName("New Town Hall");

		townHall.setLat(SampleLocations.NEW_TOWN_HALL.getLatitude());
		townHall.setLng(SampleLocations.NEW_TOWN_HALL.getLongitude());

		townHall.setUuid("28298fbc0d4fbd3f89500f00f93eb01ecffc3ce9");

		return townHall;
	}

	private static GPlace generateNewTownHall() {
		GPlace newTownHall = new GPlace();

		newTownHall.setUuid("28298fbc0d4fbd3f89500f00f93eb01ecffc3ce9");
		newTownHall
				.setReference("CnRrAAAA0s0h7O0QOYqZGfpnGEDzn5lGPK_PXAc4XjV_mbkFBs2DJD-fDhjXNN3LTvO8YIrhF0pWbI3PKMuLJ7tXXCxRTdzzynGHb5peS31X8FwLaVC78TaQce89KAfzEgANOzqt6kbsTqtLjw3qA1iIGlVskhIQZxmkeKlIY-cmadI-ruyc2hoUGF813yeqOwn94_P6x07n4RPzRt8");

		newTownHall.setName("New Town Hall");
		newTownHall.setLatitude(SampleLocations.NEW_TOWN_HALL.getLatitude());
		newTownHall.setLongitude(SampleLocations.NEW_TOWN_HALL.getLongitude());
		
		List<String> types = Lists.newLinkedList();
		types.add("city_hall");
        types.add("local_government_office");
        types.add("locality");
        types.add("political");
        types.add("establishment");
		
		newTownHall.setTypes(types);
		
		return newTownHall;
	}

}
