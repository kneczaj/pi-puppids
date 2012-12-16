package assets.constants;

import java.util.Map;

import com.google.common.collect.Maps;

import models.PlaceType;
import models.ResourceType;

public class Mappings {
	public final static Map<PlaceType, ResourceType> PLACE_TO_RESOURCE_MAP = generatePlaceToResourceMap();
	
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
		map.put(PlaceType.funeral_home, ResourceType.Special);
		map.put(PlaceType.furniture_store, ResourceType.Material);
		map.put(PlaceType.gas_station, ResourceType.Material);
		map.put(PlaceType.general_contractor, ResourceType.Material);
		map.put(PlaceType.grocery_or_supermarket, ResourceType.Material);
		map.put(PlaceType.gym, ResourceType.Special);
		map.put(PlaceType.hair_care, ResourceType.Special);
		map.put(PlaceType.hardware_store, ResourceType.Material);
		map.put(PlaceType.health, ResourceType.Special);
		map.put(PlaceType.hindu_temple, ResourceType.Special);
		map.put(PlaceType.home_goods_store, ResourceType.Material);
		map.put(PlaceType.hospital, ResourceType.Special);
		map.put(PlaceType.insurance_agency, ResourceType.Credits);
		map.put(PlaceType.jewelry_store, ResourceType.Special);
		map.put(PlaceType.laundry, ResourceType.Special);
		map.put(PlaceType.lawyer, ResourceType.Special);
		map.put(PlaceType.library, ResourceType.Knowledge);
		map.put(PlaceType.liquor_store, ResourceType.Food);
		map.put(PlaceType.local_government_office, ResourceType.Special);
		map.put(PlaceType.locksmith, ResourceType.Special);
		map.put(PlaceType.lodging, ResourceType.Special);
		map.put(PlaceType.meal_delivery, ResourceType.Food);
		map.put(PlaceType.meal_takeaway, ResourceType.Food);
		map.put(PlaceType.mosque, ResourceType.Special);
		map.put(PlaceType.movie_rental, ResourceType.Cultural);
		map.put(PlaceType.movie_theater, ResourceType.Cultural);
		map.put(PlaceType.moving_company, ResourceType.Cultural);
		map.put(PlaceType.museum, ResourceType.Cultural);
		map.put(PlaceType.night_club, ResourceType.Cultural);
		map.put(PlaceType.painter, ResourceType.Special);
		map.put(PlaceType.park, ResourceType.Cultural);
		map.put(PlaceType.parking, ResourceType.Transportation);
		map.put(PlaceType.pet_store, ResourceType.Special);
		map.put(PlaceType.pharmacy, ResourceType.Special);
		map.put(PlaceType.physiotherapist, ResourceType.Special);
		map.put(PlaceType.place_of_worship, ResourceType.Special);
		map.put(PlaceType.plumber, ResourceType.Special);
		map.put(PlaceType.police, ResourceType.Special);
		map.put(PlaceType.post_office, ResourceType.Special);
		map.put(PlaceType.real_estate_agency, ResourceType.Special);
		map.put(PlaceType.restaurant, ResourceType.Food);
		map.put(PlaceType.roofing_contractor, ResourceType.Material);
		map.put(PlaceType.rv_park, ResourceType.Special);
		map.put(PlaceType.school, ResourceType.Knowledge);
		map.put(PlaceType.shoe_store, ResourceType.Special);
		map.put(PlaceType.shopping_mall, ResourceType.Food);
		map.put(PlaceType.spa, ResourceType.Special);
		map.put(PlaceType.stadium, ResourceType.Special);
		map.put(PlaceType.storage, ResourceType.Special);
		map.put(PlaceType.store, ResourceType.Food);
		map.put(PlaceType.subway_station, ResourceType.Transportation);
		map.put(PlaceType.synagogue, ResourceType.Special);
		map.put(PlaceType.taxi_stand, ResourceType.Transportation);
		map.put(PlaceType.train_station, ResourceType.Transportation);
		map.put(PlaceType.travel_agency, ResourceType.Special);
		map.put(PlaceType.university, ResourceType.Knowledge);
		map.put(PlaceType.veterinary_care, ResourceType.Special);
		map.put(PlaceType.zoo, ResourceType.Special);
		
		return map;
	}
}
