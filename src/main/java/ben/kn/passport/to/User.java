package ben.kn.passport.to;

import java.security.Principal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "users")
public class User implements Principal {

	@Id
	@GeneratedValue
	@Column(nullable = false, name = "id")
	protected int id;

	@Column(nullable = false, name = "username")
	private String username;

	@Column(nullable = false, name = "password")
	private String password;

	@Column(nullable = false, name = "email")
	private String email;

	@Column(nullable = true, name = "confirmationCode")
	private String confirmationCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "creationDate", nullable = false, length = 19)
	private Date creationDate;

	@Column(name = "verified", nullable = false)
	private boolean verified;

	@Column(name = "role", nullable = true)
	private String role;

	/**
	 * @deprecated
	 */
	protected User() {

	}

	public User(String username, String password, String email, Date creationDate) {
		this.username = username;
		this.password = password;
		this.email = email;
		this.creationDate = creationDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConfirmationCode() {
		return confirmationCode;
	}

	public void setConfirmationCode(String confirmationCode) {
		this.confirmationCode = confirmationCode;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date createdDate) {
		this.creationDate = createdDate;
	}

	public boolean isVerified() {
		return verified;
	}

	public void setVerified(boolean verified) {
		this.verified = verified;
	}

	public UserRole getRole() {
		return UserRole.valueOf(role);
	}

	public void setRole(UserRole role) {
		this.role = role.toString();
	}

	@Override
	public String getName() {
		return username;
	}
}