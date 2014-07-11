package ben.kn.passport;

import java.util.Collection;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import ben.kn.passport.lang.PassportException;
import ben.kn.passport.to.User;
import ben.kn.passport.to.UserRole;

public class PassportAuthenticationProvider implements AuthenticationProvider {
	protected final Logger log = Logger.getLogger(getClass());

	private UserService userSvc;
	private CipherService cipherService;

	public PassportAuthenticationProvider(UserService userSvc, CipherService cipherService) {
		this.userSvc = userSvc;
		this.cipherService = cipherService;
	}

	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		// check that the authentication has actually been provided
		if ( authentication == null ) {
			log.debug("Attempted to authenticate but the given authentication was null.");
			throw new AuthenticationCredentialsNotFoundException("No authentication provided!");
		}

		if ( !(authentication instanceof UsernamePasswordAuthenticationToken) ) {
			log.warn("Attempted to authenticate but the given authentication was not the kind expected. Instead it was "
					+ authentication.getClass());
			throw new BadCredentialsException(
					"The wrong type of authorization has been given, cannot continue.");
		}

		// get username
		String username = (authentication.getPrincipal() == null) ? null : authentication.getName();
		String password = (String) ((authentication.getPrincipal() == null) ? null : authentication
				.getCredentials());

		User user = null;
		if ( username != null ) {
			try {
				// get the User with this username
				user = userSvc.selectByName(username);
				if ( user != null ) {
					// check the password
					if ( password != null ) {
						String encryptedPassword = cipherService.encryptPassword(password);
						// if it doesn't match, then throw an exception
						if ( !encryptedPassword.equals(user.getPassword()) ) {
							throw new BadCredentialsException("Invalid password provided.");
						}
					}
				} else {
					throw new UsernameNotFoundException(
							"The user you've attempted to log in as was not found.");
				}
			} catch (PassportException e) {
				throw new ProviderNotFoundException(e.getMessage());
			}
		}

		return createSuccessAuthentication(authentication, user);
	}

	/**
	 * Creates a successful {@link Authentication} object.
	 * <p>
	 * Protected so subclasses can override.
	 * </p>
	 * <p>
	 * Subclasses will usually store the original credentials the user supplied
	 * (not salted or encoded passwords) in the returned
	 * <code>Authentication</code> object.
	 * </p>
	 * 
	 * @param authentication that was presented to the provider for validation
	 * @param user that was loaded by the implementation
	 * 
	 * @return the successful authentication token
	 */
	protected Authentication createSuccessAuthentication(Authentication authentication, User user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(user,
				user.getPassword(), toCol(user.getRole()));
		result.setDetails(authentication.getDetails());

		return result;
	}

	private Collection<? extends GrantedAuthority> toCol(UserRole role) {
		Collection<UserRole> col = new HashSet<UserRole>();
		col.add(role);
		return col;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

}
