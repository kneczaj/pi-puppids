package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.common.base.Objects;

/**
 * 
 * @author markus
 */
@Entity("factions")
public class Faction {

	@Id
	private ObjectId id;
	private String name;
	private Integer score;

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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id.toString())
				.add("name", name)
				.add("score", score).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Faction other = (Faction) obj;
		if (!this.getName().equals(other.getName()))
			return false;
		return true;
	}
}
