package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;
import com.google.common.base.Objects;

/**
 * 
 * @author markus
 */
public class City {

	@Id
	private ObjectId id;
	private String name;
	private String country;
	private Double latitude;
	private Double longitude;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("name", name)
				.add("country", country)
				.add("latitude", latitude)
				.add("longitude", longitude)
				.toString();
	}

}
