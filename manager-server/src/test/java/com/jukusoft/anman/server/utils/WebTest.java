package com.jukusoft.anman.server.utils;

import com.jukusoft.authentification.jwt.AuthentificationRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.Optional;

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
@TestPropertySource(properties = {
		"auth.providers=local-database",
})
public abstract class WebTest {

	@LocalServerPort
	protected int port;

	@Autowired
	protected TestRestTemplate restTemplate;

	/**
	 * this method checks, if a specific endpoint url returns the correct http status code.
	 * This is e.q. useful, if you want check, that endpoints are not public accessable.
	 *
	 * @param url                the endpoint url, e.q. "/teams/list-customer-teams"
	 * @param methods            an array with all http methods to check
	 * @param expectedStatusCode the expected http status code
	 */
	protected void checkStatusCode(String url, HttpMethod[] methods, HttpStatus expectedStatusCode, Object... urlVariables) {
		HttpEntity<?> entity = createHttpEntity(null, urlVariables);

		for (HttpMethod method : methods) {
			ResponseEntity<String> response = restTemplate.exchange(url, method, entity, String.class, urlVariables);

			//execute the checks
			assertThat(response.getStatusCode()).isEqualTo(expectedStatusCode);
		}
	}

	protected <T> ResponseEntity<T> executeAuthenticatedRequest(String url, HttpMethod method, Class<T> resClass, String jwtToken, Object... urlVariables) {
		HttpEntity<?> entity = createHttpEntity(jwtToken, urlVariables);
		return restTemplate.exchange(url, method, entity, resClass, urlVariables);
	}

	private HttpEntity createHttpEntity(String jwtToken, Object... urlVariables) {
		//create the required HttpEntity
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		headers.set("X-COM-PERSIST", "NO");
		headers.set("X-COM-LOCATION", "USA");

		if (jwtToken != null) {
			headers.set("Authorization", "Bearer " + jwtToken);
		}

		HttpEntity<?> entity = null;

		//create the HttpEntity with or without parameters
		if (urlVariables.length > 0) {
			entity = new HttpEntity<>(urlVariables[0], headers);
		} else {
			entity = new HttpEntity<>(headers);
		}

		return entity;
	}

	/**
	 * executes a login request and return the JWT token.
	 *
	 * @param username username
	 * @param password password
	 *
	 * @return optional JWT token string
	 */
	protected Optional<String> login(String username, String password, boolean checkLogin) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

		AuthentificationRequest requestBody = new AuthentificationRequest();
		requestBody.setUsername(username);
		requestBody.setPassword(password);

		HttpEntity<AuthentificationRequest> entity = new HttpEntity<>(requestBody, headers);
		ResponseEntity<String> response = restTemplate.exchange("/api/login", HttpMethod.POST, entity, String.class, requestBody);

		if (checkLogin) {
			assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		}

		JSONObject json = new JSONObject(response.getBody());
		return Optional.of(json.getString("token"));
	}

}
