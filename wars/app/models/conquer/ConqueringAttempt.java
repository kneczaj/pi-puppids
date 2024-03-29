package models.conquer;

import java.util.Date;
import java.util.Set;

import models.Player;

import org.bson.types.ObjectId;
import org.codehaus.jackson.node.ObjectNode;

import play.libs.Json;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.common.collect.Sets;

@Entity("conquering_attempts")
public class ConqueringAttempt {
	
	@Id
	private ObjectId id;
	
	private Player initiator;
	
	private String uuid;
	
	private String reference;
	
	private Date startDate;
	
	private Date endDate;
	
	private boolean canceled = false;
	
	private String placeName;
	
	private Double lat;
	
	private Double lng;
	
	private Set<Player> joiningMembers = Sets.newHashSet();

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public Player getInitiator() {
		return initiator;
	}

	public void setInitiator(Player initiator) {
		this.initiator = initiator;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Set<Player> getJoiningMembers() {
		return joiningMembers;
	}

	public void setJoiningMembers(Set<Player> joiningMembers) {
		this.joiningMembers = joiningMembers;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public ObjectNode toJson() {
		ObjectNode attempt = Json.newObject();
		attempt.put("id", id.toString());
		attempt.put("initiatorId", initiator.getId().toString());
		attempt.put("initiatorName", initiator.getUsername());
		attempt.put("uuid", uuid);
		attempt.put("reference", reference);
		attempt.put("startDate", startDate.getTime());
		if (endDate != null) {
			attempt.put("endDate", endDate.getTime());
		}
		attempt.put("canceled", canceled);
		attempt.put("joiningMemberCount", joiningMembers.size());
		
		return attempt;
	}
	
}
