package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.common.base.Objects;

/**
 * 
 * @author markus
 */
@Entity("cities")
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

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("name", name)
				.add("country", country)
				.add("latitude", latitude)
				.add("longitude", longitude)
				.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		City other = (City) obj;
		if (!this.getCountry().equals(other.getCountry()))
			return false;
		if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}

}
