package com.jukusoft.anman.server.controller.teams;

import com.jukusoft.anman.base.teams.TeamDTO;
import com.jukusoft.anman.base.teams.TeamService;
import com.jukusoft.anman.base.utils.UserHelperService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

/**
 * This test checks the TeamController.
 * See also: https://spring.io/guides/gs/testing-web/ .
 *
 * @author Justin Kuenzel
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TeamControllerTest {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	private TeamController controller;

	/**
	 * checks the constructor (that no exception is thrown).
	 */
	@Test
	void testConstructor() {
		TeamController controller = new TeamController(Mockito.mock(TeamService.class), Mockito.mock(UserHelperService.class));
		assertNotNull(controller);
	}

	/**
	 * check, that the controller can be initialized from spring.
	 *
	 * @throws Exception
	 */
	@Test
	public void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

	/**
	 * this test executes a real http request to check the API to get the list of own teams.
	 */
	@Test
	public void testShowListOwnTeamsShouldBeEmpty() {
		assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/teams/list-own-teams",
				ResponseEntity.class).getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
	}

}
