package ben.kn.passport.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ben.kn.passport.EncryptedUserService;
import ben.kn.passport.lang.PassportException;
import ben.kn.passport.to.User;

@ContextConfiguration(locations = { "/testing-applicationContext.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserServiceJIntTest {

	// Class we are testing
	@Autowired
	private EncryptedUserService testService;
	@Autowired
	private SessionFactory sessionFactory;

	// Test data needed
	final int genericId = 1;
	final String USERNAME = "bknear";
	final String PASSWORD = "cheese";
	final String EMAIL = "bknear@gmail.com";
	final String ROLE = "ADMIN";
	final User user = new User(USERNAME, PASSWORD, EMAIL, new Date());

	static Date fromDate;

	final List<User> userList = new ArrayList<User>();

	@Before
	public void setup() {
	}

	private Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	/************** TESTING SERVICE-SPECIFIC METHODS ****************/

	@Test
	public void testSelectById() throws PassportException {
		getSession().save(user);

		User result = testService.selectById(user.getId());

		assertTrue("User was not found correctly.", result != null);
	}

	@Test
	public void testSelectByName() throws PassportException {
		getSession().save(user);

		User result = testService.selectByName(USERNAME);

		assertTrue("User was not found correctly.", result != null);
	}

	/********* Generic tests **********/

	@Test
	@Rollback(false)
	public void testCreate() throws PassportException {
		testService.create(USERNAME, PASSWORD, EMAIL, ROLE);
	}

	@Test
	public void testDelete() throws PassportException {
		int id = (Integer) getSession().save(user);

		testService.delete(id);

		User result = (User) getSession().get(User.class, id);

		assertTrue("User was not deleted correctly.", result == null);
	}

	@Test
	public void testUpdate() throws PassportException {
		int id = (Integer) getSession().save(user);

		String newName = "UpdateTest";
		user.setUsername(newName);

		testService.update(user);

		User result = (User) getSession().get(User.class, id);

		assertTrue("Updated username didn't match", result.getUsername().equals(newName));
		assertTrue("Updated user does not match", result.equals(user));
	}
}