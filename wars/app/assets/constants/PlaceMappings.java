package assets.constants;

import java.util.Map;

import models.AmountType;
import models.PlaceType;
import models.ResourceType;

import com.google.common.collect.Maps;

public class PlaceMappings {
	
	/**
	 * Map of google maps place types to resource types
	 */
	public final static Map<PlaceType, ResourceType> PLACE_TO_RESOURCE_MAP = generatePlaceToResourceMap();
	
	/**
	 * Map of resource amount categories to explicit amounts
	 */
	public final static Map<AmountType, Integer> AMOUNT_TYPE_TO_VALUE_MAP = generateAmountTypeToValueMap();
	
	/**
	 * Map of PlaceTypes to an amount categorie
	 */
	public final static Map<PlaceType, AmountType> PLACE_TO_AMOUNT_MAP = generatePlaceToAmountMap();
	
	/**
	 * Map of PlaceTypes to their resource demand
	 */
	public final static Map<PlaceType, Map<ResourceType, Integer>> PLACE_TO_RESOURCE_DEMAND_MAP = generatePlaceToResourceDemandMap();
	
	private static Map<PlaceType, ResourceType> generatePlaceToResourceMap() {
		Map<PlaceType, ResourceType> map = Maps.newHashMap();
		
		map.put(PlaceType.airport, ResourceType.Special);
		map.put(PlaceType.amusement_park, ResourceType.Cultural);
		map.put(PlaceType.aquarium, ResourceType.Cultural);
		map.put(PlaceType.art_gallery, ResourceType.Cultural);
		map.put(PlaceType.atm, ResourceType.Credits);
		map.put(PlaceType.bakery, ResourceType.Food);
		map.put(PlaceType.bank, ResourceType.Credits);
		map.put(PlaceType.bar, ResourceType.Food);
		map.put(PlaceType.beauty_salon, ResourceType.Cultural);
		map.put(PlaceType.bicycle_store, ResourceType.Material);
		map.put(PlaceType.book_store, ResourceType.Knowledge);
		map.put(PlaceType.bowling_alley, ResourceType.Cultural);
		map.put(PlaceType.bus_station, ResourceType.Transportation);
		map.put(PlaceType.cafe, ResourceType.Food);
		map.put(PlaceType.campground, ResourceType.Special);
		map.put(PlaceType.car_dealer, ResourceType.Transportation);
		map.put(PlaceType.car_rental, ResourceType.Transportation);
		map.put(PlaceType.car_repair, ResourceType.Transportation);
		map.put(PlaceType.car_wash, ResourceType.Transportation);
		map.put(PlaceType.casino, ResourceType.Credits);
		map.put(PlaceType.cemetery, ResourceType.Special);
		map.put(PlaceType.church, ResourceType.Special);
		map.put(PlaceType.city_hall, ResourceType.Special);
		map.put(PlaceType.clothing_store, ResourceType.Material);
		map.put(PlaceType.convenience_store, ResourceType.Material);
		map.put(PlaceType.courthouse, ResourceType.Special);
		map.put(PlaceType.dentist, ResourceType.Credits);
		map.put(PlaceType.department_store, ResourceType.Material);
		map.put(PlaceType.doctor, ResourceType.Credits);
		map.put(PlaceType.electrician, ResourceType.Credits);
		map.put(PlaceType.electronics_store, ResourceType.Material);
		map.put(PlaceType.embassy, ResourceType.Special);
		map.put(PlaceType.establishment, ResourceType.Credits);
		map.put(PlaceType.finance, ResourceType.Credits);
		map.put(PlaceType.fire_station, ResourceType.Special);
		map.put(PlaceType.florist, ResourceType.Credits);
		map.put(PlaceType.food, ResourceType.Food);
		map.put(PlaceType.funeral_home, ResourceType.Material);
		map.put(PlaceType.furniture_store, ResourceType.Material);
		map.put(PlaceType.gas_station, ResourceType.Material);
		map.put(PlaceType.general_contractor, ResourceType.Material);
		map.put(PlaceType.grocery_or_supermarket, ResourceType.Material);
		map.put(PlaceType.gym, ResourceType.Material);
		map.put(PlaceType.hair_care, ResourceType.Material);
		map.put(PlaceType.hardware_store, ResourceType.Material);
		map.put(PlaceType.health, ResourceType.Material);
		map.put(PlaceType.hindu_temple, ResourceType.Special);
		map.put(PlaceType.home_goods_store, ResourceType.Material);
		map.put(PlaceType.hospital, ResourceType.Material);
		map.put(PlaceType.insurance_agency, ResourceType.Credits);
		map.put(PlaceType.jewelry_store, ResourceType.Credits);
		map.put(PlaceType.laundry, ResourceType.Material);
		map.put(PlaceType.lawyer, ResourceType.Credits);
		map.put(PlaceType.library, ResourceType.Knowledge);
		map.put(PlaceType.liquor_store, ResourceType.Food);
		map.put(PlaceType.local_government_office, ResourceType.Special);
		map.put(PlaceType.locksmith, ResourceType.Material);
		map.put(PlaceType.lodging, ResourceType.Material);
		map.put(PlaceType.meal_delivery, ResourceType.Food);
		map.put(PlaceType.meal_takeaway, ResourceType.Food);
		map.put(PlaceType.mosque, ResourceType.Special);
		map.put(PlaceType.movie_rental, ResourceType.Cultural);
		map.put(PlaceType.movie_theater, ResourceType.Cultural);
		map.put(PlaceType.moving_company, ResourceType.Cultural);
		map.put(PlaceType.museum, ResourceType.Cultural);
		map.put(PlaceType.night_club, ResourceType.Cultural);
		map.put(PlaceType.painter, ResourceType.Material);
		map.put(PlaceType.park, ResourceType.Cultural);
		map.put(PlaceType.parking, ResourceType.Transportation);
		map.put(PlaceType.pet_store, ResourceType.Material);
		map.put(PlaceType.pharmacy, ResourceType.Material);
		map.put(PlaceType.physiotherapist, ResourceType.Material);
		map.put(PlaceType.place_of_worship, ResourceType.Special);
		map.put(PlaceType.plumber, ResourceType.Material);
		map.put(PlaceType.police, ResourceType.Special);
		map.put(PlaceType.post_office, ResourceType.Material);
		map.put(PlaceType.real_estate_agency, ResourceType.Material);
		map.put(PlaceType.restaurant, ResourceType.Food);
		map.put(PlaceType.roofing_contractor, ResourceType.Material);
		map.put(PlaceType.rv_park, ResourceType.Material);
		map.put(PlaceType.school, ResourceType.Knowledge);
		map.put(PlaceType.shoe_store, ResourceType.Material);
		map.put(PlaceType.shopping_mall, ResourceType.Food);
		map.put(PlaceType.spa, ResourceType.Material);
		map.put(PlaceType.stadium, ResourceType.Special);
		map.put(PlaceType.storage, ResourceType.Material);
		map.put(PlaceType.store, ResourceType.Food);
		map.put(PlaceType.subway_station, ResourceType.Transportation);
		map.put(PlaceType.synagogue, ResourceType.Special);
		map.put(PlaceType.taxi_stand, ResourceType.Transportation);
		map.put(PlaceType.train_station, ResourceType.Transportation);
		map.put(PlaceType.travel_agency, ResourceType.Transportation);
		map.put(PlaceType.university, ResourceType.Knowledge);
		map.put(PlaceType.veterinary_care, ResourceType.Material);
		map.put(PlaceType.zoo, ResourceType.Special);
		
		return map;
	}

	/**
	 * Change Mappings from AmountTypes to a numeric value here!
	 */
	private static Map<AmountType, Integer> generateAmountTypeToValueMap() {
		Map<AmountType, Integer> map = Maps.newHashMap();
		
		map.put(AmountType.VERY_LOW, 10);
		map.put(AmountType.LOW, 25);
		map.put(AmountType.MIDDLE, 50);
		map.put(AmountType.HIGH, 75);
		map.put(AmountType.EXTREME, 100);
		
		return map;
	}

	private static Map<PlaceType, AmountType> generatePlaceToAmountMap() {
		Map<PlaceType, AmountType> map = Maps.newHashMap();
		
		map.put(PlaceType.airport, AmountType.EXTREME);
		map.put(PlaceType.amusement_park, AmountType.HIGH);
		map.put(PlaceType.aquarium, AmountType.HIGH);
		map.put(PlaceType.art_gallery, AmountType.MIDDLE);
		map.put(PlaceType.atm, AmountType.VERY_LOW);
		map.put(PlaceType.bakery, AmountType.MIDDLE);
		map.put(PlaceType.bank, AmountType.LOW);
		map.put(PlaceType.bar, AmountType.LOW);
		map.put(PlaceType.beauty_salon, AmountType.MIDDLE);
		map.put(PlaceType.bicycle_store, AmountType.MIDDLE);
		map.put(PlaceType.book_store, AmountType.MIDDLE);
		map.put(PlaceType.bowling_alley, AmountType.MIDDLE);
		map.put(PlaceType.bus_station, AmountType.VERY_LOW);
		map.put(PlaceType.cafe, AmountType.LOW);
		map.put(PlaceType.campground, AmountType.MIDDLE);
		map.put(PlaceType.car_dealer, AmountType.MIDDLE);
		map.put(PlaceType.car_rental, AmountType.MIDDLE);
		map.put(PlaceType.car_repair, AmountType.MIDDLE);
		map.put(PlaceType.car_wash, AmountType.MIDDLE);
		map.put(PlaceType.casino, AmountType.MIDDLE);
		map.put(PlaceType.cemetery, AmountType.MIDDLE);
		map.put(PlaceType.church, AmountType.MIDDLE);
		map.put(PlaceType.city_hall, AmountType.HIGH);
		map.put(PlaceType.clothing_store, AmountType.MIDDLE);
		map.put(PlaceType.convenience_store, AmountType.MIDDLE);
		map.put(PlaceType.courthouse, AmountType.MIDDLE);
		map.put(PlaceType.dentist, AmountType.MIDDLE);
		map.put(PlaceType.department_store, AmountType.MIDDLE);
		map.put(PlaceType.doctor, AmountType.MIDDLE);
		map.put(PlaceType.electrician, AmountType.MIDDLE);
		map.put(PlaceType.electronics_store, AmountType.MIDDLE);
		map.put(PlaceType.embassy, AmountType.HIGH);
		map.put(PlaceType.establishment, AmountType.MIDDLE);
		map.put(PlaceType.finance, AmountType.MIDDLE);
		map.put(PlaceType.fire_station, AmountType.MIDDLE);
		map.put(PlaceType.florist, AmountType.MIDDLE);
		map.put(PlaceType.food, AmountType.MIDDLE);
		map.put(PlaceType.funeral_home, AmountType.MIDDLE);
		map.put(PlaceType.furniture_store, AmountType.MIDDLE);
		map.put(PlaceType.gas_station, AmountType.MIDDLE);
		map.put(PlaceType.general_contractor, AmountType.MIDDLE);
		map.put(PlaceType.grocery_or_supermarket, AmountType.MIDDLE);
		map.put(PlaceType.gym, AmountType.MIDDLE);
		map.put(PlaceType.hair_care, AmountType.MIDDLE);
		map.put(PlaceType.hardware_store, AmountType.MIDDLE);
		map.put(PlaceType.health, AmountType.MIDDLE);
		map.put(PlaceType.hindu_temple, AmountType.MIDDLE);
		map.put(PlaceType.home_goods_store, AmountType.MIDDLE);
		map.put(PlaceType.hospital, AmountType.HIGH);
		map.put(PlaceType.insurance_agency, AmountType.MIDDLE);
		map.put(PlaceType.jewelry_store, AmountType.MIDDLE);
		map.put(PlaceType.laundry, AmountType.MIDDLE);
		map.put(PlaceType.lawyer, AmountType.MIDDLE);
		map.put(PlaceType.library, AmountType.HIGH);
		map.put(PlaceType.liquor_store, AmountType.MIDDLE);
		map.put(PlaceType.local_government_office, AmountType.MIDDLE);
		map.put(PlaceType.locksmith, AmountType.MIDDLE);
		map.put(PlaceType.lodging, AmountType.MIDDLE);
		map.put(PlaceType.meal_delivery, AmountType.MIDDLE);
		map.put(PlaceType.meal_takeaway, AmountType.MIDDLE);
		map.put(PlaceType.mosque, AmountType.MIDDLE);
		map.put(PlaceType.movie_rental, AmountType.MIDDLE);
		map.put(PlaceType.movie_theater, AmountType.MIDDLE);
		map.put(PlaceType.moving_company, AmountType.MIDDLE);
		map.put(PlaceType.museum, AmountType.MIDDLE);
		map.put(PlaceType.night_club, AmountType.MIDDLE);
		map.put(PlaceType.painter, AmountType.MIDDLE);
		map.put(PlaceType.park, AmountType.MIDDLE);
		map.put(PlaceType.parking, AmountType.MIDDLE);
		map.put(PlaceType.pet_store, AmountType.MIDDLE);
		map.put(PlaceType.pharmacy, AmountType.MIDDLE);
		map.put(PlaceType.physiotherapist, AmountType.MIDDLE);
		map.put(PlaceType.place_of_worship, AmountType.MIDDLE);
		map.put(PlaceType.plumber, AmountType.MIDDLE);
		map.put(PlaceType.police, AmountType.MIDDLE);
		map.put(PlaceType.post_office, AmountType.MIDDLE);
		map.put(PlaceType.real_estate_agency, AmountType.MIDDLE);
		map.put(PlaceType.restaurant, AmountType.MIDDLE);
		map.put(PlaceType.roofing_contractor, AmountType.MIDDLE);
		map.put(PlaceType.rv_park, AmountType.MIDDLE);
		map.put(PlaceType.school, AmountType.MIDDLE);
		map.put(PlaceType.shoe_store, AmountType.MIDDLE);
		map.put(PlaceType.shopping_mall, AmountType.MIDDLE);
		map.put(PlaceType.spa, AmountType.MIDDLE);
		map.put(PlaceType.stadium, AmountType.MIDDLE);
		map.put(PlaceType.storage, AmountType.MIDDLE);
		map.put(PlaceType.store, AmountType.MIDDLE);
		map.put(PlaceType.subway_station, AmountType.MIDDLE);
		map.put(PlaceType.synagogue, AmountType.MIDDLE);
		map.put(PlaceType.taxi_stand, AmountType.MIDDLE);
		map.put(PlaceType.train_station, AmountType.MIDDLE);
		map.put(PlaceType.travel_agency, AmountType.MIDDLE);
		map.put(PlaceType.university, AmountType.EXTREME);
		map.put(PlaceType.veterinary_care, AmountType.MIDDLE);
		map.put(PlaceType.zoo, AmountType.MIDDLE);
		
		return map;
	}
	
	private static Map<PlaceType, Map<ResourceType, Integer>> generatePlaceToResourceDemandMap() {
		Map<PlaceType, Map<ResourceType, Integer>> map = Maps.newHashMap();
		
		Map<ResourceType, Integer> noResourceNeededMap = Maps.newHashMap();
		noResourceNeededMap.put(ResourceType.Credits, 0);
		noResourceNeededMap.put(ResourceType.Material, 0);
		
		//generate the map
		for (PlaceType type : PlaceType.values()) {
			
			//special places need resources
			if (PLACE_TO_RESOURCE_MAP.get(type) == ResourceType.Special) {
				Map<ResourceType, Integer> resourceMap = Maps.newHashMap();
				
				AmountType amountType = PLACE_TO_AMOUNT_MAP.get(type);
				Integer amount = AMOUNT_TYPE_TO_VALUE_MAP.get(amountType);
				
				resourceMap.put(ResourceType.Credits, amount);
				resourceMap.put(ResourceType.Material, amount);
				
				map.put(type, resourceMap);
			} else {
				map.put(type, noResourceNeededMap);
			}
		}
		
		return map;
	}
}
