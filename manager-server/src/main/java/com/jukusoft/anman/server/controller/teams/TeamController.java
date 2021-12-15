package com.jukusoft.anman.server.controller.teams;

import com.jukusoft.anman.base.teams.TeamDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This REST controller is responsible for all teams endpoints.
 *
 * @author Justin Kuenzel
 */
@RestController("/teams")
public class TeamController {

	/**
	 * return a list with all teams which the user belongs to.
	 *
	 * @return list with own teams
	 */
	@GetMapping(path = "/teams/list-own-teams")
	public ResponseEntity<List<TeamDTO>> listOwnTeams() {
		//TODO: check permissions

		return null;
	}

	/**
	 * return a list with all teams which are available for this customer.
	 * The permission "customer.can.see.all.teams" is required to use this endpoint.
	 *
	 * @return list with all teams of the customer
	 */
	@GetMapping(path = "/teams/list-customer-teams")
	public ResponseEntity<List<TeamDTO>> listAllTeams() {
		//TODO: check permissions

		return null;
	}

}
