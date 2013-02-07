package models;

import java.util.Date;
import java.util.List;

import play.Logger;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

@Entity("teams")
public class Team extends TimeStampedModel {

	private String name;

	@Embedded
	private Photo avatar;
	private Integer score;

	@Reference
	private Faction faction;

	@Reference
	private City city;
	
	@Reference
	private List<Player> players = Lists.newLinkedList();
	
	@Reference
	private Player teamMaster;
	
	public Team() {
	}
	
	public Team(String name) {
		setName(name);
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
		if (this.players.isEmpty())
			setTeamMaster(player);
		return this.players.add(player);
	}
	
	public Boolean removePlayer(Player player) {
		return this.players.remove(player);
	}
	
	public Boolean isMember(Player player) {
		return this.players.contains(player);
	}
	
	public void setTeamMaster(Player player) {
		this.teamMaster = player;
	}
	
	public Player getTeamMaster() {
		if (this.teamMaster == null) {
			Logger.warn("The team master of " + getName() + " is not set, but should be - setting now.");
			refindTeamMaster();
		}
		return this.teamMaster;
	}
	
	public Player refindTeamMaster() {
		
		Player minPlayer = null;
		Date minDate = null;
		for (Player player : players) {
			Date date = player.getJoinTeamDate();
			
			if (minPlayer == null && date != null)  {
				minPlayer = player;
				minDate = date;
				
				continue;
			} 
			
			if (date != null && minDate.getTime() > date.getTime()) {
				minPlayer = player;
				minDate = date;
			}
		}
		
		setTeamMaster(minPlayer);
		
		return this.teamMaster;
	}

	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", getId())
				.add("name", name)
				.add("createdAt", getCreationDate())
				.add("score", score)
				.add("faction", faction)
				.add("city", city).toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		
		Team other = (Team) obj;
		if (!getName().equals(other.getName()))
			return false;
		return true;
	}

}
