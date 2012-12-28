package services.google.places.api;

import java.util.List;

import models.PlaceType;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class RequestURL {

	private static final String GOOGLE_PLACES_API_URL = "https://maps.googleapis.com/maps/api/place";
	private static final String GOOGLE_PLACES_API_KEY = "AIzaSyCdrfRsWjuVuIxjonf0mFWeF3v5wztOfxI";
	
	public String reference;
	public String searchType;
	public String key = GOOGLE_PLACES_API_KEY;
	public List<String> keywords;
	public List<PlaceType> placeTypes;
	public Double latitude;
	public Double longitude;
	public Integer radius;

	public String toString() {
		StringBuilder sb = new StringBuilder(GOOGLE_PLACES_API_URL);
		sb.append("/" + this.searchType);
		sb.append("/json");
		sb.append("?sensor=false"); // location data is not coming from GPS
									// device => sensor=false
		
		if (reference != null) {
			sb.append("&reference=" + reference);
		}

		if (keywords != null) {
			sb.append("&keyword="
					+ Joiner.on(',').join(keywords).toString());
		}

		if (latitude != null) {
			sb.append("&location=" + String.valueOf(longitude) + ","
					+ String.valueOf(latitude));
		}

		if (radius != null) {
			sb.append("&radius=" + String.valueOf(radius));
		}

		if (placeTypes != null) {
			List<String> types = Lists.newArrayList();
			for (PlaceType pt : placeTypes) {
				types.add(pt.name());
			}

			sb.append("&types=" + Joiner.on(',').join(types).toString());
		}
		
		sb.append("&key=" + GOOGLE_PLACES_API_KEY);

		return sb.toString();
	}
}