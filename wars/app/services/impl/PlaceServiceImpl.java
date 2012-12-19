package services.impl;

import models.AmountType;
import models.PlaceType;
import models.ResourceType;
import services.api.PlaceService;
import assets.constants.Mappings;

public class PlaceServiceImpl implements PlaceService {

	@Override
	public ResourceType getResourceType(PlaceType placeType) {
		return Mappings.PLACE_TO_RESOURCE_MAP.get(placeType);
	}

	@Override
	public Integer getResourceAmount(PlaceType placeType) {
		AmountType amount = Mappings.PLACE_TO_AMOUNT_MAP.get(placeType);
		return Mappings.AMOUNT_TYPE_TO_VALUE_MAP.get(amount);
	}

}
