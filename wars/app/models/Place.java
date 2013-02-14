package models;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

@Entity("places")
public class Place {

	@Id
	private ObjectId id;
	private String uuid;
	private String name;
	private Double lat;
	private Double lng;
	private PlaceType type;
	private Integer amount;
	private ResourceType resource;
	private Map<ResourceType, Integer> resourceDemand;
	private Integer numberOfConquerors;
	private String reference;
	
	@Reference
	private List<Player> conqueredBy = Lists.newArrayList();
	
	@Reference
	private List<Unit> deployedUnits = Lists.newLinkedList();
	
	public ObjectId getId() {
		return id;
	}
	
	public String getIdString() {
		return this.id.toString();
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
	
	public Double getLat() {
		return lat;
	}
	
	public void setLat(Double lat) {
		this.lat = lat;
	}
	
	public Double getLng() {
		return lng;
	}
	
	public void setLng(Double lng) {
		this.lng = lng;
	}
	
	public PlaceType getType() {
		return type;
	}
	
	public void setType(PlaceType type) {
		this.type = type;
	}
	
	public Integer getAmount() {
		return amount;
	}
	
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
	
	public ResourceType getResource() {
		return resource;
	}

	public void setResource(ResourceType resource) {
		this.resource = resource;
	}

	public List<Player> getConqueredBy() {
		return conqueredBy;
	}

	public void setConqueredBy(List<Player> conqueredBy) {
		this.conqueredBy = conqueredBy;
	}
	
	public List<Unit> getDeployedUnits() {
		return deployedUnits;
	}

	public void setDeployedUnits(List<Unit> deployedUnits) {
		this.deployedUnits = deployedUnits;
	}
	
	public Map<ResourceType, Integer> getResourceDemand() {
		return resourceDemand;
	}

	public void setResourceDemand(Map<ResourceType, Integer> demand) {
		this.resourceDemand = demand;
	}
	
	public Integer getNumberOfConquerors() {
		return numberOfConquerors;
	}

	public void setNumberOfConquerors(Integer numberOfConquerors) {
		this.numberOfConquerors = numberOfConquerors;
	}
	
	public void removePlayerFromConquerors(Player player) {
		this.conqueredBy.remove(player);
	}

	@Override
	public String toString() {
		Joiner joiner = Joiner.on(", ").skipNulls();
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("lat", lat)
				.add("lng", lng)
				.add("type", type)
				.add("amount", amount)
				.add("conqueredBy", joiner.join(conqueredBy))
				.toString();
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
}
