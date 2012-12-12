package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;

public class ResourceDepot {

	@Id
	private ObjectId id;
	private ResourceType resource;
	private Integer amount;
	
	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}
	public ResourceType getResource() {
		return resource;
	}
	public void setResource(ResourceType resource) {
		this.resource = resource;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
}
