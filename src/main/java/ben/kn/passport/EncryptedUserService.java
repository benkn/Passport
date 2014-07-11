package ben.kn.passport;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ben.kn.passport.lang.PassportException;
import ben.kn.passport.to.User;
import ben.kn.passport.to.UserRole;

@Service
public class EncryptedUserService extends UserService {

	protected CipherService cipherService;

	@Autowired
	public EncryptedUserService(CipherService cipherService) {
		this.cipherService = cipherService;
	}

	@Override
	public void create(String username, String password, String email, String role)
			throws PassportException {

		// convert password to cipher
		String cipher = cipherService.encryptPassword(password);

		User user = new User(username, cipher, email, new Date());
		if ( role != null )
			user.setRole(UserRole.valueOf(role));

		if ( userContacter != null && userContacter.isOperable() ) {
			// generate the confirmation code
			user.setConfirmationCode(generateConfirmationCode());

			log.info("Creating conf code " + user.getConfirmationCode() + " for user "
					+ user.getUsername());

			userDao.create(user);

			// contact the user to verify their account
			log.info("Contacting the user to confirm their account.");
			userContacter
					.messageUser(
							user,
							String.format(CONFIRMATION_EMAIL_MESSAGE, username,
									user.getConfirmationCode()));
		} else {
			log.info("Creating user " + user.getUsername());
			userDao.create(user);
		}
	}

	/**
	 * Update the stored values for the given User.
	 * 
	 * @param updatedUser User to update, with a clear-text password
	 * @throws PassportException
	 */
	public void update(User updatedUser) throws PassportException {
		try {
			User merged = null;
			if ( updatedUser.getId() > 0 ) {
				merged = userDao.selectById(updatedUser.getId());
			} else if ( updatedUser.getUsername() != null ) {
				merged = userDao.selectByName(updatedUser.getUsername());
			}

			if ( merged != null ) {
				merged.setUsername(updatedUser.getUsername());
				merged.setEmail(updatedUser.getEmail());
				merged.setPassword(cipherService.encryptPassword(updatedUser.getPassword()));
			}
			userDao.update(merged);
		} catch (Exception e) {
			throw new PassportException(e.getMessage());
		}
	}
}