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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

/**
 * Test the TeamService.
 *
 * @author Justin Kuenzel
 */
public class TeamServiceTest extends DBTest {

	@Test
	public void testAddTeam() throws Exception {
		assertEquals(0, teamDAO.count());

		UserHelperService userHelperService = new UserHelperService(userDAO);

		//first, import the default customer
		importDefaultCustomer();

		//check, that one customer exists
		assertEquals(1, customerDAO.count());

		//get the default customer
		CustomerEntity customer = customerDAO.findOneByName("super-admin").orElseThrow();

		//verify, that no other team exists
		assertEquals(0, teamDAO.count());

		//add the new team
		TeamService teamService = new TeamService(customerDAO, teamDAO, userHelperService);
		long teamID = teamService.addTeam(customer, "new-team-title", "new-team-description");
		assertTrue(teamID > 0);

		//check, that team was created
		assertEquals(1, teamDAO.count());

		//check customer
		assertEquals(1, teamDAO.findOneByName("new-team-title").get().getCustomer().getId());

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

}
