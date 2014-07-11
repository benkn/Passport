package ben.kn.hibernate;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.stereotype.Service;

import ben.kn.dps.EncryptionService;

@Service
public class SecuredSessionFactory {

	private static final Logger log = Logger.getLogger(SecuredSessionFactory.class);
	private static SessionFactory sessionFactory = null;
	private static LocalSessionFactoryBean sessionFactoryBean = null;
	private static boolean decryptPassword = true;
	private static DriverManagerDataSource dataSource;

	public void setDecryptPassword(boolean decrypt) {
		decryptPassword = decrypt;
	}

	private static EncryptionService encryptionSvc;

	@Autowired
	public SecuredSessionFactory(LocalSessionFactoryBean sessionFactory2,
			DriverManagerDataSource dataSource2) {
		sessionFactoryBean = sessionFactory2;
		dataSource = dataSource2;
		encryptionSvc = new EncryptionService();
	}

	@Autowired
	public void setEncryptionSvc(EncryptionService encryptionSvc2) {
		encryptionSvc = encryptionSvc2;
	}

	public static SessionFactory getSessionFactory() {
		if ( sessionFactory == null ) {
			try {
				if ( decryptPassword ) {
					String password = dataSource.getPassword();

					log.info("MySQL Password: " + password);

					log.info("decrypting...");
					password = encryptionSvc.decrypt(password);

					log.info("MySQL Password (decrypted): " + password);

					dataSource.setPassword(password);
				}

				// generate the SessionFactory
				sessionFactoryBean.afterPropertiesSet();
				sessionFactory = sessionFactoryBean.getObject();
			} catch (Exception e) {
				log.error("Could not decrypt the password for the datasource! Datasource availability now at risk. ("
						+ e.getLocalizedMessage() + ").");
			}
		}
		return sessionFactory;
	}
}