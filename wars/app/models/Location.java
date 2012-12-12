package models;

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

}
