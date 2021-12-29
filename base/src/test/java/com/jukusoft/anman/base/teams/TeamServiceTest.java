package com.jukusoft.anman.base.teams;

import com.jukusoft.anman.base.entity.general.CustomerEntity;
import com.jukusoft.anman.base.utils.DBTest;
import com.jukusoft.anman.base.utils.UserHelperService;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

/**
 * Test the TeamService.
 *
 * @author Justin Kuenzel
 */
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
class TeamServiceTest extends DBTest {

	@Test
	@Transactional
	void testAddTeam() throws Exception {
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
		long teamID = teamService.addTeam(customer, getDefaultUser(), "new-team-title", "new-team-description");
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

		TeamDetailsDTO teamDetailsDTO = teamService.getTeamDetails(teamID).orElseThrow();
		assertEquals("new-team-title", teamDetailsDTO.title());
		assertEquals("new-team-description", teamDetailsDTO.description());
		assertEquals(getDefaultCustomer().getId(), teamDetailsDTO.customerID());
		assertEquals(getDefaultCustomer().getName(), teamDetailsDTO.customerName());
		assertEquals(1, teamDetailsDTO.memberCount());
		assertTrue(teamDetailsDTO.memberList().contains(getDefaultUser().getUsername()));

		//cleanup
		cleanUp();
	}

	@Test
	@Transactional
	void testListAllTeamsOfNotExistentCustomer() {
		assertThrows(IllegalStateException.class, () -> createTeamService().listAllTeamsOfCustomer(-1));
	}

	@Test
	@Transactional
	void testIfUserIsMemberOfTeam() throws Exception {
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

		//the user should be member of this team, because the owner is always added as team member
		assertTrue(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), createdTeamID));

		flushDB();

		//add user as duplicate member of team
		teamService.addUserAsMemberOfTeam(getDefaultUser().getId(), createdTeamID);
		flushDB();

		assertTrue(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), createdTeamID));

		//delete the user from the team
		teamService.removeUserAsMemberOfTeam(getDefaultUser().getId(), createdTeamID);
		flushDB();

		//the user should not longer be member of this team
		assertFalse(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), createdTeamID));

		//add the user as member of this team again
		teamService.addUserAsMemberOfTeam(getDefaultUser().getId(), createdTeamID);
		flushDB();

		//the user should be member of this team again
		assertTrue(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), createdTeamID));

		//cleanup
		cleanUp();
	}

	/**
	 * this test verifys, that the deletion of teams works as expected
	 */
	@Test
	@Transactional
	void testDeleteTeam() throws Exception {
		//first, import the default customer
		importDefaultCustomer();

		//pre-condition: there should be no team in database
		assertEquals(0, teamDAO.count());

		TeamService teamService = createTeamService();

		//first, create a new team to test
		long teamID = createTeam("test-title1", "test-description1");
		assertTrue(teamDAO.existsById(teamID));
		flushDB();

		assertEquals(1, teamDAO.count());
		assertTrue(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), teamID));

		//create some other teams
		for (int i = 0; i < 10; i++) {
			long id = i + 2;
			long teamID2 = createTeam("test-title" + id, "test-description" + id);
		}

		assertEquals(11, teamDAO.count());

		//delete the first team
		teamService.deleteTeam(getDefaultCustomer(), teamID, true);
		flushDB();

		assertFalse(teamDAO.existsById(teamID));
		flushDB();

		//check, if the team is no longer available
		assertEquals(10, teamDAO.count());
		assertFalse(teamDAO.findOneByName("test-title1").isPresent());

		//delete another random team
		long teamIDToDelete = new Random().ints(1, 2, 12).findFirst().getAsInt();
		assertTrue(teamDAO.findOneByName("test-title" + teamIDToDelete).isPresent());
		TeamEntity team = teamDAO.findOneByName("test-title" + teamIDToDelete).get();
		teamService.deleteTeam(getDefaultCustomer(), team.getId(), false);
		flushDB();
		assertFalse(teamDAO.findOneByName("test-title" + teamIDToDelete).isPresent());

		//check, that all other teams are still available
		for (int i = 0; i < 10; i++) {
			long id = i + 2;

			//check all teams without the deleted one
			if (id != teamIDToDelete) {
				assertTrue(teamDAO.findOneByName("test-title" + id).isPresent());
			}
		}

		//check, that the user is no longer member of the deleted teams
		assertFalse(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), teamID));
		assertFalse(teamService.checkIfUserIsMemberOfTeam(getDefaultUser().getId(), teamIDToDelete));

		teamService.cleanAllCaches();
		cleanUp();
	}

	/**
	 * this test verifies, that the deleteTeam() method does not delete teams from other customers.
	 */
	@Test
	void testDeleteTeamFromOtherCustomer() throws Exception {
		//first, import the default customer
		importDefaultCustomer();

		//pre-condition: there should be no team in database
		assertEquals(0, teamDAO.count());

		//create a new example customer
		CustomerEntity newCustomer = new CustomerEntity("test2");
		newCustomer = customerDAO.save(newCustomer);

		TeamService teamService = createTeamService();

		long teamID = teamService.addTeam(newCustomer, getDefaultUser(), "test", "test1");
		flushDB();

		assertTrue(teamDAO.existsById(teamID));

		//delete team from other customer
		AtomicBoolean b = new AtomicBoolean(false);

		//check, that the exception is thrown
		try {
			teamService.deleteTeam(getDefaultCustomer(), teamID, true);
		} catch (IllegalStateException e) {
			b.set(true);
		}

		assertTrue(b.get());
		flushDB();

		//the team should still exists, because it belongs to another customer
		assertTrue(teamDAO.existsById(teamID));

		cleanUp();
	}

	protected TeamService createTeamService() {
		UserHelperService userHelperService = new UserHelperService(userDAO);
		return new TeamService(customerDAO, userDAO, teamDAO, userHelperService);
	}

	protected long createTeam(String title, String description) {
		//get the default customer
		CustomerEntity customer = getDefaultCustomer();

		//add the new team
		TeamService teamService = createTeamService();
		return teamService.addTeam(customer, getDefaultUser(), title, description);
	}

}
