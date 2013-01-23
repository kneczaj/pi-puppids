package test.util;

import models.AmountType;
import models.Place;
import models.PlaceType;
import assets.constants.PlaceMappings;

public class SamplePlaces {
	
	public static Place tum = generateTUM();
	
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

}
