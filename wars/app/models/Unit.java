package models;

import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;

@Entity("units")
public class Unit {

	@Id
	private ObjectId id;
	private UnitType type;
	private String name;
	private Integer minStrength;
	private Integer maxStrength;
	private Double chanceOfFailure;
	private Map<ResourceType, Integer> costs;
	
	@Reference
	private Place deployedAt;
	
	@Reference 
	private Player player;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public UnitType getType() {
		return type;
	}

	public void setType(UnitType type) {
		this.type = type;
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
	
	public Place getDeployedAt() {
		return deployedAt;
	}

	public void setDeployedAt(Place deployedAt) {
		this.deployedAt = deployedAt;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public Map<ResourceType, Integer> getCosts() {
		return costs;
	}

	public void setCosts(Map<ResourceType, Integer> costs) {
		this.costs = costs;
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
