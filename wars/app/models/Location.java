package models;

import com.google.common.base.Objects;

/**
 * Representation for a Location
 * 
 * @author markus
 */
public class Location {

	private final Double longitude;
	private final Double latitude;

	public Location(Double longitude, Double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("longitude", longitude)
				.add("latitude", latitude)
				.toString();
	}

}
