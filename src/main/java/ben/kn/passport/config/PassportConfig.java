package ben.kn.passport.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;

import ben.kn.hibernate.SecuredSessionFactory;
import ben.kn.passport.CipherService;
import ben.kn.passport.EncryptedUserService;
import ben.kn.passport.IUserService;
import ben.kn.passport.UserService;
import ben.kn.passport.contact.EmailContacter;
import ben.kn.passport.contact.EmailService;
import ben.kn.passport.dao.UserDao;
import ben.kn.passport.to.User;

/**
 * Java-based configuration for Passport to alleviate configuration needs for
 * clients. Required fields are marked as such, and this will create all the
 * necessary objects for Passport to function.
 * 
 * @author Ben (bknear@gmail.com)
 * @since Nov 30, 2012
 */
@Configuration
public class PassportConfig {

	private String mailServerHost;
	private int mailServerPort;
	private int verficationGracePeriod = -1;
	private String emailFromAddress;
	private String passportDBURL;
	private String passportDBUsername;
	private String passportDBPassword;
	private String passphrase;
	private String salt;
	private String showSQLOutput = "false";
	private boolean useEncryptionUserService = false;
	private boolean decryptPassword = true;

	/**
	 * Set the passphrase to use for encrypting passwords. Not setting this will
	 * use a default value.
	 * 
	 * @param passphrase String
	 */
	public void setPassphrase(String passphrase) {
		this.passphrase = passphrase;
	}

	/**
	 * String to use as the <code>salt</code> for encrypting passwords. Not
	 * setting this will use a default value.
	 * 
	 * @param salt String
	 */
	public void setSalt(String salt) {
		this.salt = salt;
	}

	/**
	 * Set the hostname of the machine running the mail server. If not set, then
	 * Passport will not send emails.
	 * 
	 * @param mailServerHost String
	 */
	public void setMailServerHost(String mailServerHost) {
		this.mailServerHost = mailServerHost;
	}

	/**
	 * The port for the mail server to use. If not set, then Passport will not
	 * send emails.
	 * 
	 * @param mailServerPort int
	 */
	public void setMailServerPort(int mailServerPort) {
		this.mailServerPort = mailServerPort;
	}

	/**
	 * Set the number of days to wait for a response from a new user to respond
	 * and be verified before deleting the account.
	 * 
	 * @param verficationGracePeriod int
	 */
	public void setVerficationGracePeriod(int verficationGracePeriod) {
		this.verficationGracePeriod = verficationGracePeriod;
	}

	/**
	 * Set the response address to send emails from. This will be the from
	 * address on the email sent to confirm the creation of a user account.
	 * 
	 * @param emailFromAddress String
	 */
	public void setEmailFromAddress(String emailFromAddress) {
		this.emailFromAddress = emailFromAddress;
	}

	/**
	 * <strong>Required</strong> - Set the connection string for the database
	 * containing the <strong>Passport</strong> tables.
	 * 
	 * @param passportDBURL String
	 */
	@Required
	public void setPassportDBURL(String passportDBURL) {
		this.passportDBURL = passportDBURL;
	}

	/**
	 * <strong>Required</strong> - Set the username to connect to the database
	 * with.
	 * 
	 * @param passportDBUsername String
	 */
	@Required
	public void setPassportDBUsername(String passportDBUsername) {
		this.passportDBUsername = passportDBUsername;
	}

	/**
	 * <strong>Required</strong> - Set the password to use with the username
	 * when connecting to the database. This can be an encrypted version or a
	 * plain text version.
	 * 
	 * @param passportDBPassword String
	 */
	@Required
	public void setPassportDBPassword(String passportDBPassword) {
		this.passportDBPassword = passportDBPassword;
	}

	/**
	 * String to equal 'true' or 'false' on whether to show SQL output while
	 * running. <br>
	 * <br>
	 * <strong>default is 'false'</strong>
	 * 
	 * @param showSQLOutput String
	 */
	public void setShowSQLOutput(String showSQLOutput) {
		this.showSQLOutput = showSQLOutput;
	}

	/**
	 * Boolean value on whether to use the {@link EncryptedUserService}. <br>
	 * <br>
	 * <strong>default is 'false'</strong>
	 * 
	 * @param useEncryptedUserService boolean
	 */
	public void setUseEncryptedUserService(boolean useEncryptedUserService) {
		this.useEncryptionUserService = useEncryptedUserService;
	}

	/**
	 * Boolean on whether to decrypt the DB password using the configured
	 * encryption service. <br>
	 * <br>
	 * <strong>default is 'true'</strong>
	 * 
	 * @param decryptPassword
	 */
	public void setDecryptPassword(boolean decryptPassword) {
		this.decryptPassword = decryptPassword;
	}

	@Bean
	public IUserService userService() {
		IUserService svc;
		if ( useEncryptionUserService ) {
			svc = new EncryptedUserService(cipherService());
		} else {
			svc = new UserService();
		}

		// if the verificationGracePeriod has been set by the user, then use
		// it.
		if ( verficationGracePeriod >= 0 ) {
			svc.setVerficationGracePeriod(verficationGracePeriod);
		}

		/* the rest of the properties are autowired */

		return svc;
	}

	@Bean
	public CipherService cipherService() {
		CipherService cipherService = new CipherService(passphrase, salt);
		return cipherService;
	}

	@Bean
	public EmailContacter userContacter() {
		EmailService es = emailService();
		EmailContacter userContacter = new EmailContacter(es, emailFromAddress);
		return userContacter;
	}

	@Bean
	public EmailService emailService() {
		if ( mailServerHost != null && mailServerPort > 0 ) {
			JavaMailSenderImpl mailServer = new JavaMailSenderImpl();
			mailServer.setHost(mailServerHost);
			mailServer.setPort(mailServerPort);

			EmailService emailService = new EmailService(mailServer);
			return emailService;
		} else {
			return null;
		}
	}

	@Bean
	public SecuredSessionFactory securedSessionFactory() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName("com.mysql.jdbc.Driver");
		dataSource.setUrl(passportDBURL);
		dataSource.setUsername(passportDBUsername);
		dataSource.setPassword(passportDBPassword);

		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
		hibernateProperties.setProperty("hibernate.show_sql", showSQLOutput);
		sessionFactory.setHibernateProperties(hibernateProperties);
		sessionFactory.setAnnotatedClasses(new Class[] { User.class });

		SecuredSessionFactory securedSessionFactory = new SecuredSessionFactory(sessionFactory,
				dataSource);
		securedSessionFactory.setDecryptPassword(decryptPassword);

		return securedSessionFactory;
	}

	@Bean
	public UserDao userDao() {
		return new UserDao(securedSessionFactory());
	}

	@Bean
	public HibernateTransactionManager transactionManager() {
		@SuppressWarnings("static-access")
		HibernateTransactionManager transactionManager = new HibernateTransactionManager(
				securedSessionFactory().getSessionFactory());
		return transactionManager;
	}
}
