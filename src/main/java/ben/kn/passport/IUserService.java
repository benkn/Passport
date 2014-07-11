package ben.kn.passport;

import ben.kn.passport.lang.PassportException;
import ben.kn.passport.to.User;

public interface IUserService {
	public void setVerficationGracePeriod(int verficationGracePeriod);

	/**
	 * Select the User with the given ID
	 * 
	 * @param id int of the id
	 * @return User with the given ID, or null if not found
	 * @throws PassportException
	 */
	public User selectById(int id) throws PassportException;

	/**
	 * Select a User by the given name
	 * 
	 * @param name String
	 * @return User, if one exists, with the given name
	 * @throws PassportException
	 */
	public User selectByName(String name) throws PassportException;

	/**
	 * Given the confirmationCode, verify the user if it matches what is
	 * expected.
	 * 
	 * @param userId int of the user's ID
	 * @param confirmationCode String of code given by user
	 * @return boolean whether the user has been verified
	 */
	public boolean verifyUser(int userId, String confirmationCode);

	/**
	 * Remove all users who have not been verified since the verification grace
	 * period.
	 * 
	 * @throws PassportException
	 */
	public void purgeUnverifiedUsers() throws PassportException;

	/**
	 * Create a user with the given properties. This service will then attempt
	 * to contact the user to verify his or her account.
	 * 
	 * @param username String of the user's username
	 * @param password String of the user's password
	 * @param email String of the user's email address
	 * @return Long of the id of the user
	 * @throws PassportException
	 */
	public void create(String username, String password, String email) throws PassportException;

	/**
	 * Create a user with the given properties. This service will then attempt
	 * to contact the user to verify his or her account.
	 * 
	 * @param username String of the user's username
	 * @param password String of the user's password
	 * @param email String of the user's email address
	 * @param role String of the user's role
	 * @return Long of the id of the user
	 * @throws PassportException
	 */
	public void create(String username, String password, String email, String role) throws PassportException;

	/**
	 * Update the stored values for the given User.
	 * 
	 * @param updatedUser User to update, with a clear-text password
	 * @throws PassportException
	 */
	public void update(User updatedUser) throws PassportException;

	/**
	 * Remove the User with the given ID.
	 * 
	 * @param id long of the user ID
	 * @throws PassportException
	 */
	public void delete(int id) throws PassportException;

	/**
	 * Remove the User with the given username.
	 * 
	 * @param username String of the username
	 * @throws PassportException
	 */
	public void delete(String username) throws PassportException;
}
