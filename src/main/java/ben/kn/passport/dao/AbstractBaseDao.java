package ben.kn.passport.dao;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ben.kn.hibernate.SecuredSessionFactory;

@Transactional
abstract class AbstractBaseDao<T> {

	private SessionFactory sessionFactory;
	
	protected AbstractBaseDao() {
		
	}

	@SuppressWarnings("static-access")
	@Autowired
	public AbstractBaseDao(SecuredSessionFactory sessionFactoryBean) {
		this.sessionFactory = sessionFactoryBean.getSessionFactory();
	}

	protected Session getSession() throws HibernateException {
		return sessionFactory.getCurrentSession();
	}

	/**
	 * Stores the object and returns the ID value.
	 * 
	 * @param object
	 * @return
	 * @throws HibernateException
	 */
	public Serializable create(T object) throws HibernateException {
		return getSession().save(object);
	}

	public void delete(T object) throws HibernateException {
		getSession().delete(object);
	}

	public void update(T object) throws HibernateException {
		getSession().update(object);
	}
}