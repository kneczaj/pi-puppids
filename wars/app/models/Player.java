package models;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import models.notifications.Notification;

import org.bson.types.ObjectId;

import securesocial.core.java.AuthenticationMethod;
import securesocial.core.java.PasswordInfo;
import securesocial.core.java.SocialUser;
import securesocial.core.java.UserId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.code.morphia.annotations.Reference;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Entity("players")
public class Player implements AvatarInterface {

	@Id
	private ObjectId id;
	private String username;
	private String passwordHash;
	private String firstname;
	private String name;
	private String email;

	@Embedded
	private Photo avatar;
	private String homeTown;
	private Date dateOfBirth;
	private Integer minStrength;
	private Integer maxStrength;
	private Integer score = 0;
	private String authenticationProvider;
	private String secureSocialIdentifier;
	private Map<ResourceType, Integer> resourceDepot = initializeResourceDepots();
	
	@Reference
	private List<Place> conquered = Lists.newLinkedList();

	@Reference
	private Team team;
	private Date joinTeamDate;
	
	@Reference
	private List<Unit> units = Lists.newLinkedList();
	
	@Reference
	private List<Notification> notifications = Lists.newLinkedList();
	
	public Date getJoinTeamDate() {
		return joinTeamDate;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Photo getAvatar() {
		return avatar;
	}

	public void setAvatar(Photo avatar) {
		this.avatar = avatar;
	}

	public String getHomeTown() {
		return homeTown;
	}

	public void setHomeTown(String homeTown) {
		this.homeTown = homeTown;
	}

	public Date getDateOfBirth() {
		return dateOfBirth;
	}
	
	public String getDateOfBirthString() {
		if (dateOfBirth == null)
			return "";
		SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		return format.format(dateOfBirth);
	}

	public void setDateOfBirth(Date dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
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

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getAuthenticationProvider() {
		return authenticationProvider;
	}

	public void setAuthenticationProvider(String authenticationProvider) {
		this.authenticationProvider = authenticationProvider;
	}

	public String getSecureSocialIdentifier() {
		return secureSocialIdentifier;
	}

	public void setSecureSocialIdentifier(String secureSocialIdentifier) {
		this.secureSocialIdentifier = secureSocialIdentifier;
	}
	
	public Team getTeam() {
		return team;
	}

	public void setTeam(Team team) {
		this.team = team;
		this.joinTeamDate = new Date();
	}
	
	public List<Player> getTeammates() {
		
		LinkedList<Player> teamPlayers = new LinkedList<Player>();
		
		if (team == null)
			return teamPlayers;
		
		teamPlayers.addAll(team.getPlayers());
		teamPlayers.remove(this);
		return teamPlayers;
		
	}
	
	public void addNotification(Notification n) {
		notifications.add(0, n);
	}
	
	public List<Notification> getNotificationsList() {
		return notifications;
	}
	
	public void clearNotificationsList() {
		notifications.clear();
	}
	
	
	public List<Place> getConquered() {
		return conquered;
	}

	public void setConquered(List<Place> conquered) {
		this.conquered = conquered;
	}

	public List<Unit> getUnits() {
		return units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}
	
	public Integer getResourceDepot(ResourceType type) {
		if (resourceDepot.get(type) == null) {
			return 0;
		}
		
		return resourceDepot.get(type);
	}
	
	public Map<ResourceType, Integer> getResourceDepot() {
		return resourceDepot;
	}
	
	public void withdrawFromResourceDepot(ResourceType type, Integer amount) {
		Integer newValue = getResourceDepot(type) - amount;
		resourceDepot.put(type, newValue);
	}
	
	public void depositToResourceDepot(ResourceType type, Integer amount) {
		Integer newValue = getResourceDepot(type) + amount;
		resourceDepot.put(type, newValue);
	}
	
	public void setResourceDepot(Map<ResourceType, Integer> depot) {
		this.resourceDepot = depot;
	}
	
	public Boolean isTeamMaster() {
		return getId().equals(getTeam().getTeamMaster().getId());
	}

	public SocialUser toSocialUser() {
		SocialUser su = new SocialUser();
		su.setAuthMethod(AuthenticationMethod.valueOf(authenticationProvider));
		su.setEmail(email);
		su.setFirstName(firstname);
		su.setLastName(name);
		PasswordInfo pwInfo = new PasswordInfo();
		pwInfo.setPassword(passwordHash);
		su.setPasswordInfo(pwInfo);
		UserId userId = new UserId();
		userId.setId(secureSocialIdentifier);
		userId.setProvider(authenticationProvider);
		su.setId(userId);

		return su;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((authenticationProvider == null) ? 0
						: authenticationProvider.hashCode());
		result = prime * result + ((avatar == null) ? 0 : avatar.hashCode());
		result = prime * result
				+ ((dateOfBirth == null) ? 0 : dateOfBirth.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result
				+ ((homeTown == null) ? 0 : homeTown.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((passwordHash == null) ? 0 : passwordHash.hashCode());
		result = prime * result
				+ ((resourceDepot == null) ? 0 : resourceDepot.hashCode());
		result = prime
				* result
				+ ((secureSocialIdentifier == null) ? 0
						: secureSocialIdentifier.hashCode());
		result = prime * result + ((team == null) ? 0 : team.hashCode());
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Player other = (Player) obj;
		if (authenticationProvider == null) {
			if (other.authenticationProvider != null)
				return false;
		} else if (!authenticationProvider.equals(other.authenticationProvider))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (homeTown == null) {
			if (other.homeTown != null)
				return false;
		} else if (!homeTown.equals(other.homeTown))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (passwordHash == null) {
			if (other.passwordHash != null)
				return false;
		} else if (!passwordHash.equals(other.passwordHash))
			return false;
		if (resourceDepot == null) {
			if (other.resourceDepot != null)
				return false;
		} else if (!resourceDepot.equals(other.resourceDepot))
			return false;
		if (secureSocialIdentifier == null) {
			if (other.secureSocialIdentifier != null)
				return false;
		} else if (!secureSocialIdentifier.equals(other.secureSocialIdentifier))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	
	private Map<ResourceType, Integer> initializeResourceDepots() {
		Map<ResourceType, Integer> depots = Maps.newHashMap();
		for (ResourceType type : ResourceType.values()) {
			depots.put(type, 0);
		}
		return depots;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("email", email)
				.add("username", username)
				.add("homeTown", homeTown)
				.add("score", score)
				.add("team", team).toString();
	}
	
}
