package ben.kn.passport.to;

import org.springframework.security.core.GrantedAuthority;

/**
 * Enum represents a role for a {@link User}
 * 
 * @author Ben (bknear@gmail.com)
 * @since Jan 29, 2013
 */
public enum UserRole implements GrantedAuthority {
	ADMIN("Administrator"), USER("User");

	private String display;

	UserRole(String display) {
		this.display = display;
	}

	public String getDisplay() {
		return display;
	}

	public static UserRole fromBytes(byte[] bytes) {
		if ( bytes != null ) {
			UserRole.valueOf(new String(bytes));
		}
		return null;
	}

	@Override
	public String getAuthority() {
		return toString();
	}
}
