package models;

import com.google.common.base.Objects;

/**
 * Representation for a Location
 * 
 * @author markus
 */
public class Location {

	private Double longitude;

	private Double latitude;
	
	private Double longitudeRadians;
	
	private Double latitudeRadians;
	
	public Location() {
		
	}

	public Location(Double longitude, Double latitude) {
		setLatitude(latitude);
		setLongitude(longitude);
	}
	
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
		this.longitudeRadians = Math.toRadians(longitude);
	}

	public Double getLongitude() {
		return longitude;
	}
	
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
		this.latitudeRadians = Math.toRadians(latitude);
	}
	
	public Double getLatitudeRadians() {
		return latitudeRadians;
	}
	
	public Double getLongitudeRadians() {
		return longitudeRadians;
	}

	public Double getLatitude() {
		return latitude;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result
				+ ((longitude == null) ? 0 : longitude.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Location other = (Location) obj;
		if (latitude == null) {
			if (other.latitude != null)
				return false;
		} else if (!latitude.equals(other.latitude))
			return false;
		if (longitude == null) {
			if (other.longitude != null)
				return false;
		} else if (!longitude.equals(other.longitude))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("longitude", longitude)
				.add("latitude", latitude)
				.toString();
	}

}
