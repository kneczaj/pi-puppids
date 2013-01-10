package models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;

public class ConqueringAttempt {
	
	@Id
	private ObjectId id;
	
	private Player initiator;
	
	private Place place;
	
	private Date startDate;
	
	private Date endDate;
	
	private List<Player> joiningMembers;

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

	public Place getPlace() {
		return place;
	}

	public void setPlace(Place place) {
		this.place = place;
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

	public List<Player> getJoiningMembers() {
		return joiningMembers;
	}

	public void setJoiningMembers(List<Player> joiningMembers) {
		this.joiningMembers = joiningMembers;
	}
	
}
