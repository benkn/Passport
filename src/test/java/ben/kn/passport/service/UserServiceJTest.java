package ben.kn.passport.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ben.kn.passport.CipherService;
import ben.kn.passport.EncryptedUserService;
import ben.kn.passport.dao.UserDao;
import ben.kn.passport.lang.PassportException;
import ben.kn.passport.to.User;

public class UserServiceJTest {

	// create the mock context to substitute for the DAO
	final UserDao dao = Mockito.mock(UserDao.class);

	// Class we are testing
	private EncryptedUserService testService;

	// Test data needed
	final int genericId = 1;
	final String USERNAME = "John Doe";
	final String CIPHER = "cipher1";
	final String EMAIL = "junk@email.me";
	final User user = new User(USERNAME, CIPHER, EMAIL, new Date());

	static Date fromDate;

	final List<User> userList = new ArrayList<User>();

	@Before
	public void setup() {
		// we don't auto-wire in this test because we want to force the DAO to
		// be the mock DAO
		testService = new EncryptedUserService(new CipherService(null, null));
		testService.setUserDao(dao);
	}

	/************** TESTING SERVICE-SPECIFIC METHODS ****************/

	@Test
	public void testSelectById() throws PassportException {
		// expectations
		when(dao.selectById(genericId)).thenReturn(user);

		// execute
		User result = testService.selectById(genericId);

		assertTrue("User was not found correctly.", result != null);
	}

	@Test
	public void testSelectByName() throws PassportException {
		when(dao.selectByName(USERNAME)).thenReturn(user);

		User result = testService.selectByName(USERNAME);

		assertTrue("User was not found correctly.", result != null);
	}

	@Test
	public void testPurgeUnverifiedUsers() throws PassportException {

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.DAY_OF_YEAR, -3);
		fromDate = cal.getTime();

		userList.add(user);

		when(dao.selectUnverifiedByCreationDate(fromDate)).thenReturn(userList);

		testService.purgeUnverifiedUsers();
	}

	/********* Generic tests **********/

	@Test
	public void testCreate() throws PassportException {
		testService.create(USERNAME, CIPHER, EMAIL);
	}

	@Test
	public void testDelete() throws PassportException {
		when(dao.selectById(genericId)).thenReturn(user);

		testService.delete(genericId);
	}

	@Test
	public void testUpdate() throws PassportException {
		when(dao.selectByName(USERNAME)).thenReturn(user);

		testService.update(user);
	}
}