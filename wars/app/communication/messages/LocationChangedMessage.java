package communication.messages;

/**
 * Is sent to the ClientPushActor if the location of a player changed.
 * 
 * @author markus
 */
public class LocationChangedMessage {

	private final String id;

	private final Long timestamp;

	private final Double longitude;

	private final Double latitude;
	
	private final Integer accuracy;
	
	private final Integer speed;

	public LocationChangedMessage(String id, Long timestamp, Double longitude,
			Double latitude, Integer accuracy, Integer speed) {
		this.id = id;
		this.timestamp = timestamp;
		this.longitude = longitude;
		this.latitude = latitude;
		this.accuracy = accuracy;
		this.speed = speed;
	}

	public String getId() {
		return id;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Integer getAccuracy() {
		return accuracy;
	}

	public Integer getSpeed() {
		return speed;
	}

}