package com.jukusoft.anman.server.controller.teams;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.net.http.HttpResponse;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_CLASS;

/**
 * this helper class allows tests for the RestControllers.
 * NOTE: this starts a full web server so this tests take some time.
 * Do not use it, if you don't need it.
 *
 * @author Justin Kuenzel
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test"})
@DirtiesContext(classMode = AFTER_CLASS)
public abstract class WebTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected TestRestTemplate restTemplate;

	/**
	 * this method checks, if a specific endpoint url returns the correct http status code.
	 * This is e.q. useful, if you want check, that endpoints are not public accessable.
	 *
	 * @param url the endpoint url, e.q. "/teams/list-customer-teams"
	 * @param methods an array with all http methods to check
	 * @param expectedStatusCode the expected http status code
	 */
	protected void checkStatusCode(String url, HttpMethod[] methods, HttpStatus expectedStatusCode) {
		//create the required HttpEntity
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("X-COM-PERSIST", "NO");
		headers.set("X-COM-LOCATION", "USA");
		HttpEntity<String> entity = new HttpEntity<String>(headers);

		for (HttpMethod method : methods) {
			ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class);

			//execute the checks
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		}
	}

}
