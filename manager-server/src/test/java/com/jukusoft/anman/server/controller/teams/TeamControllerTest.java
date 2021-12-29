package com.jukusoft.anman.server.controller.teams;

import com.jukusoft.anman.base.dao.CustomerDAO;
import com.jukusoft.anman.base.dao.UserDAO;
import com.jukusoft.anman.base.entity.user.UserEntity;
import com.jukusoft.anman.base.teams.TeamDAO;
import com.jukusoft.anman.base.teams.TeamDTO;
import com.jukusoft.anman.base.teams.TeamDetailsDTO;
import com.jukusoft.anman.base.teams.TeamService;
import com.jukusoft.anman.base.utils.UserHelperService;
import com.jukusoft.anman.server.controller.SuccessResponseDTO;
import com.jukusoft.anman.server.utils.WebTest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

/**
 * This test checks the TeamController.
 * See also: https://spring.io/guides/gs/testing-web/ .
 *
 * @author Justin Kuenzel
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@WebMvcTest(TeamController.class)
@ActiveProfiles({"test", "user-creation-importer"})
@DirtiesContext(classMode = AFTER_CLASS)
@Transactional
class TeamControllerTest extends WebTest {

	/*@Autowired
	private MockMvc mockMvc;*/

	/*@MockBean
	private TeamService service;

	@MockBean
	private UserHelperService userHelperService;

	@MockBean
	private SessionService sessionService;

	@MockBean
	private JWTWebSecurityConfig jwtWebSecurityConfig;

	@Autowired
	private WebSecurityConfig webSecurityConfig;*/

	@Autowired
	private TeamController controller;

	@Autowired
	private TeamDAO teamDAO;

	@Autowired
	protected CustomerDAO customerDAO;

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private TeamService teamService;

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
	void contextLoads() throws Exception {
		assertThat(controller).isNotNull();
	}

	/**
	 * this test executes a real http request to check the API to get the list of own teams.
	 */
	@Test
	void testEndpointsAreNotAccessableWithoutLogin() throws Exception {
		/*Mockito.when(service.listAllTeamsOfCustomer(ArgumentMatchers.anyLong())).thenReturn(null);

		//perform http request, see also: https://spring.io/guides/gs/testing-web/ .
		mockMvc.perform(MockMvcRequestBuilders.get("/teams/list-own-teams")).andDo(print()).andExpect(status().isOk());*/

		String requestUrl = "http://localhost:" + port + "/teams/list-own-teams";

		//verify, that json is returned
		String res = this.restTemplate.getForObject(requestUrl,
				String.class);
		assertThat(res).isNotNull();

		//execute the same request again, but return it as ResponseEntity, so that we can check http status code.
		/*ResponseEntity<String> response = restTemplate.getForEntity(requestUrl, String.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);*/

		checkStatusCode(requestUrl, new HttpMethod[]{HttpMethod.GET}, HttpStatus.UNAUTHORIZED);

		//check the other endpoints
		checkStatusCode("/teams/list-customer-teams", new HttpMethod[]{HttpMethod.GET}, HttpStatus.UNAUTHORIZED);
		checkStatusCode("/teams/create-team", new HttpMethod[]{HttpMethod.PUT}, HttpStatus.UNAUTHORIZED, new TeamDTO(0, null, null));
	}

	@Test
		//@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	void testListOwnTeamsAsAuthenticatedUser() {
		teamDAO.deleteAll();
		flushDB();

		//verify, that no other team exists
		assertThat(teamDAO.count()).isZero();
		assertThat(userDAO.count()).isEqualTo(1);
		assertThat(userDAO.findAll().stream().findFirst().get().getTeams().size()).isZero();

		//TODO: check why this method call fixes the cache problem
		assertThat(teamService.listTeamsOfUser(userDAO.findAll().stream().findFirst().get().getUserID()).size()).isZero();

		String jwtToken = loginGetJWTToken("admin", "admin", true).orElseThrow();
		ResponseEntity<List> response = executeAuthenticatedRequest("/teams/list-own-teams", HttpMethod.GET, List.class, jwtToken);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		//the list should be empty, because no team was created yet
		assertThat(response.getBody()).isEmpty();
	}

	@Test
	void testCreateTeam() {
		teamDAO.deleteAll();

		//verify, that no other team exists
		assertThat(teamDAO.count()).isZero();
		assertThat(userDAO.count()).isEqualTo(1);
		assertThat(userDAO.findAll().stream().findFirst().get().getTeams().size()).isZero();

		//login
		String jwtToken = loginGetJWTToken("admin", "admin", true).orElseThrow();

		TeamDTO team = new TeamDTO(0, "test-team", "test-description");
		ResponseEntity<String> response = executeAuthenticatedRequest("/teams/create-team", HttpMethod.PUT, String.class, jwtToken, team);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		//verify, that team was created
		assertThat(teamDAO.count()).isEqualTo(1);

		//we have to flush the entity manager in the junit tests, so all entities are fetched new from database
		flushDB();

		//user is added as owner team member, so he should see the team in his own teams list
		assertThat(listOwnTeams(jwtToken)).isNotEmpty();

		//clean up
		teamDAO.deleteAll();
	}

	@Test
	void testListAllCustomerTeams() {
		deleteAllTeams();

		//verify, that no other team exists
		assertThat(teamDAO.count()).isZero();
		assertThat(userDAO.count()).isEqualTo(1);
		assertThat(userDAO.findAll().stream().findFirst().get().getTeams().size()).isZero();

		//login
		String jwtToken = loginGetJWTToken("admin", "admin", true).orElseThrow();
		List<Map<String, String>> customerTeams = listCustomerTeams(jwtToken);
		assertThat(customerTeams).isEmpty();

		//create customer team
		TeamDTO team = new TeamDTO(0, "test-team11", "test-description1");
		ResponseEntity<String> response = executeAuthenticatedRequest("/teams/create-team", HttpMethod.PUT, String.class, jwtToken, team);
		flushDB();

		//check customer teams again
		customerTeams = listCustomerTeams(jwtToken);
		assertThat(customerTeams).isNotEmpty();
		assertThat(customerTeams.size()).isEqualTo(1);
		assertThat(customerTeams.get(0).get("title")).isEqualTo("test-team11");

		//TODO: add check that teams from other customers are hidden

		//clean up
		teamDAO.deleteAll();
		flushDB();

		assertThat(teamDAO.count()).isZero();
	}

	@Transactional(value = Transactional.TxType.REQUIRES_NEW)
	protected void deleteAllTeams() {
		teamDAO.deleteAll();
	}

	@Test
	void testCreateAndDeleteTeam() {
		assertThat(teamDAO.count()).isZero();

		//login
		String jwtToken = loginGetJWTToken("admin", "admin", true).orElseThrow();

		//create customer team
		TeamDTO team = new TeamDTO(0, "test-team11", "test-description1");
		ResponseEntity<String> response = executeAuthenticatedRequest("/teams/create-team", HttpMethod.PUT, String.class, jwtToken, team);
		flushDB();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(teamDAO.count()).isEqualTo(1);

		long teamID = Long.parseLong(response.getBody());

		//delete team again
		response = executeAuthenticatedRequest("/teams/delete-team/" + teamID, HttpMethod.DELETE, String.class, jwtToken, team);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(teamDAO.count()).isZero();

		teamDAO.deleteAll();
		flushDB();
	}

	//@Test
	void testAddTeamMembers() {
		assertThat(teamDAO.count()).isZero();

		//login
		String jwtToken = loginGetJWTToken("admin", "admin", true).orElseThrow();

		//create customer team
		TeamDTO team = new TeamDTO(0, "test-team2", "test-description2");
		ResponseEntity<String> response = executeAuthenticatedRequest("/teams/create-team", HttpMethod.PUT, String.class, jwtToken, team);
		flushDB();

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(teamDAO.count()).isEqualTo(1);

		long teamID = Long.parseLong(response.getBody());

		//first, check the current count of the team members
		ResponseEntity<TeamDetailsDTO> response1 = executeAuthenticatedRequest("/teams/details/" + teamID, HttpMethod.GET, TeamDetailsDTO.class, jwtToken, team);
		assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response1.getBody()).isNotNull();

		TeamDetailsDTO teamDetailsDTO = response1.getBody();
		assertThat(teamDetailsDTO.teamID()).isEqualTo(teamID);
		assertThat(teamDetailsDTO.memberCount()).isEqualTo(1);

		//create a new user
		UserEntity newUser = new UserEntity(getDefaultCustomer(), "new-user", "New-User", "New-User");
		newUser = userDAO.save(newUser);
		long newUserId = newUser.getUserID();

		//add team members
		MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<>();
		requestParams.add("teamID", Long.toString(teamID));
		requestParams.add("memberID", Long.toString(newUserId));
		ResponseEntity<SuccessResponseDTO> response2 = executeAuthenticatedRequestWithRequestData("/teams/add-member" + teamID, HttpMethod.PUT, SuccessResponseDTO.class, jwtToken, requestParams);
		assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response2.getBody().success()).isTrue();

		//delete team again
		response = executeAuthenticatedRequest("/teams/delete-team/" + teamID, HttpMethod.DELETE, String.class, jwtToken, team);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		assertThat(teamDAO.count()).isZero();

		userDAO.deleteById(newUserId);
		teamDAO.deleteAll();
		flushDB();

		assertThat(teamDAO.count()).isZero();
	}

	protected List<TeamDTO> listOwnTeams(String jwtToken) {
		ResponseEntity<List> response = executeAuthenticatedRequest("/teams/list-own-teams", HttpMethod.GET, List.class, jwtToken);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		return response.getBody();
	}

	protected List<Map<String, String>> listCustomerTeams(String jwtToken) {
		ResponseEntity<List> response = executeAuthenticatedRequest("/teams/list-customer-teams", HttpMethod.GET, List.class, jwtToken);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

		return response.getBody();
	}

}
