package ben.kn.passport.contact;

import ben.kn.passport.to.User;

public interface UserContacter {
	/**
	 * Send the given message to the user
	 * 
	 * @param user User to send message to
	 * @param messageText String of the message to send
	 */
	public void messageUser(User user, String messageText);

	/**
	 * Check if this UserContacter is functional
	 * 
	 * @return boolean
	 */
	public boolean isOperable();
}
