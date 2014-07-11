package ben.kn.passport.contact;

import org.apache.log4j.Logger;

import ben.kn.passport.to.User;

public class MockContacter implements UserContacter {
	protected final Logger log = Logger.getLogger(getClass());

	@Override
	public void messageUser(User user, String messageText) {
		log.info("Contacting user: " + user + "\n" + messageText);
	}

	@Override
	public boolean isOperable() {
		return true;
	}
}