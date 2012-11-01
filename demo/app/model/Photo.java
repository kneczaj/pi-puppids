package model;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity
public class Photo {
	
	@Id 
	private ObjectId id;
	
	private String identifier;
	
	private String filename;
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public ObjectId getId() {
		return id;
	}

	public String getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	public String getFilename() {
		return filename;
	}
	
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
}