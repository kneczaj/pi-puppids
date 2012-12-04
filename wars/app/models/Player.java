package models;

import java.util.Date;

import javax.persistence.Embedded;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;

@Entity("players")
public class Player {

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
	private Integer score;
	
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
	
	public void setId(ObjectId id) {
		this.id = id;
	}
	
	public ObjectId getId() {
		return id;
	}
	
	
	
}
