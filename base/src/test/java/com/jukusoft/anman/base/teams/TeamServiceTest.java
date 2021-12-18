package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.importer.UserCreationImporter;
import com.jukusoft.anman.base.utils.DBTest;
import com.jukusoft.anman.base.utils.PasswordService;
import com.jukusoft.anman.base.utils.UserHelperService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import javax.transaction.Transactional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * Test the TeamService.
 *
 * @author Justin Kuenzel
 */
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class TeamServiceTest extends DBTest {

	@Test
	@Transactional
	public void testAddTeam() throws Exception {
		assertEquals(0, teamDAO.count());

		//first, import the default customer
		importDefaultCustomer();

		//check, that one customer exists
		assertEquals(1, customerDAO.count());

		//get the default customer
		CustomerEntity customer = customerDAO.findOneByName("super-admin").orElseThrow();

		//verify, that no other team exists
		assertEquals(0, teamDAO.count());

		//add the new team
		TeamService teamService = createTeamService();
		long teamID = teamService.addTeam(customer, "new-team-title", "new-team-description");
		assertTrue(teamID > 0);

		//check, that team was created
		assertEquals(1, teamDAO.count());

		//check customer
		assertEquals(getDefaultCustomer().getId(), teamDAO.findOneByName("new-team-title").get().getCustomer().getId());

		//synchronize database
		flushDB();

		//check teams of customer
		List<TeamDTO> customerTeams = teamService.listAllTeamsOfCustomer(customer.getId());
		assertEquals(1, customerTeams.size());

		//check values
		TeamDTO team = teamService.getTeam(teamID).orElseThrow();
		assertEquals("new-team-title", team.title());
		assertEquals("new-team-description", team.description());

		//cleanup
		cleanUp();
	}

	@Test
	@Transactional
	public void testListAllTeamsOfNotExistentCustomer() {
		assertThrows(IllegalStateException.class, () -> createTeamService().listAllTeamsOfCustomer(-1));
	}

	@Test
	@Transactional
	public void testIfUserIsMemberOfTeam() throws Exception {
		//first, create customer and user
		importDefaultCustomer();

		//check, that admin user was created
		assertEquals(1, userDAO.count());
		assertEquals("admin", userDAO.findAll().get(0).getUsername());
		assertTrue(userDAO.findOneByUsername("admin").isPresent());

		TeamService teamService = createTeamService();
		flushDB();

		//first, check not existent users and teams
		assertFalse(teamService.checkIfUserIsMemberOfTeam(-1l, 10l));
		assertFalse(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), -1l));

		//create a team
		long createdTeamID = createTeam("test", "test1");
		assertFalse(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), createdTeamID));

		flushDB();

		//add user as member of team
		teamService.addUserAsMemberOfTeam(getDefaultUser().getId(), createdTeamID);
		flushDB();

		assertTrue(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), createdTeamID));

		//cleanup
		cleanUp();
	}

	protected TeamService createTeamService() {
		UserHelperService userHelperService = new UserHelperService(userDAO);
		return new TeamService(customerDAO, teamDAO, userHelperService);
	}

	protected long createTeam(String title, String description) {
		//get the default customer
		CustomerEntity customer = customerDAO.findOneByName("super-admin").orElseThrow();

		//add the new team
		TeamService teamService = createTeamService();
		return teamService.addTeam(customer, title, description);
	}

}
