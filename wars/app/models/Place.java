package models;

import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

@Entity("places")
public class Place {

	@Id
	private ObjectId id;
	private Double lat;
	private Double lng;
	private PlaceType type;
	private Integer amount;
	private List<Player> conqueredBy = Lists.newArrayList();
	
	public ObjectId getId() {
		return id;
	}
	
	public void setId(ObjectId id) {
		this.id = id;
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
	
	public List<Player> getConqueredBy() {
		return conqueredBy;
	}

	public void setConqueredBy(List<Player> conqueredBy) {
		this.conqueredBy = conqueredBy;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Place[");
		sb.append("id: ");
		sb.append(id);
		sb.append(", lat: ");
		sb.append(lat);
		sb.append(", lng: ");
		sb.append(lng);
		sb.append(", type: ");
		sb.append(type);
		sb.append(", amount: ");
		sb.append(amount);
		sb.append(", conqueredBy: ");
		Joiner joiner = Joiner.on(", ").skipNulls();
		sb.append(joiner.join(conqueredBy));
		sb.append("]");
		return sb.toString();
	}
	
}
