package com.jukusoft.anman.server.utils;

import com.jukusoft.anman.base.teams.TeamDTO;
import com.jukusoft.authentification.jwt.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * this test class checks some security issues.s
 *
 * @author Justin Kuenzel
 */
@ActiveProfiles({"test", "user-creation-importer"})
public class SecurityTest extends WebTest {

	@Value("${jwt.secret}")
	private String secret;

	/**
	 * this test verifys, that wrong credentials leads to a "credentials wrong" message without a JWT token.
	 */
	@Test
	void testLoginNotExistentUser() {
		String jwtToken = login("not-existent-user", "admin", false).orElse(null);
		assertThat(jwtToken).isEqualTo("Credentials wrong");
	}

	/**
	 * this test verifys, that the login works correctly and returns valide JWT token.
	 */
	@Test
	void testLogin() {
		String jwtToken = login("admin", "admin", true).orElse(null);
		assertThat(jwtToken).isNotNull();

		//check, that the response is a json string
		assertThat(jwtToken.startsWith("{")).isTrue();
		assertThat(jwtToken.endsWith("}")).isTrue();

		JSONObject json = new JSONObject(jwtToken);
		assertThat(json.has("token")).isTrue();
		assertThat(json.getString("token").length()).isGreaterThan(0);

		//verify JWT token
		Claims claims = Jwts.parser()
				.setSigningKey(secret)
				.parseClaimsJws(json.getString("token")).getBody();

		assertThat(Long.parseLong(claims.getId())).isGreaterThan(0);
		assertThat(claims.getSubject()).isEqualTo("admin");

		//check, that the JWT token is not expired yet
		assertThat(claims.getExpiration().after(new Date())).isTrue();
	}

	@Test
	void testUrlsAreNotAccessablePublic(){
		//add all API urls here
		checkStatusCode("/teams/list-own-teams", new HttpMethod[]{HttpMethod.GET}, HttpStatus.UNAUTHORIZED);
		checkStatusCode("/teams/list-customer-teams", new HttpMethod[]{HttpMethod.GET}, HttpStatus.UNAUTHORIZED);
		checkStatusCode("/teams/create-team", new HttpMethod[]{HttpMethod.PUT}, HttpStatus.UNAUTHORIZED, new TeamDTO(0, null, null));
	}

}
