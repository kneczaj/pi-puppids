package models;

import java.util.Date;

import org.bson.types.ObjectId;

import securesocial.core.java.AuthenticationMethod;
import securesocial.core.java.PasswordInfo;
import securesocial.core.java.SocialUser;
import securesocial.core.java.UserId;

import com.google.code.morphia.annotations.Embedded;
import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;
import com.google.common.base.Objects;

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
	private String authenticationProvider;
	private String secureSocialIdentifier;

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
	public String toString() {
		return Objects.toStringHelper(this.getClass())
				.add("id", id)
				.add("email", email)
				.add("username", username)
				.add("homeTown", homeTown)
				.add("score", score).toString();
	}
}
