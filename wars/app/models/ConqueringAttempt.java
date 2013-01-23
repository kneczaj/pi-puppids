package models;

import java.util.Date;
import java.util.Set;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;
import com.google.common.collect.Sets;

public class ConqueringAttempt {
	
	@Id
	private ObjectId id;
	
	private Player initiator;
	
	private String uuid;
	
	private Date startDate;
	
	private Date endDate;
	
	private boolean canceled = false;
	
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
	
}
