package ben.kn.passport;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ben.kn.passport.contact.UserContacter;
import ben.kn.passport.dao.UserDao;
import ben.kn.passport.lang.PassportException;
import ben.kn.passport.to.User;
import ben.kn.passport.to.UserRole;

@Service
public class UserService implements IUserService {
	/**
	 * Maximum length for confirmation code
	 */
	public static final int JUNK_LENGTH = 100;

	public static final String CONFIRMATION_EMAIL_MESSAGE = "Dear %s,\n"
			+ "\tThank you for joining our site. Please verify your account with the following: %s\n"
			+ "And enjoy the service!";

	protected UserDao userDao;
	protected UserContacter userContacter;

	protected int verficationGracePeriod = 3;
	protected final Logger log = Logger.getLogger(UserService.class);

	public void setVerficationGracePeriod(int verficationGracePeriod) {
		this.verficationGracePeriod = verficationGracePeriod;
	}

	@Autowired
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	@Autowired
	public void setUserContacter(UserContacter contacter) {
		userContacter = contacter;
	}

	/**
	 * Select the User with the given ID
	 * 
	 * @param id int of the id
	 * @return User with the given ID, or null if not found
	 * @throws PassportException
	 */
	public User selectById(int id) throws PassportException {
		try {
			return userDao.selectById(id);
		} catch (Exception e) {
			throw new PassportException("Error in retrieving the user with id " + id + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Select a User by the given name
	 * 
	 * @param name String
	 * @return User, if one exists, with the given name
	 * @throws PassportException
	 */
	public User selectByName(String name) throws PassportException {
		try {
			return userDao.selectByName(name);
		} catch (Exception e) {
			throw new PassportException("Error in retrieving the user with name " + name + ": "
					+ e.getMessage());
		}
	}

	/**
	 * Given the confirmationCode, verify the user if it matches what is
	 * expected.
	 * 
	 * @param userId int of the user's ID
	 * @param confirmationCode String of code given by user
	 * @return boolean whether the user has been verified
	 */
	public boolean verifyUser(int userId, String confirmationCode) {
		User user = userDao.selectById(userId);

		if ( user.getConfirmationCode().equals(confirmationCode) ) {
			user.setVerified(true);
			user.setConfirmationCode(null);
			userDao.update(user);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Remove all users who have not been verified since the verification grace
	 * period.
	 * 
	 * @throws PassportException
	 */
	public void purgeUnverifiedUsers() throws PassportException {
		// create the date to use
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, verficationGracePeriod * -1);
		Date fromDate = cal.getTime();

		log.info("Purging unverified users created before " + fromDate);

		try {
			List<User> usersToPurge = userDao.selectUnverifiedByCreationDate(fromDate);

			for ( User user : usersToPurge ) {
				userDao.delete(user);
			}
		} catch (Exception e) {
			throw new PassportException(e.getMessage());
		}
	}

	protected String generateConfirmationCode() {
		StringBuilder sb = new StringBuilder();
		while ( sb.length() < JUNK_LENGTH ) {
			String hex = Integer.toHexString(RandomGenerator.generateRandomBetween(1,
					Integer.MAX_VALUE - 1));
			sb.append(hex);
		}

		return sb.toString().substring(0, JUNK_LENGTH);
	}

	public void create(String username, String password, String email) throws PassportException {
		create(username, password, email, null);
	}

	public void create(String username, String password, String email, String role)
			throws PassportException {

		User user = new User(username, password, email, new Date());
		if ( role != null )
			user.setRole(UserRole.valueOf(role));

		try {
			if ( userContacter.isOperable() ) {
				// generate the confirmation code
				user.setConfirmationCode(generateConfirmationCode());

				log.info("Creating conf code " + user.getConfirmationCode() + " for user "
						+ user.getUsername());

				userDao.create(user);

				// contact the user to verify their account
				if ( userContacter != null ) {
					log.info("Contacting the user to confirm their account.");
					userContacter.messageUser(
							user,
							String.format(CONFIRMATION_EMAIL_MESSAGE, username,
									user.getConfirmationCode()));
				}
			} else {
				log.info("Creating user " + user.getUsername());
				userDao.create(user);
			}
		} catch (Exception e) {
			throw new PassportException(e.getMessage());
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
				merged.setPassword(updatedUser.getPassword());
			}
			userDao.update(merged);
		} catch (Exception e) {
			throw new PassportException(e.getMessage());
		}
	}

	/**
	 * Remove the User with the given ID.
	 * 
	 * @param id long of the user ID
	 * @throws PassportException
	 */
	public void delete(int id) throws PassportException {
		try {
			User userToDelete = userDao.selectById(id);
			userDao.delete(userToDelete);
		} catch (Exception e) {
			throw new PassportException(e.getMessage());
		}
	}

	@Override
	public void delete(String username) throws PassportException {
		try {
			User userToDelete = userDao.selectByName(username);
			userDao.delete(userToDelete);
		} catch (Exception e) {
			throw new PassportException(e.getMessage());
		}
	}
}