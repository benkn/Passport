package ben.kn.hibernate;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/testing-applicationContext.xml")
public class SecuredSessionFactoryJTest {

	@Autowired
	SecuredSessionFactory securedSessionFactoryBean;

	@Before
	public void setUp() throws Exception {
	}

	@SuppressWarnings("static-access")
	@Test
	public void testGetSessionFactory() {
		assertNotNull(securedSessionFactoryBean);
		assertNotNull(securedSessionFactoryBean.getSessionFactory());
	}

}
