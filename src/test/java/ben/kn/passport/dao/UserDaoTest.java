package ben.kn.passport.dao;

import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import ben.kn.passport.to.User;
import ben.kn.passport.to.UserRole;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
@ContextConfiguration(locations = { "/testing-passportConfig-context.xml" })
// @ContextConfiguration(locations = { "/testing-applicationContext.xml" })
public class UserDaoTest {

	@Autowired
	private UserDao dao;

	private final String USERNAME = "test-user";
	private final String CIPHER = "cipher1";
	private final String EMAIL = "bknear@gmail.com";
	final User user = new User(USERNAME, CIPHER, EMAIL, new Date());

	@Before
	public void setup() {
		user.setRole(UserRole.USER);
	}

	@After
	public void tearDown() {
	}

	private Session getSession() {
		return dao.getSession();
	}

	/********************** Test Methods **********************/

	@Test
	public void testSelectByName() {
		getSession().save(user);

		User result = dao.selectByName(USERNAME);
		assertTrue(result.getId() == user.getId());
	}

	@Test
	public void testSelectById() {
		int id = (Integer) getSession().save(user);

		User result = dao.selectById(id);

		assertTrue("User was not found correctly.", result != null);
		assertTrue("Mismatch in testing the selectById functionality", result.equals(user));
	}

	@SuppressWarnings("deprecation")
	@Test
	public void testSelectUnverifiedByCreationDate() {

		Date creationDate = new Date();
		creationDate.setDate(creationDate.getDate() - 1);

		user.setCreationDate(creationDate);
		getSession().save(user);

		Date testDate = new Date();

		Collection<User> results;
		User resultUser;

		Logger log = Logger.getLogger(getClass());
		log.info("Test date = " + testDate);

		// test with only one available date
		results = dao.selectUnverifiedByCreationDate(testDate);
		assertTrue("Results not found, though expected.", results != null && results.size() > 0);
		assertTrue("Incorrect total of results found. Expected 1, found " + results.size(),
				results.size() == 1);
		resultUser = results.iterator().next();
		assertTrue("Mismatch in testing the selectUnverifiedByCreationDate functionality",
				resultUser.equals(user));

		// create a second user which should not be returned when searching
		User secondUser = new User("secondUser", CIPHER, EMAIL, new Date());
		getSession().save(secondUser);

		// test with two available dates, but only one should be returned
		results = dao.selectUnverifiedByCreationDate(testDate);
		assertTrue("Results not found, though expected.", results != null && results.size() > 0);
		assertTrue("Incorrect total of results found. Expected 1, found " + results.size(),
				results.size() == 1);
		resultUser = results.iterator().next();
		assertTrue("Mismatch in testing the selectUnverifiedByCreationDate functionality",
				resultUser.equals(user));

		// create a third user which should be returned when searching
		User thirdUser = new User("thirdUser", CIPHER, EMAIL, creationDate);
		getSession().save(thirdUser);

		// test with three available dates, but only two should be returned
		results = dao.selectUnverifiedByCreationDate(testDate);
		assertTrue("Results not found, though expected.", results != null && results.size() > 0);
		assertTrue("Incorrect total of results found. Expected 2, found " + results.size(),
				results.size() == 2);
		Iterator<User> iter = results.iterator();
		while ( iter.hasNext() ) {
			resultUser = iter.next();
			assertTrue("Mismatch in testing the selectUnverifiedByCreationDate functionality",
					resultUser.equals(user) || resultUser.equals(thirdUser));
		}

		// create a fourth user which should not be returned when searching
		User fourthUser = new User("fourthUser", CIPHER, EMAIL, creationDate);
		fourthUser.setVerified(true);
		getSession().save(fourthUser);

		// test with four available dates, but only two should be returned
		results = dao.selectUnverifiedByCreationDate(testDate);
		assertTrue("Results not found, though expected.", results != null && results.size() > 0);
		assertTrue("Incorrect total of results found. Expected 2, found " + results.size(),
				results.size() == 2);
		iter = results.iterator();
		while ( iter.hasNext() ) {
			resultUser = iter.next();
			assertTrue("Mismatch in testing the selectUnverifiedByCreationDate functionality",
					resultUser.equals(user) || resultUser.equals(thirdUser));
		}
	}

	/********************** CRUD **********************/

	@Test
	public void testCreate() {
		int id = (Integer) dao.create(user);

		User result = (User) getSession().get(User.class, id);

		assertTrue("User was not created correctly.", result != null);
		assertTrue("Mismatch in testing the create functionality", result.equals(user));
	}

	@Test
	public void testDelete() {
		int id = (Integer) getSession().save(user);

		dao.delete(user);

		User result = (User) getSession().get(User.class, id);

		assertTrue("User was not deleted correctly.", result == null);
	}

	@Test
	public void testUpdate() {
		int id = (Integer) getSession().save(user);

		String newName = "UpdateTest";
		user.setUsername(newName);

		dao.update(user);

		User result = (User) getSession().get(User.class, id);

		assertTrue("Updated username didn't match", result.getUsername().equals(newName));
		assertTrue("Updated user does not match", result.equals(user));
	}
}