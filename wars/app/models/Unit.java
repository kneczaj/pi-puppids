package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity("units")
public class Unit {

	@Id
	private ObjectId id;
	private String name;
	private Integer minStrength;
	private Integer maxStrength;
	private Double chanceOfFailure;
	
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
	
	public Integer getMinStrength() {
		return minStrength;
	}
	
	public void setMinStrength(Integer minStrength) {
		this.minStrength = minStrength;
	}
	
	public Integer getMaxStrength() {
		return maxStrength;
	}
	
	public void setMaxStrength(Integer maxStrength) {
		this.maxStrength = maxStrength;
	}
	
	public Double getChanceOfFailure() {
		return chanceOfFailure;
	}
	
	public void setChanceOfFailure(Double chanceOfFailure) {
		this.chanceOfFailure = chanceOfFailure;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Unit[");
		sb.append("id: ");
		sb.append(id.toString());
		sb.append(", name: ");
		sb.append(name);
		sb.append(", minStrength: ");
		sb.append(minStrength);
		sb.append(", maxStrength: ");
		sb.append(maxStrength);
		sb.append(", chanceOfFailure: ");
		sb.append(chanceOfFailure);
		sb.append("]");
		
		return sb.toString();
	}
	
}
