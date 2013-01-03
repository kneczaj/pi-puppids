package models;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

@Entity("teams")
public class Team {

	@Id
	private ObjectId id;
	private String name;

	@Embedded
	private Photo avatar;
	private Date createdAt;
	private Integer score;

	@Reference
	private Faction faction;

	@Reference
	private City city;
	
	@Reference
	private List<Player> players = Lists.newLinkedList();
	
	public Team() {
		setCreatedAt(new Date());
	}
	
	public Team(String name) {
		this();
		setName(name);
	}

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

	public Photo getAvatar() {
		return avatar;
	}

	public void setAvatar(Photo avatar) {
		this.avatar = avatar;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public Faction getFaction() {
		return faction;
	}

	public void setFaction(Faction faction) {
		this.faction = faction;
	}
	
	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}
	
	public Boolean addPlayer(Player player) {
		return this.players.add(player);
	}
	
	public Boolean removePlayer(Player player) {
		return this.players.remove(player);
	}

	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("name", name)
				.add("createdAt", createdAt)
				.add("score", score)
				.add("faction", faction)
				.add("city", city).toString();
	}

}
