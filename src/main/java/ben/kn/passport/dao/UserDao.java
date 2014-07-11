package ben.kn.passport.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import ben.kn.hibernate.SecuredSessionFactory;
import ben.kn.passport.to.User;

@Repository
@Transactional(readOnly = true)
public class UserDao extends AbstractBaseDao<User> {

	// @formatter:off
	private static final String FIND_BY_NAME = "FROM User u WHERE u.username = :username";
	private static final String SELECT_BY_CREATION_DATE_UNVERIFIED = "FROM User u WHERE u.verified = false AND u.creationDate < :creationDate";
	// @formatter:on

	protected UserDao() {
		super();
	}

	@Autowired
	public UserDao(SecuredSessionFactory sessionFactory) {
		super(sessionFactory);
	}

	public User selectByName(String username) throws HibernateException {
		User result = (User) getSession().createQuery(FIND_BY_NAME)
				.setParameter("username", username).uniqueResult();

		return result;
	}

	public User selectById(int id) throws HibernateException {
		return (User) getSession().get(User.class, id);
	}

	@SuppressWarnings("unchecked")
	public List<User> selectUnverifiedByCreationDate(Date fromDate) {
		return (List<User>) getSession().createQuery(SELECT_BY_CREATION_DATE_UNVERIFIED)
				.setParameter("creationDate", fromDate).list();
	}
}