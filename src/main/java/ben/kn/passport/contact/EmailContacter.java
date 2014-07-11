package ben.kn.passport.contact;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;

import ben.kn.passport.to.User;

/**
 * This class provides the means of contacting a User with an email.
 * 
 * @author Ben (bknear@gmail.com)
 */
public class EmailContacter implements UserContacter {
	protected final Logger log = Logger.getLogger(EmailContacter.class);
	private EmailService emailService;
	private String fromAddress;
	private final String EMAIL_SUBJECT = "Welcome! Please confirm your account.";

	public EmailContacter(EmailService emailService, String fromAddress) {
		this.emailService = emailService;
		this.fromAddress = fromAddress;
	}

	public boolean isOperable() {
		return (emailService != null);
	}

	public void messageUser(User user, String messageText) {
		try {
			String to = user.getEmail();

			SimpleMailMessage message = new SimpleMailMessage();

			// Set From: header field of the header.
			message.setFrom(fromAddress);

			// Set To: header field of the header.
			message.setTo(to);

			// Set Subject: header field
			message.setSubject(EMAIL_SUBJECT);

			if ( log.isDebugEnabled() ) {
				log.debug("Sending email to: " + message.getTo());
				log.debug("Sending email from: " + message.getFrom());
				log.debug("Sending email about: " + message.getSubject());
			}

			// Now set the actual message
			message.setText(messageText);

			// Send message
			emailService.sendMessage(message);
			log.info("Sent message successfully....");
		} catch (Exception dae) {
			log.error("Failed to send email because of an exception: " + dae.getLocalizedMessage());
		}
	}
}
