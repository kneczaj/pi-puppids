package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Id;

public class BaseModel {
	
	@Id
	private ObjectId id;
	
	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

}
