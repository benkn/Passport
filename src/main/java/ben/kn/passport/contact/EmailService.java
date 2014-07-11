package ben.kn.passport.contact;

import org.apache.log4j.Logger;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * The EmailService acts as a hub for all outgoing email messages.
 * 
 * @author Ben (bknear@gmail.com)
 * @since Nov 30, 2012
 */
public class EmailService {
	protected final Logger log = Logger.getLogger(EmailService.class);
	private JavaMailSender mailSender;

	public EmailService(JavaMailSender ms) {
		mailSender = ms;
	}

	public void sendMessage(SimpleMailMessage message) {
		log.debug("Sending mail message.");
		try {
			mailSender.send(message);
		} catch (Exception e) {
			log.error("Failed to send message! " + e.getLocalizedMessage());
		}
	}
}
