package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Indexed;

@Entity
public class Photo {
	
	@Id 
	private ObjectId id;
	
	@Indexed
	private String identifier;
	
	private byte[] thumbnail;
	
	@Indexed
	private String filename;
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}
	
	public byte[] getThumbnail() {
		return thumbnail;
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
	
	@Override
	public String toString() {
		return "Photo[identifier: " + identifier + ", filename=" + filename + "]";
	}
	
}