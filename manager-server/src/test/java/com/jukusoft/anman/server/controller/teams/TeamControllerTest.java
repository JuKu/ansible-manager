package com.jukusoft.anman.server.controller.teams;

import com.jukusoft.anman.base.teams.TeamService;
import com.jukusoft.anman.base.utils.UserHelperService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

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
@ActiveProfiles({"test"})
@DirtiesContext(classMode = AFTER_CLASS)
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
		//checkStatusCode("/teams/create-team", new HttpMethod[]{HttpMethod.PUT}, HttpStatus.UNAUTHORIZED);
	}

}
