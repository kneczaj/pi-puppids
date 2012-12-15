package models;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;

@Entity("units")
public class Unit {

	@Id
	private ObjectId id;
	private String name;
	private Integer minStrength;
	private Integer maxStrength;
	private Double chanceOfFailure;
	
	@Reference 
	private Player player;

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
	
	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("name", name)
				.add("chanceOfFailure", chanceOfFailure)
				.add("player", player)
				.toString();
	}

}
