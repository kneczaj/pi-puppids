package models;

import java.util.Date;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;

@Entity("player_locations")
public class PlayerLocation {

	@Id
	private ObjectId id; 
	private Double latitude;
	private Double longitude;
	private Date timestamp;
	
	/**
	 * The uncertainty of the location measurement in meters.
	 */
	private Integer uncertainty;
	
	/**
	 * The traveling speed in meters per second.
	 */
	private Integer speed;
	
	@Reference
	private Player player;

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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getUncertainty() {
		return uncertainty;
	}

	public void setUncertainty(Integer uncertainty) {
		this.uncertainty = uncertainty;
	}

	public Integer getSpeed() {
		return speed;
	}

	public void setSpeed(Integer speed) {
		this.speed = speed;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getId() {
		return id;
	}
	
}
